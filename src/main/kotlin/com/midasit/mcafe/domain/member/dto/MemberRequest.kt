package com.midasit.mcafe.domain.member.dto

import io.swagger.v3.oas.annotations.media.Schema

class MemberRequest {
    @Schema(description = "u chef 인증", name = "MemberRequestUcehfAuth")
    class UChefAuth(val phone: String, val securityId: String, val password: String)

    @Schema(description = "회원가입", name = "MemberRequestSignup")
    class Signup(
        val username: String,
        val password: String,
        val passwordCheck: String,
        val nickname: String,
        val certKey: String
    )

    @Schema(description = "로그인", name = "MemberRequestLogin")
    class Login(val username: String, val password: String)

    @Schema(description = "닉네임 변경", name = "MemberRequestNickname")
    class Nickname(val nickname: String)

    @Schema(description = "비밀번호 변경", name = "MemberRequestPassword")
    class Password(val password: String, val passwordCheck: String)
}