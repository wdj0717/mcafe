package com.midasit.mcafe.domain.payment.dto

import io.swagger.v3.oas.annotations.media.Schema

class PaymentRequest {

    @Schema(name = "PaymentRequestPay", description = "결제 요청 객체")
    class PayOrder(
        @Schema(description = "room sn")
        val roomSn: Long,
        @Schema(description = "order sn list")
        val orderSnList: List<Long>
    )
}