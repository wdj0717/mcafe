package com.midasit.mcafe.domain.order.dto

import io.swagger.v3.oas.annotations.media.Schema

class OrderRequest {

    @Schema(name = "OrderRequestCreate", description = "주문 생성 요청 객체")
    class Create(
        @Schema(description = "메뉴 코드")
        val menuCode: String,
        @Schema(description = "옵션 목록")
        val optionList: List<Long>
    )
}