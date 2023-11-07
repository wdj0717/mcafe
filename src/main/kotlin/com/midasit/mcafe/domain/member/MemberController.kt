package com.midasit.mcafe.domain.member

import com.midasit.mcafe.domain.member.dto.MemberRequest
import com.midasit.mcafe.domain.member.dto.MemberResponse
import io.swagger.v3.oas.annotations.Operation
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/member")
class MemberController(private val memberService: MemberService) {

    @Operation(summary = "uchef 인증")
    @PostMapping("/uchef-auth")
    fun uChefAuth(@RequestBody request: MemberRequest.UChefAuth): MemberResponse.CertKey {
        return MemberResponse.CertKey(memberService.getUChefAuth(request))
    }

    @Operation(summary = "username 중복검사")
    @GetMapping("/idcheck/{username}")
    fun usernameCheck(@PathVariable username: String): MemberResponse.UsernameCheck {
        return MemberResponse.UsernameCheck(!memberService.existsMemberByUsername(username))
    }

    @Operation(summary = "회원가입")
    @PostMapping("/signup")
    fun signup(@RequestBody request: MemberRequest.Signup): MemberResponse.Result {
        return MemberResponse.Result.of(memberService.signup(request))
    }

    @Operation(summary = "로그인")
    @PostMapping("/login")
    fun login(@RequestBody request: MemberRequest.Login): MemberResponse.Login {
        val login = memberService.login(request)
        return MemberResponse.Login(name = login.name, phone = login.phone, token = login.token)
    }
}