package com.midasit.mcafe.domain.order.dto

import com.midasit.mcafe.infra.component.rs.uchef.menuinfo.MenuInfo

data class MenuInfoDto(
    val name: String,
    val code: String,
    val price: Long,
    val stock: Long,
    val optionGroupList: List<OptionGroupDto>
) {
    companion object {
        fun of(menuInfo: MenuInfo, optionGroupList: List<OptionGroupDto>): MenuInfoDto =
            MenuInfoDto(
                menuInfo.name,
                menuInfo.code,
                menuInfo.price,
                menuInfo.stock,
                optionGroupList
            )
    }
}