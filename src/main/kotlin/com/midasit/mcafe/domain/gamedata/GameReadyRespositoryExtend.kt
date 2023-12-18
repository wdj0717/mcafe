package com.midasit.mcafe.domain.gamedata

import com.midasit.mcafe.model.GameType

interface GameReadyRepositoryExtend {

    fun findGameReadyByMemberSnAndRoomSnAndGameType(memberSn: Long, roomSn: Long, gameType: GameType): GameReady?
    fun findGameReadyByRoomSnAndGameType(roomSn: Long, gameType: GameType): List<GameReady>

}