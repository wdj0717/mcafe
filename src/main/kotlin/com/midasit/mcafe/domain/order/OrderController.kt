package com.midasit.mcafe.domain.order

import com.midasit.mcafe.domain.order.dto.OrderResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@Tag(name = "주문 컨트롤러")
class OrderController(
        private val orderService: OrderService
) {
    @GetMapping("/menu")
    @Operation(summary = "메뉴 목록 조회", description = "메뉴 목록을 조회합니다.")
    fun getMenuList(): OrderResponse.GetMenuList {
        return orderService.getMenuList()
    }
}