package com.midasit.mcafe.domain.member

import com.midasit.mcafe.domain.member.dto.LoginDto
import com.midasit.mcafe.domain.member.dto.MemberDto
import com.midasit.mcafe.domain.member.dto.MemberRequest
import com.midasit.mcafe.infra.config.jwt.JwtTokenProvider
import com.midasit.mcafe.model.Role
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class MemberService(
    private val memberRepository: MemberRepository,
    private val jwtTokenProvider: JwtTokenProvider
) {

    @Transactional
    fun signup(request: MemberRequest.Signup) : MemberDto {
        val member = Member(
            phone = request.phone,
            name = request.name,
            password = request.password,
            role = Role.USER
        )
        return MemberDto.of(memberRepository.save(member))
    }

    fun login(request: MemberRequest.Login): LoginDto {
        return memberRepository.findByPhoneAndPassword(request.phone, request.password)?.let { member ->
            val accessToken = jwtTokenProvider.generateAccessToken(member.phone)
            LoginDto(phone = member.phone, name = member.name, token = accessToken)
        } ?: throw Exception("로그인 정보가 없습니다.")
    }
}