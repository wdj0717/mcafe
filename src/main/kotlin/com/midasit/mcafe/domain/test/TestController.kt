package com.midasit.mcafe.domain.test

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@Tag(name = "테스트 컨트롤러")
class TestController {

    @GetMapping("/test")
    @Operation(summary = "테스트")
    fun test(): String {
        return "test"
    }
}