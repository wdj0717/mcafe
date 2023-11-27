package com.midasit.mcafe.domain.payment

import com.midasit.mcafe.domain.payment.dto.PaymentRequest
import com.midasit.mcafe.domain.payment.dto.PaymentResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@Tag(name = "결제 컨트롤러")
@RequestMapping("/payment")
class PaymentController(private val paymentService: PaymentService) {

    @PostMapping
    @Operation(summary = "음료 주문", description = "주문을 취합하여 음료를 주문합니다.")
    fun payOrder(
        authentication: Authentication,
        @RequestBody rq: PaymentRequest.PayOrder
    ): PaymentResponse.PayOrder {
        val memberSn = getMemberSn(authentication)
        return paymentService.payOrder(memberSn, rq.roomSn)
    }

    private fun getMemberSn(authentication: Authentication): Long {
        return (authentication.principal as Long)
    }
}
