package com.midasit.mcafe.domain.order

import com.midasit.mcafe.domain.member.Member
import com.midasit.mcafe.domain.room.Room
import com.midasit.mcafe.model.BaseEntity
import com.midasit.mcafe.model.OrderStatus
import jakarta.persistence.*

@Entity
@Table(name = "`order`")
class Order(
        @Column(nullable = false)
        val orderKey: String,
        @Column(name = "status")
        @Enumerated(EnumType.STRING)
        val status: OrderStatus,
        @Column(nullable = false)
        val menuCode: String,
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "member_id", nullable = false, foreignKey = ForeignKey(name = "fk_order_member_id"))
        val member: Member,
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "room_id", nullable = false, foreignKey = ForeignKey(name = "fk_order_room_id"))
        val room: Room
) : BaseEntity(){
    @Id
    @Column(name = "order_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null
}