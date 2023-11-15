package com.midasit.mcafe.domain.order.dto

data class MenuInfoDto(
    val name: String,
    val code: String,
    val price: Long,
    val stock: Long,
    val optionGroupList: ArrayList<OptionGroupDto>
)