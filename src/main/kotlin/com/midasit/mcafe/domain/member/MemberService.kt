package com.midasit.mcafe.domain.member

import com.midasit.mcafe.domain.member.dto.LoginDto
import com.midasit.mcafe.domain.member.dto.MemberDto
import com.midasit.mcafe.domain.member.dto.MemberRequest
import com.midasit.mcafe.infra.config.jwt.JwtTokenProvider
import com.midasit.mcafe.infra.exception.CustomException
import com.midasit.mcafe.infra.exception.ErrorMessage
import com.midasit.mcafe.model.PasswordEncryptUtil
import com.midasit.mcafe.model.Role
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class MemberService(
    private val memberRepository: MemberRepository,
    private val jwtTokenProvider: JwtTokenProvider,
    private val redisTemplate: RedisTemplate<String, Any>
) {

    @Transactional
    fun signup(request: MemberRequest.Signup) : MemberDto {
        val valueOperations = redisTemplate.opsForValue()
        val phone = valueOperations.getAndDelete(request.certKey)
        if (phone != request.phone) {
            throw CustomException(ErrorMessage.INVALID_UCHEF_AUTH)
        }
        val member = Member(
            phone = request.phone,
            name = request.name,
            password = request.password,
            role = Role.USER
        )
        return MemberDto.of(memberRepository.save(member))
    }

    fun login(request: MemberRequest.Login): LoginDto {
        return memberRepository.findByPhone(request.phone)?.let { member ->
            if(PasswordEncryptUtil.match(request.password, member.password).not()) {
                throw Exception("비밀번호가 일치하지 않습니다.")
            }
            val accessToken = jwtTokenProvider.generateAccessToken(member.phone)
            LoginDto(phone = member.phone, name = member.name, token = accessToken)
        } ?: throw Exception("로그인 정보가 없습니다.")
    }

    fun findByPhone(phone: String): Member {
        return memberRepository.findByPhone(phone) ?: throw Exception("회원 정보가 없습니다.")
    }
}