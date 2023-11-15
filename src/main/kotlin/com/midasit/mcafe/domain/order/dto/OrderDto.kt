package com.midasit.mcafe.domain.order.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(name = "OrderDto", description = "주문 DTO")
data class OrderDto(
    val memberSn: Long,
    val roomSn: Long,
    val menuCode: String
)