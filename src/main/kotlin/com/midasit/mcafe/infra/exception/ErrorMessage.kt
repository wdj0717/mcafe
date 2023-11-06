package com.midasit.mcafe.infra.exception

import org.springframework.http.HttpStatus

enum class ErrorMessage(
    val message: String,
    val httpStatus: HttpStatus
) {
    // 회원가입, 로그인
    INVALID_UCHEF_AUTH("m cafe 인증 정보가 맞지 않습니다.", HttpStatus.UNAUTHORIZED),
    DUPLICATE_ID("중복된 아이디가 존재합니다.", HttpStatus.CONFLICT),
    INVALID_LOGIN_REQUEST("아이디가 존재하지 않거나 비밀번호가 틀렸습니다.", HttpStatus.UNAUTHORIZED),
    INVALID_PASSWORD_CHECK("비밀번호 확인이 올바르지 않습니다.", HttpStatus.BAD_REQUEST)
}