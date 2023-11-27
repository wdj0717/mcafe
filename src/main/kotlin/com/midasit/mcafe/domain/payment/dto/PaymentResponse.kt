package com.midasit.mcafe.domain.payment.dto

import com.midasit.mcafe.domain.order.dto.OrderDto
import io.swagger.v3.oas.annotations.media.Schema

class PaymentResponse {
    @Schema(name = "OrderResponsePay", description = "결제 응답 객체")
    class PayOrder(
        @Schema(description = "주문 번호", required = true)
        val orderNo: String,
        @Schema(description = "결제 정보 리스트", required = true)
        val orderList: List<OrderDto>
    )
}