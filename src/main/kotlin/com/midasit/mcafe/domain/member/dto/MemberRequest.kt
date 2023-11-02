package com.midasit.mcafe.domain.member.dto

import io.swagger.v3.oas.annotations.media.Schema

class MemberRequest {
    @Schema(description = "회원가입", name = "MemberRequestSignup")
    class Signup(val phone: String, val name: String, val password: String, val certKey: String)

    @Schema(description = "로그인", name = "MemberRequestLogin")
    class Login(val phone: String, val password: String)
}