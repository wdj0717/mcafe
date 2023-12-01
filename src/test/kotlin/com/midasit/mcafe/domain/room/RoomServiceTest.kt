package com.midasit.mcafe.domain.room

import com.midasit.mcafe.domain.member.Member
import com.midasit.mcafe.domain.member.MemberService
import com.midasit.mcafe.domain.order.OrderRepository
import com.midasit.mcafe.domain.room.dto.RoomRequest
import com.midasit.mcafe.domain.roommember.RoomMember
import com.midasit.mcafe.domain.roommember.RoomMemberRepository
import com.midasit.mcafe.infra.component.UChefComponent
import com.midasit.mcafe.infra.exception.CustomException
import com.midasit.mcafe.model.Role
import com.midasit.mcafe.model.RoomStatus
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.extensions.spring.SpringTestExtension
import io.kotest.extensions.spring.SpringTestLifecycleMode
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import org.springframework.test.util.ReflectionTestUtils
import java.util.*

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
        val request = RoomRequest.Create("test",  RoomStatus.PUBLIC, "test")
        val memberSn = 1L
        val member = Member(
            phone = "010-1234-1234",
            username = "username",
            password = "1q2w3e4r5t",
            nickname = "name",
            role = Role.USER
        )
        val room = Room("test", "test", member, RoomStatus.PUBLIC)
        val roomMember = RoomMember(member, room)
        ReflectionTestUtils.setField(room, "sn", 1L)
        every { roomRepository.existsByName(any()) } answers { false }
        every { memberService.findBySn(any()) } returns member
        every { roomRepository.save(any()) } returns room
        every { roomMemberRepository.save(any()) } answers { roomMember }
        `when`("방을 생성한다.") {
            val result = roomService.createRoom(request, memberSn)
            then("방이 생성된다.") {
                result.name shouldBe request.name
            }
        }

        `when`("방을 생성할 때 이미 존재하는 방 이름이 있으면") {
            every { roomRepository.existsByName(any()) } answers { true }
            then("방이 생성되지 않는다.") {
               shouldThrow<CustomException> {
                    roomService.createRoom(request, memberSn)
                }
            }
        }
    }

    given("방 Sn이 주어졌을 때") {
        val roomSn = 1L
        val room = Room(
            "test", "test", Member(
                "010-1234-1234", "username", "1q2w3e4r5t", "name", Role.USER
            ), RoomStatus.PUBLIC
        )
        every { roomRepository.findById(any()) } returns Optional.of(room)
        When("방 조회를 하면") {
            val result = roomService.findByRoomSn(roomSn)
            then("방 정보가 반환된다.") {
                result.name shouldBe room.name
            }
        }

        every { roomRepository.findById(any()) } returns Optional.empty()
        When("방 조회를 하면") {
            val exception = shouldThrow<Exception> {
                roomService.findByRoomSn(roomSn)
            }
            then("에러가 반환된다.") {
                exception.message shouldBe "존재하지 않는 방입니다."
            }
        }
    }
})