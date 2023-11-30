package com.midasit.mcafe.domain.order.dto

import com.midasit.mcafe.domain.order.Order
import io.swagger.v3.oas.annotations.media.Schema

@Schema(name = "OrderDto", description = "주문 DTO")
data class OrderDto(
    val memberSn: Long,
    val memberNickname: String,
    val roomSn: Long,
    val menu: MenuInfoDto,
    val quantity: Long,
    val optionList: List<Long> = listOf()
) {
    companion object {
        fun of(order: Order, menu: MenuInfoDto): OrderDto {
            if(order.orderOptions.isEmpty()) {
                return OrderDto(order.member.sn, order.member.nickname, order.room.sn, menu, order.quantity)
            }
            return OrderDto(order.member.sn, order.member.nickname, order.room.sn, menu, order.quantity, order.orderOptions.map { it.optionValue })
        }
    }
}