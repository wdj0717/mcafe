package com.midasit.mcafe.domain.gamedata

import com.midasit.mcafe.domain.member.Member
import com.midasit.mcafe.domain.room.Room
import com.midasit.mcafe.model.GameType
import com.midasit.mcafe.model.ReadyStatus
import org.springframework.data.jpa.repository.JpaRepository

interface GameReadyRepository : JpaRepository<GameReady, Long>, GameReadyRepositoryExtend {

    fun deleteGameReadyByRoomAndGameTypeAndReadyStatus(room: Room, gameType: GameType, readyStatus: ReadyStatus)
    fun deleteGameReadyByRoomAndMemberAndGameType(room: Room, member: Member, gameType: GameType)

}