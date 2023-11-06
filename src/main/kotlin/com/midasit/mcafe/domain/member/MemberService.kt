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
    fun signup(request: MemberRequest.Signup): MemberDto {
        this.validateMember(request)

        val member = Member(
            phone = request.phone,
            username = request.username,
            password = request.password,
            name = request.name,
            role = Role.USER
        )
        return MemberDto.of(memberRepository.save(member))
    }

    private fun validateMember(request: MemberRequest.Signup) {
        val valueOperations = redisTemplate.opsForValue()
        val phone = valueOperations.getAndDelete(request.certKey)
        if (phone != request.phone) {
            throw CustomException(ErrorMessage.INVALID_UCHEF_AUTH)
        }
    }

    fun login(request: MemberRequest.Login): LoginDto {
        return memberRepository.findByUsername(request.username)?.let { member ->
            if (PasswordEncryptUtil.match(request.password, member.password).not()) {
                throw CustomException(ErrorMessage.INVALID_LOGIN_REQUEST)
            }
            require(member.sn != null) { CustomException(ErrorMessage.INVALID_LOGIN_REQUEST) }
            val accessToken = jwtTokenProvider.generateAccessToken(member.sn)
            LoginDto(phone = member.phone, name = member.name, token = accessToken)
        } ?: throw CustomException(ErrorMessage.INVALID_LOGIN_REQUEST)
    }

    fun findBySn(memberSn: Long): Member {
        return memberRepository.findBySn(memberSn) ?: throw CustomException(ErrorMessage.INVALID_LOGIN_REQUEST)
    }
}