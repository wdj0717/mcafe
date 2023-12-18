package com.midasit.mcafe.domain.gamedata

import com.midasit.mcafe.domain.order.Order
import com.midasit.mcafe.model.GameType
import com.midasit.mcafe.model.ReadyStatus

interface GameReadyRepositoryExtend {

    fun findGameReadyByMemberSnAndRoomSnAndGameType(memberSn: Long, roomSn: Long, gameType: GameType): GameReady?
    fun findGameReadyByRoomSnAndGameType(roomSn: Long, gameType: GameType): List<GameReady>
    fun findAllOrderByRoomSnAndGameTypeAndGameReadyStatus(roomSn: Long, gType: GameType, readyStatus: ReadyStatus): List<Order>
}