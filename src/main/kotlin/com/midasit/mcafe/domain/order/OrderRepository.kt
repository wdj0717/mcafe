package com.midasit.mcafe.domain.order

import com.midasit.mcafe.domain.member.Member
import com.midasit.mcafe.domain.room.Room
import com.midasit.mcafe.infra.exception.CustomException
import com.midasit.mcafe.infra.exception.ErrorMessage
import com.midasit.mcafe.model.OrderStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.findByIdOrNull

fun OrderRepository.getOrThrow(sn: Long): Order =
    findByIdOrNull(sn) ?: throw CustomException(ErrorMessage.INVALID_REQUEST)

interface OrderRepository : JpaRepository<Order, Long> {
    fun findByMemberAndRoomAndMenuCodeAndStatus(
        member: Member,
        room: Room,
        menuCode: String,
        status: OrderStatus
    ): List<Order>

    fun findByRoomAndStatus(room: Room, status: OrderStatus): List<Order>
}