package com.midasit.mcafe.domain.order.dto

import io.swagger.v3.oas.annotations.media.Schema

class OrderResponse {

    @Schema(name = "OrderGetMenuList", description = "메뉴 요청 응답 객체")
    data class GetMenuList(
            @Schema(description = "메뉴 리스트", required = true)
            val menuCategoryList: ArrayList<MenuCategoryDto>
    )

}