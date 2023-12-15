package com.midasit.mcafe.domain.member.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(name = "LoginDto", description = "로그인 정보")
data class LoginDto(val sn: Long, val phone: String, val name: String, val token: String = "")
