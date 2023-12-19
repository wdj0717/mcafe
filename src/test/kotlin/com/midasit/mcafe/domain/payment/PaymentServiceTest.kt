package com.midasit.mcafe.domain.payment

import com.midasit.mcafe.domain.member.MemberService
import com.midasit.mcafe.domain.order.OrderRepository
import com.midasit.mcafe.domain.order.dto.MenuInfoDto
import com.midasit.mcafe.domain.room.RoomService
import com.midasit.mcafe.infra.component.UChefComponent
import com.midasit.mcafe.infra.exception.CustomException
import com.midasit.mcafe.model.Member
import com.midasit.mcafe.model.Order
import com.midasit.mcafe.model.Room
import com.midasit.mcafe.model.RoomStatus
import com.midasit.mcafe.model.getRandomLong
import com.midasit.mcafe.model.getRandomSn
import com.midasit.mcafe.model.getRandomString
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

class PaymentServiceTest : BehaviorSpec({
    val paymentRepository = mockk<PaymentRepository>(relaxed = true)
    val orderRepository = mockk<OrderRepository>(relaxed = true)
    val memberService = mockk<MemberService>(relaxed = true)
    val roomService = mockk<RoomService>(relaxed = true)
    val uChefComponent = mockk<UChefComponent>(relaxed = true)

    val paymentService = PaymentService(
        paymentRepository = paymentRepository,
        orderRepository = orderRepository,
        memberService = memberService,
        roomService = roomService,
        uChefComponent = uChefComponent
    )

    given("memberSn, roomSn, orderSnList가 주어지면") {
        val memberSn = getRandomSn()
        val roomSn = getRandomSn()
        val orderSnList = List(3) { getRandomLong(100) }
        val room = Room(roomSn = roomSn, status = RoomStatus.PUBLIC)
        val member = Member(memberSn = memberSn)
        val orderNo = getRandomString(10)
        val menuInfoDto = MenuInfoDto(
            getRandomString(10),
            getRandomString(10),
            getRandomLong(1000),
            getRandomLong(1000),
            arrayListOf()
        )
        every { memberService.findBySn(any()) } answers { member }
        every { roomService.findBySn(any()) } answers { room }
        every {
            orderRepository.findByRoomAndSnInAndStatus(
                any(),
                any(),
                any()
            )
        } answers { orderSnList.map { Order(room = room, orderSn = it) } }
        every { uChefComponent.payOrder(any(), any(), any()) } answers { orderNo }
        every { paymentRepository.save(any()) } answers { Payment(member, orderNo) }
        every { uChefComponent.getMenuInfo(any()) } answers { menuInfoDto }
        When("결제를 요청하면") {
            val result = paymentService.payOrder(memberSn, roomSn, orderSnList)
            Then("결제 정보가 반환된다.") {
                result.orderList.size shouldBe orderSnList.size
                result.orderNo shouldBe orderNo
            }
        }

        When("주문 목록이 비어있으면") {
            then("에러가 발생한다.") {
                shouldThrow<CustomException> {
                    paymentService.payOrder(memberSn, roomSn, emptyList())
                }
            }
        }

        every {
            orderRepository.findByRoomAndSnInAndStatus(
                any(),
                any(),
                any()
            )
        } answers { emptyList() }
        When("입력된 주문과 주문정보가 다를경우엔") {
            then("에러가 발생한다.") {
                shouldThrow<CustomException> {
                    paymentService.payOrder(memberSn, roomSn, orderSnList)
                }
            }
        }
    }
})