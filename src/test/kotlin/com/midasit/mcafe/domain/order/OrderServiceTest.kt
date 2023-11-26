package com.midasit.mcafe.domain.order

import com.midasit.mcafe.domain.member.Member
import com.midasit.mcafe.domain.member.MemberService
import com.midasit.mcafe.domain.order.dto.OrderRequest
import com.midasit.mcafe.domain.room.Room
import com.midasit.mcafe.domain.room.RoomService
import com.midasit.mcafe.infra.component.UChefComponent
import com.midasit.mcafe.model.OrderStatus
import com.midasit.mcafe.model.Role
import com.midasit.mcafe.model.RoomStatus
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.springframework.test.util.ReflectionTestUtils

class OrderServiceTest : BehaviorSpec({
    val uChefComponent = mockk<UChefComponent>(relaxed = true)
    val orderRepository = mockk<OrderRepository>(relaxed = true)
    val roomService = mockk<RoomService>(relaxed = true)
    val memberService = mockk<MemberService>(relaxed = true)
    val orderService = OrderService(uChefComponent, orderRepository, roomService, memberService)

    given("멤버 Sn과 방 Sn, 메뉴코드가 주어지면") {
        val memberSn = 1L
        val roomSn = 1L
        val menuCode = "test"
        val request = OrderRequest.Create(menuCode, roomSn, listOf(1L, 2L, 3L))
        val member = Member("010-1234-1234", "username", "1q2w3e4r5t", "name", Role.USER)
        ReflectionTestUtils.setField(member, "sn", memberSn)
        val room = Room("test", "test", member, RoomStatus.PUBLIC)
        val order = Order(OrderStatus.PENDING, menuCode, member, room)
        request.optionList.forEach {
            order.addOption(it)
        }
        ReflectionTestUtils.setField(room, "sn", roomSn)
        every { memberService.findBySn(any()) } returns member
        every { roomService.findByRoomSn(any()) } returns room
        every { orderRepository.save(any()) } returns order
        When("주문 등록하면") {
            val result = orderService.createOrder(memberSn, request)

            then("주문 등록 결과가 반환된다.") {
                result.memberSn shouldBe memberSn
                result.roomSn shouldBe roomSn
                result.menuCode shouldBe menuCode
                result.optionList shouldBe request.optionList.map { it.toString() }
            }
        }
    }
})