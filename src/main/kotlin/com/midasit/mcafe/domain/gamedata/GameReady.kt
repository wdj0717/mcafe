package com.midasit.mcafe.domain.gamedata

import com.midasit.mcafe.domain.member.Member
import com.midasit.mcafe.domain.room.Room
import com.midasit.mcafe.model.BaseEntity
import com.midasit.mcafe.model.GameType
import com.midasit.mcafe.model.ReadyStatus
import jakarta.persistence.*

@Entity
@Table(name = "game_ready")
class GameReady(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_sn", nullable = false, foreignKey = ForeignKey(name = "fk_gameready_member_sn"))
    val member: Member,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_sn", nullable = false, foreignKey = ForeignKey(name = "fk_gameready_room_sn"))
    val room: Room,
    @Enumerated(EnumType.STRING)
    val readyStatus: ReadyStatus,
    @Enumerated(EnumType.STRING)
    val gameType: GameType
) : BaseEntity()