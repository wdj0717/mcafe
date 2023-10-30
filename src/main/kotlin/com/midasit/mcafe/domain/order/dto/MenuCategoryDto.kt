package com.midasit.mcafe.domain.order.dto

data class MenuCategoryDto(
        val name: String,
        val menuList: ArrayList<MenuDto>
)