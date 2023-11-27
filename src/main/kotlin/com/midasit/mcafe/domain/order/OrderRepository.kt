package com.midasit.mcafe.domain.order

import com.midasit.mcafe.domain.member.Member
import com.midasit.mcafe.domain.room.Room
import com.midasit.mcafe.model.OrderStatus
import org.springframework.data.jpa.repository.JpaRepository

interface OrderRepository : JpaRepository<Order, Long> {
    fun findByMemberAndRoomAndMenuCodeAndStatus(member: Member, room: Room, menuCode: String, status: OrderStatus): List<Order>
    fun findByRoomAndStatus(room: Room, status: OrderStatus): List<Order>
    fun findBySnAndStatus(orderSn: Long, status: OrderStatus): Order?
}