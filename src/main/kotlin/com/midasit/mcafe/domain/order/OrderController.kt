package com.midasit.mcafe.domain.order

import com.midasit.mcafe.domain.order.dto.OrderRequest
import com.midasit.mcafe.domain.order.dto.OrderResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@Tag(name = "주문 컨트롤러")
@RequestMapping("/order")
class OrderController(
    private val orderService: OrderService
) {
    @GetMapping("/menu")
    @Operation(summary = "메뉴 목록 조회", description = "메뉴 목록을 조회합니다.")
    fun getMenuList(): OrderResponse.GetMenuList {
        return orderService.getMenuList()
    }

    @GetMapping("/menu/{menuCode}")
    @Operation(summary = "메뉴 정보 조회", description = "메뉴 정보를 조회합니다.")
    fun getMenuInfo(@PathVariable menuCode: String): OrderResponse.GetMenuInfo {
        return orderService.getMenuInfo(menuCode)
    }

    @PostMapping
    @Operation(summary = "주문 생성", description = "주문을 생성합니다.")
    fun createOrder(
        @RequestBody request: OrderRequest.Create,
        authentication: Authentication
    ): OrderResponse.Create {
        val memberSn = getMemberSn(authentication)
        return OrderResponse.Create(orderService.createOrder(memberSn, request))
    }

    @GetMapping
    @Operation(summary = "주문 목록 조회", description = "주문 목록을 조회합니다.")
    fun getOrderList(
        authentication: Authentication,
        @RequestParam(value="roomSn") roomSn: Long
    ): OrderResponse.GetOrderList {
        val memberSn = getMemberSn(authentication)
        return OrderResponse.GetOrderList(orderService.getOrderList(memberSn, roomSn))
    }

    @PatchMapping("/{orderSn}")
    @Operation(summary = "주문 수량 변경", description = "주문 수량을 변경합니다.")
    fun updateOrderQuantity(
        authentication: Authentication,
        @RequestBody rq: OrderRequest.UpdateQuantity,
        @PathVariable orderSn: Long
    ): Boolean {
        val memberSn = getMemberSn(authentication)
        return orderService.updateOrderQuantity(memberSn, orderSn, rq.quantity)
    }

    @DeleteMapping("/{orderSn}")
    @Operation(summary = "주문 삭제", description = "주문을 삭제합니다.")
    fun deleteOrder(
        authentication: Authentication,
        @RequestParam(value="roomSn") roomSn: Long,
        @PathVariable(value = "orderSn") orderSn: Long
    ): Boolean {
        val memberSn = getMemberSn(authentication)
        return orderService.deleteOrder(memberSn, roomSn, orderSn)
    }

    private fun getMemberSn(authentication: Authentication): Long {
        return (authentication.principal as Long)
    }
}