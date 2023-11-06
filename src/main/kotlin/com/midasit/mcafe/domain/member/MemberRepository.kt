package com.midasit.mcafe.domain.member

import org.springframework.data.jpa.repository.JpaRepository

interface MemberRepository : JpaRepository<Member, Long> {
    fun findBySn(sn: Long) : Member?
    fun findByUsername(username: String) : Member?
}