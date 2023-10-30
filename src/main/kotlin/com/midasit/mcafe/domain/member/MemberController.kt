package com.midasit.mcafe.domain.member

import com.midasit.mcafe.domain.member.dto.MemberRequest
import com.midasit.mcafe.domain.member.dto.MemberResponse
import io.swagger.v3.oas.annotations.Operation
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/member")
class MemberController(private val memberService: MemberService) {

    @Operation(summary = "회원가입")
    @PostMapping("/signup")
    fun signup(@RequestBody request: MemberRequest.Signup): MemberResponse.Result {
        return MemberResponse.Result.of(memberService.signup(request))
    }

    @Operation(summary = "로그인")
    @PostMapping("/login")
    fun login(@RequestBody request: MemberRequest.Login): MemberResponse.Login {
        val login = memberService.login(request)
        return MemberResponse.Login(name = login.name, phone = login.phone)
    }
}