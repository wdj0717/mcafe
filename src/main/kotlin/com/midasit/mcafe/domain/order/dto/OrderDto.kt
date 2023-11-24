package com.midasit.mcafe.domain.order.dto

import com.midasit.mcafe.domain.order.Order
import io.swagger.v3.oas.annotations.media.Schema

@Schema(name = "OrderDto", description = "주문 DTO")
data class OrderDto(
    val memberSn: Long,
    val memberNickname: String,
    val roomSn: Long,
    val menuCode: String,
    val quantity: Long,
    val optionList: List<String> = listOf()
) {
    companion object {
        fun of(order: Order): OrderDto {
            require(order.member.sn != null) { "주문자 정보가 없습니다." }
            require(order.room.sn != null) { "방 정보가 없습니다." }

            if(order.orderOptions.isEmpty()) {
                return OrderDto(order.member.sn, order.member.nickname, order.room.sn, order.menuCode, order.quantity)
            }
            return OrderDto(order.member.sn, order.member.nickname, order.room.sn, order.menuCode, order.quantity, order.orderOptions.map { it.optionValue })
        }
    }
}