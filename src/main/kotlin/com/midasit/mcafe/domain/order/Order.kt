package com.midasit.mcafe.domain.order

import com.midasit.mcafe.domain.member.Member
import com.midasit.mcafe.domain.payment.Payment
import com.midasit.mcafe.domain.room.Room
import com.midasit.mcafe.model.BaseEntity
import com.midasit.mcafe.model.OrderStatus
import jakarta.persistence.*

@Entity
@Table(name = "`order`")
class Order(
    status: OrderStatus,
    @Column(nullable = false)
    val menuCode: String,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_sn", nullable = false, foreignKey = ForeignKey(name = "fk_order_member_sn"))
    val member: Member,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_sn", nullable = false, foreignKey = ForeignKey(name = "fk_order_room_sn"))
    val room: Room,
    quantity: Long
) : BaseEntity() {

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    var status: OrderStatus = status
        private set
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_sn", foreignKey = ForeignKey(name = "fk_order_payment_sn"))
    var payment: Payment? = null
        private set

    @Column(name = "qauntity")
    var quantity: Long = 1
        private set

    @OneToMany(mappedBy = "order", cascade = [CascadeType.ALL], orphanRemoval = true)
    val orderOptions: MutableList<OrderOption> = mutableListOf()

    fun addOption(optionValue: Long) {
        val orderOption = OrderOption(this, optionValue)
        orderOptions.add(orderOption)
    }

    fun addQuantity() {
        this.quantity++
    }

    fun setOrderStatus(orderStatus: OrderStatus) {
        this.status = orderStatus
    }

    fun setPayment(payment: Payment) {
        this.payment = payment
    }
}