package com.midasit.mcafe.domain.test

import com.midasit.mcafe.infra.component.UChefComponent
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@Tag(name = "테스트 컨트롤러")
class TestController(private val uChefComponent: UChefComponent) {

    @GetMapping("/test")
    @Operation(summary = "테스트")
    fun test(): String {
        return "test"
    }

    @GetMapping("/test/uchef/login")
    @Operation(summary = "u chef 로그인 테스트")
    fun uChefLoginTest(phone: String, securityId: String, password: String): String {
        return uChefComponent.login(phone, securityId, password)
    }
}