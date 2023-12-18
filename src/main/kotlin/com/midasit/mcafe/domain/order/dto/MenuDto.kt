package com.midasit.mcafe.domain.order.dto

import com.midasit.mcafe.infra.component.rs.uchef.menu.OrderButtonComp

class MenuDto(
    val name: String,
    val code: String,
    val price: Long,
    val unit: String,
    val stock: Long
) {
    companion object {
        fun from(orderButtonComp: OrderButtonComp): MenuDto =
            MenuDto(
                orderButtonComp.name,
                orderButtonComp.code,
                orderButtonComp.price,
                orderButtonComp.unit,
                orderButtonComp.stock
            )
    }
}