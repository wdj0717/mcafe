package com.midasit.mcafe.domain.payment

import com.midasit.mcafe.domain.member.Member
import com.midasit.mcafe.domain.order.Order
import com.midasit.mcafe.model.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "payment")
class Payment(
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "member_id", nullable = false, foreignKey = ForeignKey(name = "fk_payment_member_id"))
        val member: Member,
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "order_id", nullable = false, foreignKey = ForeignKey(name = "fk_payment_order_id"))
        val order: Order,
) : BaseEntity()