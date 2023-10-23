package com.midasit.mcafe.domain.order

import com.midasit.mcafe.model.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "order_option")
class OrderOption(
        @ManyToOne
        @JoinColumn(name = "order_id", nullable = false, foreignKey = ForeignKey(name = "fk_order_option_order_id"))
        val order: Order,
        @Column(name = "option_value")
        val optionValue:String
) : BaseEntity(){
    @Id
    @Column(name = "order_option_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null
}