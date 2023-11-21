package com.midasit.mcafe.domain.order

import com.midasit.mcafe.domain.member.MemberService
import com.midasit.mcafe.domain.order.dto.OrderDto
import com.midasit.mcafe.domain.order.dto.OrderRequest
import com.midasit.mcafe.domain.order.dto.OrderResponse
import com.midasit.mcafe.domain.room.RoomService
import com.midasit.mcafe.infra.component.UChefComponent
import com.midasit.mcafe.model.OrderStatus
import org.springframework.stereotype.Service

@Service
class OrderService(
    private val uChefComponent: UChefComponent,
    private val orderRepository: OrderRepository,
    private val roomService: RoomService,
    private val memberService: MemberService
) {

    fun getMenuList(): OrderResponse.GetMenuList {
        return OrderResponse.GetMenuList(uChefComponent.getMenuList())
    }

    fun getMenuInfo(menuCode: Long): OrderResponse.GetMenuInfo {
        return OrderResponse.GetMenuInfo(uChefComponent.getMenuInfo(menuCode))
    }

    fun createOrder(memberSn: Long, roomSn: Long, request: OrderRequest.Create): OrderDto {

        val member = memberService.findBySn(memberSn)
        val room = roomService.findRoomSn(roomSn)
        val order = Order(OrderStatus.PENDING, request.menuCode, member, room)
        request.optionList.forEach {
            order.addOption(it)
        }
        return OrderDto.of(orderRepository.save(order))
    }
}