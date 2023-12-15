package com.midasit.mcafe.domain.member.dto

import io.swagger.v3.oas.annotations.media.Schema

class MemberResponse {

    @Schema(description = "cert key", name = "MemberResponseCertKey")
    class CertKey(val certKey: String)

    @Schema(description = "username 중복 검사 반환 객체", name = "MemberResponseUsernameCheck")
    class UsernameCheck(val isPossible: Boolean)

    @Schema(description = "유저 정보", name = "MemberResponseResult")
    class Result(
        val username: String,
        val nickname: String,
        val phone: String
    ) {
        companion object {
            fun of(memberDto: MemberDto): Result {
                return Result(memberDto.username, memberDto.nickname, memberDto.phone)
            }
        }
    }

    @Schema(description = "로그인 유저 정보", name = "MemberResponseLogin")
    class Login(val sn:Long, val name: String, val phone: String, val token: String = "")
}