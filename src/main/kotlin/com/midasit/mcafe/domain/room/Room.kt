package com.midasit.mcafe.domain.room

import com.midasit.mcafe.domain.member.Member
import com.midasit.mcafe.model.BaseEntity
import com.midasit.mcafe.model.RoomStatus
import jakarta.persistence.*

@Entity
class Room(
        @Column(nullable = false)
        val name: String,
        @Column(nullable = false)
        val password: String,
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "host_id", nullable = false, foreignKey = ForeignKey(name = "fk_room_host_id"))
        val host: Member,
        @Enumerated(EnumType.STRING)
        val status: RoomStatus
): BaseEntity(){
    @Id
    @Column(name = "room_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null
}