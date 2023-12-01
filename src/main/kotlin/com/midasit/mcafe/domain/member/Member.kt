package com.midasit.mcafe.domain.member

import com.midasit.mcafe.infra.converter.PasswordConverter
import com.midasit.mcafe.model.BaseEntity
import com.midasit.mcafe.model.Role
import jakarta.persistence.*

@Entity
@Table(name = "member")
class Member(
    @Column(nullable = false, unique = true)
    val phone: String,
    @Column(nullable = false, unique = true)
    val username: String,
    password: String,
    nickname: String,
    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    val role: Role
) : BaseEntity() {
    @Column(nullable = false)
    @Convert(converter = PasswordConverter::class)
    var password: String = password
        protected set

    @Column(nullable = false)
    var nickname: String = nickname
        protected set

}