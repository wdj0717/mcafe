package com.midasit.mcafe.domain.order

import com.midasit.mcafe.model.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "order_option")
class OrderOption(
        @ManyToOne
        @JoinColumn(name = "order_sn", nullable = false, foreignKey = ForeignKey(name = "fk_order_option_order_sn"))
        val order: Order,
        @Column(name = "option_value")
        val optionValue:String
) : BaseEntity()