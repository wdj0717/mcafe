package com.midasit.mcafe.domain.gamedata.dto

import com.midasit.mcafe.domain.gamedata.GameReady
import com.midasit.mcafe.domain.member.dto.MemberDto
import com.midasit.mcafe.model.GameType
import com.midasit.mcafe.model.ReadyStatus
import io.swagger.v3.oas.annotations.media.Schema

class GameDataResponse {
    @Schema(name = "GameReadyResponseResults", description = "게임 준비 상태 rs 리스트")
    class Results(val results: List<Result>) {
        companion object {
            fun of(gameReadys: List<GameReady>) =
                Results(gameReadys.map { Result.of(it) })
        }
    }

    @Schema(name = "GameReadyResponseResult", description = "게임 준비 상태 rs")
    class Result(val memberDto: MemberDto, val roomSn: Long, val gameType: GameType, val readyStatus: ReadyStatus){
        companion object {
            fun of(gameReady: GameReady) =
                Result(MemberDto.of(gameReady.member), gameReady.room.sn, gameReady.gameType, gameReady.readyStatus)
        }
    }
}