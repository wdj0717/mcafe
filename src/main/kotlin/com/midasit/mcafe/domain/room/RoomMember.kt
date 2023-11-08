package com.midasit.mcafe.domain.room

import com.midasit.mcafe.domain.member.Member
import com.midasit.mcafe.model.BaseEntity
import jakarta.persistence.*

@Entity
class RoomMember(
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "member_sn", nullable = false, foreignKey = ForeignKey(name = "fk_room_member_member_sn"))
        val member: Member,
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "room_sn", nullable = false, foreignKey = ForeignKey(name = "fk_room_member_room_sn"))
        val room: Room,
) : BaseEntity()