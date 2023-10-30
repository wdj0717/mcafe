package com.midasit.mcafe.domain.member

import com.midasit.mcafe.infra.converter.PasswordConverter
import com.midasit.mcafe.model.BaseEntity
import com.midasit.mcafe.model.Role
import jakarta.persistence.*

@Entity
@Table(name = "member", uniqueConstraints = [UniqueConstraint(columnNames = ["phone"])])
class Member(
       @Column(nullable = false)
       val phone :String,
       name :String,
       password :String,
       @Column(name = "role")
       @Enumerated(EnumType.STRING)
       val role :Role
) : BaseEntity(){
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

    @Column(nullable = false)
    var name: String = name
        private set

    @Column(nullable = false)
    @Convert(converter = PasswordConverter::class)
    var password: String = password
        private set
}