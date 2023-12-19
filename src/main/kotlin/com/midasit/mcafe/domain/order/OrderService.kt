package com.midasit.mcafe.domain.order

import com.midasit.mcafe.domain.gamedata.GameReadyService
import com.midasit.mcafe.domain.member.Member
import com.midasit.mcafe.domain.member.MemberService
import com.midasit.mcafe.domain.order.dto.OrderDto
import com.midasit.mcafe.domain.order.dto.OrderRequest
import com.midasit.mcafe.domain.order.dto.OrderResponse
import com.midasit.mcafe.domain.room.Room
import com.midasit.mcafe.domain.room.RoomService
import com.midasit.mcafe.infra.component.UChefComponent
import com.midasit.mcafe.model.GameType
import com.midasit.mcafe.model.OrderStatus
import com.midasit.mcafe.model.validate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OrderService(
    private val uChefComponent: UChefComponent,
    private val orderRepository: OrderRepository,
    private val roomService: RoomService,
    private val memberService: MemberService,
    private val gameReadyService: GameReadyService
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
        val room = roomService.findBySn(request.roomSn)
        roomService.checkMemberInRoom(member, room)

        // 게임 준비 상태로 변경
        gameReadyService.createGameReady(member, room, GameType.PINBALL)

        val order = findDuplicateOrder(member, room, request.menuCode, request.optionList)
        order?.addQuantity() ?: run {
            val newOrder = getNewOrder(request, member, room)
            return OrderDto.of(orderRepository.save(newOrder), uChefComponent.getMenuInfo(newOrder.menuCode))
        }



        return OrderDto.of(order, uChefComponent.getMenuInfo(order.menuCode))
    }

    @Transactional
    fun updateOrderQuantity(memberSn: Long, orderSn: Long, quantity: Long): Boolean {
        val member = memberService.findBySn(memberSn)
        val order = orderRepository.getOrThrow(orderSn)

        validate { order.member.sn == member.sn }
        order.updateQuantity(quantity)

        return true
    }

    @Transactional
    fun deleteOrder(memberSn: Long, roomSn: Long, orderSn: Long): Boolean {
        val member = memberService.findBySn(memberSn)
        val room = roomService.findBySn(roomSn)
        roomService.checkMemberInRoom(member, room)

        val order = orderRepository.getOrThrow(orderSn)
        validate { room.host.sn == member.sn || order.member.sn == member.sn }

        orderRepository.delete(order)

        return true
    }

    private fun findDuplicateOrder(
        member: Member,
        room: Room,
        menuCode: String,
        optionList: List<Long>
    ): Order? {
        val orderList =
            orderRepository.findByMemberAndRoomAndMenuCodeAndStatus(member, room, menuCode, OrderStatus.PENDING)
        return orderList.find { order ->
            val optionSet = order.orderOptions.map { it.optionValue }.toHashSet()
            optionSet == optionList.toHashSet()
        }
    }

    private fun getNewOrder(
        request: OrderRequest.Create,
        member: Member,
        room: Room
    ): Order {
        val newOrder = Order(OrderStatus.PENDING, request.menuCode, member, room, 1)
        request.optionList.forEach {
            newOrder.addOption(it)
        }
        return newOrder
    }
}