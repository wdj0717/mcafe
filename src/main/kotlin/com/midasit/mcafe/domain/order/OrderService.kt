package com.midasit.mcafe.domain.order

import com.midasit.mcafe.domain.member.Member
import com.midasit.mcafe.domain.member.MemberService
import com.midasit.mcafe.domain.order.dto.OrderDto
import com.midasit.mcafe.domain.order.dto.OrderRequest
import com.midasit.mcafe.domain.order.dto.OrderResponse
import com.midasit.mcafe.domain.room.Room
import com.midasit.mcafe.domain.room.RoomService
import com.midasit.mcafe.infra.component.UChefComponent
import com.midasit.mcafe.infra.exception.CustomException
import com.midasit.mcafe.infra.exception.ErrorMessage
import com.midasit.mcafe.model.OrderStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

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

    fun getMenuInfo(menuCode: String): OrderResponse.GetMenuInfo {
        return OrderResponse.GetMenuInfo(uChefComponent.getMenuInfo(menuCode))
    }

    @Transactional
    fun createOrder(memberSn: Long, request: OrderRequest.Create): OrderDto {
        val member = memberService.findBySn(memberSn)
        val room = roomService.findByRoomSn(request.roomSn)
        roomService.checkMemberInRoom(member, room)

        val order = findDuplicateOrder(member, room, request.menuCode, request.optionList)
        order?.addQuantity() ?: run {
            val newOrder = Order(OrderStatus.PENDING, request.menuCode, member, room, 1)
            request.optionList.forEach {
                newOrder.addOption(it)
            }
            return OrderDto.of(orderRepository.save(newOrder), uChefComponent.getMenuInfo(newOrder.menuCode))
        }

        return OrderDto.of(order, uChefComponent.getMenuInfo(order.menuCode))
    }

    @Transactional
    fun updateOrderQuantity(memberSn: Long, orderSn: Long, quantity: Long): Boolean {
        val member = memberService.findBySn(memberSn)
        val order = findBySn(orderSn)

        require(order.member.sn == member.sn) { throw CustomException(ErrorMessage.INVALID_REQUEST) }
        order.updateQuantity(quantity)

        return true
    }

    @Transactional
    fun deleteOrder(memberSn: Long, roomSn: Long, orderSn: Long): Boolean {
        val member = memberService.findBySn(memberSn)
        val room = roomService.findByRoomSn(roomSn)
        roomService.checkMemberInRoom(member, room)

        val order = findBySn(orderSn)
        require(room.host.sn == member.sn || order.member.sn == member.sn) { throw CustomException(ErrorMessage.INVALID_REQUEST) }

        orderRepository.delete(order)

        return true
    }

    fun findBySn(orderSn: Long): Order {
        return orderRepository.findBySnAndStatus(orderSn, OrderStatus.PENDING)
            ?: throw CustomException(ErrorMessage.INVALID_REQUEST)
    }

    private fun findDuplicateOrder(member: Member, room: Room, menuCode: String, optionList: List<Long>): Order? {
        val orderList =
            orderRepository.findByMemberAndRoomAndMenuCodeAndStatus(member, room, menuCode, OrderStatus.PENDING)
        return orderList.find { order ->
            val optionSet = order.orderOptions.map { it.optionValue }.toHashSet()
            optionSet == optionList.toHashSet()
        }
    }
}