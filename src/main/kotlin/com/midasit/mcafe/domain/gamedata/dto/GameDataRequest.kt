package com.midasit.mcafe.domain.gamedata.dto

import com.midasit.mcafe.model.GameType
import com.midasit.mcafe.model.ReadyStatus
import io.swagger.v3.oas.annotations.media.Schema

class GameDataRequest {

    @Schema(name = "GameReadyRequestPut", description = "게임 준비 상태 업데이트 rq")
    class Put(val memberSn: Long, val roomSn: Long, val gameType: GameType, val readyStatus: ReadyStatus)
}