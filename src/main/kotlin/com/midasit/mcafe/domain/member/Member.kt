package com.midasit.mcafe.domain.member

import com.midasit.mcafe.model.BaseEntity
import com.midasit.mcafe.model.Role
import jakarta.persistence.*

@Entity
@Table(name = "member")
class Member(
       @Column(nullable = false)
       val phone :String,
       @Column(nullable = false)
       val name :String,
       @Column(nullable = false)
       val password :String,
       @Column(name = "role")
       @Enumerated(EnumType.STRING)
       val role :Role
) : BaseEntity(){
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null
}