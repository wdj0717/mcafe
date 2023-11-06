package com.midasit.mcafe.domain.recentorder

import com.midasit.mcafe.domain.member.Member
import com.midasit.mcafe.domain.order.Order
import com.midasit.mcafe.model.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "recent_order")
class RecentOrder (
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "member_id", nullable = false, foreignKey = ForeignKey(name = "fk_recent_order_member_id"))
        val member: Member,
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "order_id", nullable = false, foreignKey = ForeignKey(name = "fk_recent_order_order_id"))
        val order: Order,
): BaseEntity()