package com.midasit.mcafe.domain.room

import com.midasit.mcafe.domain.member.MemberService
import com.midasit.mcafe.domain.order.OrderRepository
import com.midasit.mcafe.domain.order.dto.MenuInfoDto
import com.midasit.mcafe.domain.room.dto.RoomDto
import com.midasit.mcafe.domain.room.dto.RoomRequest
import com.midasit.mcafe.domain.roommember.RoomMember
import com.midasit.mcafe.domain.roommember.RoomMemberRepository
import com.midasit.mcafe.infra.component.UChefComponent
import com.midasit.mcafe.infra.exception.CustomException
import com.midasit.mcafe.infra.exception.ErrorMessage
import com.midasit.mcafe.model.Member
import com.midasit.mcafe.model.Order
import com.midasit.mcafe.model.Room
import com.midasit.mcafe.model.RoomStatus
import com.midasit.mcafe.model.getRandomLong
import com.midasit.mcafe.model.getRandomOf
import com.midasit.mcafe.model.getRandomSn
import com.midasit.mcafe.model.getRandomString
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.data.repository.findByIdOrNull
import org.springframework.test.util.ReflectionTestUtils

class RoomServiceTest : BehaviorSpec({
    val roomRepository = mockk<RoomRepository>(relaxed = true)
    val memberService = mockk<MemberService>(relaxed = true)
    val orderRepository = mockk<OrderRepository>(relaxed = true)
    val roomMemberRepository = mockk<RoomMemberRepository>(relaxed = true)
    val uChefComponent = mockk<UChefComponent>(relaxed = true)
    val roomService = RoomService(
        roomRepository,
        roomMemberRepository,
        orderRepository,
        memberService,
        uChefComponent
    )

    afterContainer {
        clearAllMocks()
    }

    given("방 정보가 주어졌을 때") {
        val request = RoomRequest.Create(
            name = getRandomString(10),
            status = getRandomOf(RoomStatus.values()),
            password = getRandomString(10)
        )
        val memberSn = getRandomSn()
        val member = Member(memberSn = memberSn)
        val room = Room(
            name = request.name,
            password = request.password,
            host = member,
            status = request.status
        )
        val roomMember = RoomMember(member, room)
        every { roomRepository.existsByNameAndStatusNot(any(), any()) } answers { false }
        every { memberService.findBySn(any()) } answers { member }
        every { roomRepository.save(any()) } answers { room }
        every { roomMemberRepository.save(any()) } answers { roomMember }
        `when`("방을 생성한다.") {
            val result = roomService.createRoom(request, memberSn)
            then("방이 생성된다.") {
                result.name shouldBe request.name
            }
        }

        `when`("방을 생성할 때 이미 존재하는 방 이름이 있으면") {
            every { roomRepository.existsByNameAndStatusNot(any(), any()) } answers { true }
            then("방이 생성되지 않는다.") {
                shouldThrow<CustomException> {
                    roomService.createRoom(request, memberSn)
                }
            }
        }
    }

    given("방 목록을 조회하려고 하면") {
        val roomList = listOf(Room(), Room(), Room())
        every { roomRepository.findAllByStatusNot(any()) } answers { roomList }
        When("방 목록을 조회하면") {
            val result = roomService.getRoomList()
            then("방 목록이 반환된다.") {
                result.size shouldBe roomList.size
            }
        }
    }

    given("방 Sn과 멤버 Sn이 주어지면") {
        val roomSn = getRandomSn()
        val memberSn = getRandomSn()
        val member = Member(memberSn = memberSn)
        val room = Room(roomSn = roomSn, status = RoomStatus.PUBLIC)
        val roomMember = RoomMember(member, room)
        val menuInfoDto = MenuInfoDto(
            getRandomString(10),
            getRandomString(10),
            getRandomLong(1000),
            getRandomLong(1000),
            arrayListOf()
        )
        every { memberService.findBySn(any()) } answers { member }
        every { roomRepository.getOrThrow(any()) } answers { room }
        every { roomMemberRepository.findByRoom(any()) } answers { listOf(roomMember) }
        every { orderRepository.findByRoomAndStatus(any(), any()) } answers { listOf(Order()) }
        every { roomMemberRepository.existsByRoomAndMember(any(), any()) } answers { true }
        every { uChefComponent.getMenuInfo(any()) } answers { menuInfoDto }
        When("방 정보를 조회하면") {
            val result = roomService.getRoomInfo(memberSn, roomSn)
            then("방 정보가 반환된다.") {
                result.room shouldBe RoomDto.of(room)
            }
        }

        every { roomRepository.getOrThrow(any()) } answers { room }
        every { roomMemberRepository.existsByRoomAndMember(any(), any()) } answers { false }
        When("방에 속해있지 않을때 방 정보를 조회하면") {
            val exception = shouldThrow<CustomException> {
                roomService.getRoomInfo(memberSn, roomSn)
            }
            then("에러가 반환된다.") {
                exception.message shouldBe ErrorMessage.INVALID_ROOM_INFO.message
            }
        }

        room.status = RoomStatus.CLOSED
        every { roomRepository.getOrThrow(any()) } answers { room }
        When("방이 닫혔는데, 방 정보를 조회하면") {
            val exception = shouldThrow<CustomException> {
                roomService.getRoomInfo(memberSn, roomSn)
            }
            then("에러가 반환된다.") {
                exception.message shouldBe ErrorMessage.INVALID_ROOM_INFO.message
            }
        }
    }

    given("멤버 Sn이 주어지면") {
        val memberSn = getRandomSn()
        val member = Member(memberSn = memberSn)
        val roomMember = RoomMember(member, Room(status = RoomStatus.PUBLIC))
        every { memberService.findBySn(any()) } answers { member }
        every { roomMemberRepository.findByMember(any()) } answers {
            listOf(
                roomMember,
                RoomMember(member, Room(status = RoomStatus.CLOSED))
            )
        }
        When("입장한 방 목록을 조회하면") {
            val result = roomService.getEnteredRoomList(memberSn)
            then("입장한 방 목록이 반환된다.") {
                result.map { it.sn } shouldBe listOf(roomMember.room.sn)
            }
        }
    }

    given("멤버 Sn과 방 Sn과 패스워드가 주어지면") {
        val memberSn = getRandomSn()
        val roomSn = getRandomSn()
        val password = getRandomString(10)
        val member = Member(memberSn = memberSn)
        val room = Room(roomSn = roomSn, status = RoomStatus.PRIVATE, password = password)
        val roomMember = RoomMember(member, room)
        every { memberService.findBySn(any()) } answers { member }
        every { roomRepository.getOrThrow(any()) } answers { room }
        every { roomMemberRepository.existsByRoomAndMember(any(), any()) } answers { false }
        every { roomMemberRepository.save(any()) } answers { roomMember }
        When("방에 입장하면") {
            val result = roomService.enterRoom(memberSn, roomSn, password)
            then("입장한다.") {
                result shouldBe true
            }
        }

        every { roomRepository.getOrThrow(any()) } answers { room }
        every { roomMemberRepository.existsByRoomAndMember(any(), any()) } answers { true }
        When("이미 방에 입장한 상태에서 방에 입장하면") {
            val exception = shouldThrow<CustomException> {
                roomService.enterRoom(memberSn, roomSn, password)
            }
            then("에러가 반환된다.") {
                exception.message shouldBe ErrorMessage.ALREADY_ENTERED_ROOM.message
            }
        }

        every { roomRepository.getOrThrow(any()) } answers { room }
        every { roomMemberRepository.existsByRoomAndMember(any(), any()) } answers { false }
        When("비밀번호가 틀렸을 때 방에 입장하면") {
            val exception = shouldThrow<CustomException> {
                roomService.enterRoom(memberSn, roomSn, getRandomString(10))
            }
            then("에러가 반환된다.") {
                exception.message shouldBe ErrorMessage.INVALID_ROOM_PASSWORD.message
            }
        }

        room.status = RoomStatus.CLOSED
        every { roomRepository.getOrThrow(any()) } answers { room }
        every { roomMemberRepository.existsByRoomAndMember(any(), any()) } answers { false }
        When("방이 닫혔는데, 방에 입장하면") {
            val exception = shouldThrow<CustomException> {
                roomService.enterRoom(memberSn, roomSn, password)
            }
            then("에러가 반환된다.") {
                exception.message shouldBe ErrorMessage.INVALID_ROOM_INFO.message
            }
        }
    }

    given("방 정보가 주어졌을때") {
        val memberSn = getRandomSn()
        val roomSn = getRandomSn()
        val member = Member(memberSn = memberSn)
        val room = Room(roomSn = roomSn, host = member, status = RoomStatus.PUBLIC)
        every { memberService.findBySn(any()) } answers { member }
        every { roomRepository.getOrThrow(any()) } answers { room }
        When("방 정보를 수정하면") {
            val result = roomService.updateRoom(
                memberSn,
                roomSn,
                getRandomString(10),
                getRandomOf(RoomStatus.values()),
                getRandomString(10)
            )
            then("방 정보가 수정된다.") {
                result shouldBe true
            }
        }

        every { memberService.findBySn(any()) } answers { member }
        every { roomRepository.getOrThrow(any()) } answers { room }
        When("비밀번호 방에 비밀번호가 없이 방 정보를 수정하려고 하면") {
            val exception = shouldThrow<CustomException> {
                roomService.updateRoom(memberSn, roomSn, getRandomString(10), RoomStatus.PRIVATE, null)
            }
            then("에러가 반환된다.") {
                exception.message shouldBe ErrorMessage.INVALID_ROOM_INFO.message
            }
        }

        every { memberService.findBySn(any()) } answers { member }
        every { roomRepository.getOrThrow(any()) } answers { Room() }
        When("호스트가 아닌 방 정보를 수정하려고 하면") {
            val exception = shouldThrow<CustomException> {
                roomService.updateRoom(memberSn, roomSn, getRandomString(10), RoomStatus.PRIVATE, getRandomString(10))
            }
            then("에러가 반환된다.") {
                exception.message shouldBe ErrorMessage.INVALID_ROOM_INFO.message
            }
        }

        every { memberService.findBySn(any()) } answers { member }
        every { roomRepository.getOrThrow(any()) } answers { Room(host = member, status = RoomStatus.CLOSED) }
        When("닫혀있는 방 정보를 수정하면") {
            val exception = shouldThrow<CustomException> {
                roomService.updateRoom(
                    memberSn,
                    roomSn,
                    getRandomString(10),
                    getRandomOf(RoomStatus.values()),
                    getRandomString(10)
                )
            }
            then("에러가 반환된다.") {
                exception.message shouldBe ErrorMessage.INVALID_ROOM_INFO.message
            }
        }
    }

    given("멤버 Sn과 방 Sn이 주어지면") {
        val memberSn = getRandomSn()
        val roomSn = getRandomSn()
        val member = Member(memberSn = memberSn)
        val room = Room(roomSn = roomSn, host = Member(), status = RoomStatus.PUBLIC)
        every { memberService.findBySn(any()) } answers { member }
        every { roomRepository.getOrThrow(any()) } answers { room }
        every { roomMemberRepository.existsByRoomAndMember(any(), any()) } answers { true }
        When("방에서 나가면") {
            val result = roomService.exitRoom(memberSn, roomSn)
            then("방에서 나간다.") {
                result shouldBe true
                verify(exactly = 1) { roomMemberRepository.deleteByRoomAndMember(any(), any()) }
            }
        }

        ReflectionTestUtils.setField(room, "host", member)
        every { memberService.findBySn(any()) } answers { member }
        every { roomRepository.getOrThrow(any()) } answers { room }
        every { roomMemberRepository.existsByRoomAndMember(any(), any()) } answers { true }
        When("방장이 나갈려고 하면") {
            val exception = shouldThrow<CustomException> {
                roomService.exitRoom(memberSn, roomSn)
            }
            then("에러가 반환된다.") {
                exception.message shouldBe ErrorMessage.HOST_CANT_EXIT.message
            }
        }
    }

    given("멤버 Sn과 방 Sn이 주어지면") {
        val memberSn = getRandomSn()
        val roomSn = getRandomSn()
        val member = Member(memberSn = memberSn)
        val room = Room(roomSn = roomSn, host = member, status = RoomStatus.PUBLIC)
        every { memberService.findBySn(any()) } answers { member }
        every { roomRepository.getOrThrow(any()) } answers { room }
        When("방을 삭제하면") {
            val result = roomService.deleteRoom(memberSn, roomSn)
            then("방이 삭제된다.") {
                result shouldBe true
            }
        }

        every { memberService.findBySn(any()) } answers { member }
        every { roomRepository.getOrThrow(any()) } answers { Room() }
        When("호스트가 아닌 방을 삭제하면") {
            val exception = shouldThrow<CustomException> {
                roomService.deleteRoom(memberSn, roomSn)
            }
            then("에러가 반환된다.") {
                exception.message shouldBe ErrorMessage.INVALID_ROOM_INFO.message
            }
        }
    }


    given("방 Sn이 주어졌을 때") {
        val roomSn = getRandomSn()
        val room = Room(roomSn = roomSn)
        every { roomRepository.findByIdOrNull(any()) } answers { room }
        When("방 조회를 하면") {
            val result = roomService.findBySn(roomSn)
            then("방 정보가 반환된다.") {
                result.name shouldBe room.name
            }
        }

        every { roomRepository.findByIdOrNull(any()) } answers { null }
        When("방 조회를 하면") {
            val exception = shouldThrow<CustomException> {
                roomService.findBySn(roomSn)
            }
            then("에러가 반환된다.") {
                exception.message shouldBe ErrorMessage.INVALID_ROOM_INFO.message
            }
        }
    }
})