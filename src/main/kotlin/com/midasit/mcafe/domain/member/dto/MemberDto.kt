package com.midasit.mcafe.domain.member.dto

import com.midasit.mcafe.domain.member.Member
import com.midasit.mcafe.model.Role
import io.swagger.v3.oas.annotations.media.Schema

@Schema(name = "MemberDto", description = "회원 정보")
data class MemberDto(var nickname: String, var phone: String, var role: Role) {

    companion object {
        fun of(member: Member): MemberDto {
            return MemberDto(member.nickname, member.phone, member.role)
        }
    }
}
