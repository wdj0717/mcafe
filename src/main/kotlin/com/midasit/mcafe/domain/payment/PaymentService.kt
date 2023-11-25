package com.midasit.mcafe.domain.payment

import com.midasit.mcafe.domain.member.MemberService
import com.midasit.mcafe.domain.order.OrderRepository
import com.midasit.mcafe.domain.order.dto.OrderDto
import com.midasit.mcafe.domain.room.RoomService
import com.midasit.mcafe.infra.component.UChefComponent
import com.midasit.mcafe.model.OrderStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PaymentService(
    private val paymentRepository: PaymentRepository,
    private val orderRepository: OrderRepository,

    private val memberService: MemberService,
    private val roomService: RoomService,

    private val uChefComponent: UChefComponent
) {
    @Transactional
    fun payOrder(memberSn: Long, roomSn: Long): List<OrderDto> {
        val member = memberService.findBySn(memberSn)
        val room = roomService.findByRoomSn(roomSn)
        roomService.checkMemberInRoom(member, room)

        val orderList = orderRepository.findByRoomAndStatus(room, OrderStatus.PENDING)
        uChefComponent.payOrder(member, orderList)

        val payment = paymentRepository.save(Payment(member))
        orderList.forEach {
            it.setPayment(payment)
            it.setOrderStatus(OrderStatus.DONE)
        }

        return orderList.map { OrderDto.of(it) }
    }
}