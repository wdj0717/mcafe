package com.midasit.mcafe.domain.room

import com.midasit.mcafe.domain.member.Member
import com.midasit.mcafe.model.BaseEntity
import jakarta.persistence.*

@Entity
class RoomMember(
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "member_id", nullable = false, foreignKey = ForeignKey(name = "fk_room_member_member_id"))
        val member: Member,
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "room_id", nullable = false, foreignKey = ForeignKey(name = "fk_room_member_room_id"))
        val room: Room,
) : BaseEntity(){

    @Id
    @Column(name = "room_member_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null
}