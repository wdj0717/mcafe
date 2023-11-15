package com.midasit.mcafe.domain.recentorder

import com.midasit.mcafe.domain.member.Member
import com.midasit.mcafe.domain.order.Order
import com.midasit.mcafe.model.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "recent_order")
class RecentOrder(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_sn", nullable = false, foreignKey = ForeignKey(name = "fk_recent_order_member_sn"))
    val member: Member,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_sn", nullable = false, foreignKey = ForeignKey(name = "fk_recent_order_order_sn"))
    val order: Order,
) : BaseEntity()