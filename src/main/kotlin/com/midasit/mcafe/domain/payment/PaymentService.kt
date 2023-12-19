package com.midasit.mcafe.domain.payment

import com.midasit.mcafe.domain.gamedata.GameReadyService
import com.midasit.mcafe.domain.member.MemberService
import com.midasit.mcafe.domain.order.OrderRepository
import com.midasit.mcafe.domain.order.dto.OrderDto
import com.midasit.mcafe.domain.payment.dto.PaymentResponse
import com.midasit.mcafe.domain.room.RoomService
import com.midasit.mcafe.infra.component.UChefComponent
import com.midasit.mcafe.infra.exception.ErrorMessage
import com.midasit.mcafe.model.GameType
import com.midasit.mcafe.model.OrderStatus
import com.midasit.mcafe.model.ReadyStatus
import com.midasit.mcafe.model.validate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PaymentService(
    private val paymentRepository: PaymentRepository,
    private val orderRepository: OrderRepository,

    private val memberService: MemberService,
    private val roomService: RoomService,
    private val gameReadyService: GameReadyService,

    private val uChefComponent: UChefComponent
) {
    @Transactional
    fun payOrder(memberSn: Long, roomSn: Long, orderSnList: List<Long>): PaymentResponse.PayOrder {
        val member = memberService.findBySn(memberSn)
        val room = roomService.findBySn(roomSn)
        roomService.checkMemberInRoom(member, room)

        val orderList = orderRepository.findByRoomAndSnInAndStatus(room, orderSnList, OrderStatus.PENDING)
        validate(ErrorMessage.INVALID_ORDER_LIST) { orderList.size == orderSnList.size }

        val orderNo = uChefComponent.payOrder(member.nickname, member.phone, orderList)

        val payment = paymentRepository.save(Payment(member, orderNo))
        orderList.forEach {
            it.attachPayment(payment)
            it.updateOrderStatus(OrderStatus.DONE)
        }

        // 방의 게임 준비 상태 ready 인 사람 모두 삭제
        gameReadyService.deleteGameReadyStatusByRoomAndGameTypeAndReadyStatus(room, GameType.PINBALL, ReadyStatus.READY)

        return PaymentResponse.PayOrder(
            orderNo,
            orderList.map { OrderDto.of(it, uChefComponent.getMenuInfo(it.menuCode)) })
    }
}