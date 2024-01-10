package com.midasit.mcafe.infra.component.rq.uchef.payorder

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.midasit.mcafe.domain.order.Order
import com.midasit.mcafe.domain.order.OrderOption
import com.midasit.mcafe.domain.order.dto.MenuInfoDto
import com.midasit.mcafe.infra.exception.CustomException
import com.midasit.mcafe.infra.exception.ErrorMessage

class OrderRq(
    @JsonProperty("menu_code")
    val menuCode: String,
    @JsonProperty("menu_name")
    val menuName: String,
    @JsonProperty("unit_price")
    val unitPrice: Long,
    @JsonProperty("menu_qty")
    var menuQty: Long,
    @JsonProperty("memo")
    val memo: String = "",
    @JsonProperty("optionlist")
    val optionList: List<List<Any>>,
    @JsonIgnore
    val couponAmount: Long
) {
    fun addMenuQty() {
        this.menuQty++
    }
    companion object {
        fun of(order: Order, menuInfoDto: MenuInfoDto): OrderRq {
            var couponAmount = menuInfoDto.price
            val optionList = order.orderOptions.map {
                val add = addMenuOption(menuInfoDto, it)
                couponAmount += add
                listOf(it.optionValue, 1, 1, "")
            }

            return OrderRq(
                menuInfoDto.code,
                menuInfoDto.name,
                menuInfoDto.price,
                order.quantity,
                "",
                optionList,
                couponAmount
            )
        }

        private fun addMenuOption(menuInfoDto: MenuInfoDto, orderOption: OrderOption): Long {
            return menuInfoDto.optionGroupList
                .flatMap { it.optionList }
                .firstOrNull { it.code == orderOption.optionValue }
                ?.price ?: throw CustomException(ErrorMessage.INTERNAL_SERVER_ERROR)
        }
    }
}