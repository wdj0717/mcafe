package com.midasit.mcafe.domain.gamedata

import com.midasit.mcafe.domain.room.Room
import com.midasit.mcafe.model.GameType
import com.midasit.mcafe.model.ReadyStatus
import org.springframework.data.jpa.repository.JpaRepository

interface GameReadyRepository : JpaRepository<GameReady, Long>, GameReadyRepositoryExtend {

    fun deleteGameReadyByRoomAndGameTypeAndReadyStatus(room: Room, gameType: GameType, readyStatus: ReadyStatus)

}