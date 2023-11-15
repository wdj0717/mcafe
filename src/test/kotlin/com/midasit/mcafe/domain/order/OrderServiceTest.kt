package com.midasit.mcafe.domain.order

import com.midasit.mcafe.infra.component.UChefComponent
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.mockk

class OrderServiceTest : BehaviorSpec({
    val uChefComponent = mockk<UChefComponent>(relaxed = true)
    val orderService = OrderService(uChefComponent)

    given("멤버 Sn과 방 Sn, 메뉴코드가 주어지면") {
        val memberSn = 1L
        val roomSn = 1L
        val menuCode = "test"

        When("주문 등록하면") {
            val result = orderService.createOrder(memberSn, roomSn, menuCode)

            then("주문 등록 결과가 반환된다.") {
                result.memberSn shouldBe memberSn
                result.roomSn shouldBe roomSn
                result.menuCode shouldBe menuCode
            }
        }
    }
})