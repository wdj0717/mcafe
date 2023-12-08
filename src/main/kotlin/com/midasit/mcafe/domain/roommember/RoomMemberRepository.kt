package com.midasit.mcafe.domain.roommember

import com.midasit.mcafe.domain.member.Member
import com.midasit.mcafe.domain.room.Room
import org.springframework.data.jpa.repository.JpaRepository

interface RoomMemberRepository : JpaRepository<RoomMember, Long> {
    fun findByRoom(room: Room): List<RoomMember>
    fun findByMember(member: Member): List<RoomMember>
    fun existsByRoomAndMember(room: Room, member: Member): Boolean
    fun deleteByRoomAndMember(room: Room, member: Member): Long
    fun deleteByMember(member: Member)
}