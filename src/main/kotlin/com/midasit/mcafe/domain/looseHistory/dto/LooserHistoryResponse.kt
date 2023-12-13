package com.midasit.mcafe.domain.looseHistory.dto

import com.midasit.mcafe.domain.member.dto.MemberDto
import com.midasit.mcafe.model.GameType
import io.swagger.v3.oas.annotations.media.Schema

class LooserHistoryResponse {

    @Schema(name = "LooserHistoryResponseResults", description = "패배 조회 결과 리스트")
    class Results(val results: List<Result>) {
        companion object {
            fun of(looseHistoryDtos: List<LooseHistoryDto>) =
                Results(looseHistoryDtos.map { Result.of(MemberDto.of(it.looser), it) })
        }
    }

    @Schema(name = "LooserHistoryResponseResult", description = "패배 조회 결과")
    class Result(val memberDto: MemberDto, val gameType: GameType, val looseCount: Long) {
        companion object {
            fun of(memberDto: MemberDto, looseHistoryDto: LooseHistoryDto) =
                Result(memberDto, looseHistoryDto.gameType, looseHistoryDto.looseCount)
        }
    }
}