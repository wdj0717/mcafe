package com.midasit.mcafe.domain.member

import com.midasit.mcafe.infra.converter.PasswordConverter
import com.midasit.mcafe.model.BaseEntity
import com.midasit.mcafe.model.Role
import jakarta.persistence.*

@Entity
@Table(name = "member", uniqueConstraints = [UniqueConstraint(columnNames = ["username", "phone"])])
class Member(
    @Column(nullable = false)
    val phone: String,
    @Column(nullable = false)
    val username: String,
    password: String,
    name: String,
    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    val role: Role
) : BaseEntity() {
    @Column(nullable = false)
    @Convert(converter = PasswordConverter::class)
    var password: String = password
        private set

    @Column(nullable = false)
    var name: String = name
        private set

}