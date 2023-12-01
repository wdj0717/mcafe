package com.midasit.mcafe.domain.member

import com.midasit.mcafe.infra.exception.CustomException
import com.midasit.mcafe.infra.exception.ErrorMessage
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.findByIdOrNull

fun MemberRepository.getOrThrow(sn: Long): Member =
    findByIdOrNull(sn) ?: throw CustomException(ErrorMessage.INVALID_LOGIN_REQUEST)

interface MemberRepository : JpaRepository<Member, Long> {
    fun findByUsername(username: String): Member?
    fun existsByUsername(username: String): Boolean
}