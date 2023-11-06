package com.midasit.mcafe.domain.room

import com.midasit.mcafe.domain.member.Member
import com.midasit.mcafe.model.BaseEntity
import com.midasit.mcafe.model.RoomStatus
import jakarta.persistence.*

@Entity
@Table(name = "room", uniqueConstraints = [UniqueConstraint(columnNames = ["name", "status"])])
class Room(
        name: String,
        password: String,
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "host_id", nullable = false, foreignKey = ForeignKey(name = "fk_room_host_id"))
        val host: Member,
        @Enumerated(EnumType.STRING)
        val status: RoomStatus
): BaseEntity(){
    @Column(nullable = false)
    var name: String = name
        private set

    @Column(nullable = false)
    var password: String = password
        private set
}