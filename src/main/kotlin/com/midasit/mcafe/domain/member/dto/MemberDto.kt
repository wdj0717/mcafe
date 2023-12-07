package com.midasit.mcafe.domain.member.dto

import com.midasit.mcafe.domain.member.Member
import com.midasit.mcafe.model.Role
import io.swagger.v3.oas.annotations.media.Schema

@Schema(name = "MemberDto", description = "회원 정보")
data class MemberDto(val nickname: String,
                     val username: String,
                     val phone: String,
                     val role: Role) {

    companion object {
        fun of(member: Member): MemberDto {
            return MemberDto(member.nickname, member.username, member.phone, member.role)
        }
    }
}
