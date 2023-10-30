package com.midasit.mcafe.domain.member.dto

import io.swagger.v3.oas.annotations.media.Schema

class MemberResponse {

    @Schema(description = "유저 정보", name = "MemberResponseResult")
    class Result(val name: String, val phone: String) {
        companion object {
            fun of(memberDto: MemberDto): Result {
                return Result(memberDto.name, memberDto.phone)
            }
        }
    }

    @Schema(description = "로그인 유저 정보", name = "MemberResponseLogin")
    class Login(val name: String, val phone: String, val token: String = "")
}