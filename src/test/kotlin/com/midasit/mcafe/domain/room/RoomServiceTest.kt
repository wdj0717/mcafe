package com.midasit.mcafe.domain.room

import com.midasit.mcafe.domain.member.Member
import com.midasit.mcafe.domain.member.MemberService
import com.midasit.mcafe.domain.room.dto.RoomRequest
import com.midasit.mcafe.model.Role
import com.midasit.mcafe.model.RoomStatus
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.springframework.test.util.ReflectionTestUtils

class RoomServiceTest : BehaviorSpec({
    val roomRepository = mockk<RoomRepository>(relaxed = true)
    val memberService = mockk<MemberService>(relaxed = true)
    val roomService = RoomService(roomRepository, memberService)

    given("방 정보가 주어졌을 때") {
        val request = RoomRequest.Create("test", "test", RoomStatus.PUBLIC)
        val memberSn = 1L
        val member = Member(phone = "010-1234-1234", username = "username", password = "1q2w3e4r5t", nickName = "name", role = Role.USER)
        val room = Room("test", "test", member, RoomStatus.PUBLIC)
        ReflectionTestUtils.setField(room, "sn", 1L)
        every { roomRepository.findByName(any()) } returns null
        every { memberService.findBySn(any()) } returns member
        every { roomRepository.save(any()) } returns room
        `when`("방을 생성한다.") {
            val result = roomService.createRoom(request, memberSn)
            then("방이 생성된다.") {
                result.name shouldBe request.name
            }
        }

        `when`("방을 생성할 때 이미 존재하는 방 이름이 있으면") {
            every { roomRepository.findByName(any()) } returns room
            val exception = shouldThrow<Exception> {
                roomService.createRoom(request, memberSn)
            }
            then("방이 생성되지 않는다.") {
                exception.message shouldBe "이미 존재하는 방 이름입니다."
            }
        }
    }
})