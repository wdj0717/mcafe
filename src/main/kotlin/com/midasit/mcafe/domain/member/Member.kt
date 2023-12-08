package com.midasit.mcafe.domain.member

import com.midasit.mcafe.infra.converter.PasswordConverter
import com.midasit.mcafe.model.BaseEntity
import com.midasit.mcafe.model.Role
import jakarta.persistence.*

@Entity
@Table(name = "member")
class Member(
    phone: String,
    username: String,
    password: String,
    nickname: String,
    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    val role: Role
) : BaseEntity() {

    @Column(nullable = false, unique = true)
    var phone: String = phone
        protected set
    @Column(nullable = false, unique = true)
    var username: String = username
        protected set

    @Column(nullable = false)
    @Convert(converter = PasswordConverter::class)
    var password: String = password
        protected set

    @Column(nullable = false)
    var nickname: String = nickname
        protected set

    fun updateNickname(nickname: String) {
        this.nickname = nickname
    }

    fun delete() {
        this.username = "UNKNOWN_USER_${this.sn}"
        this.nickname = "UNKNOWN_USER_${this.sn}"
        this.phone = "UNKNOWN_USER_${this.sn}"
    }

    fun updatePassword(password: String) {
        this.password = password
    }
}
