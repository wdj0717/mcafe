package com.midasit.mcafe.domain.looseHistory.dto

import com.midasit.mcafe.domain.member.dto.MemberDto
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

class LooserHistoryResponse {

    @Schema(name = "LooserHistoryResponseResults", description = "패배 조회 결과 리스트")
    class Results(val results: List<Result>)

    @Schema(name = "LooserHistoryResponseResult", description = "패배 조회 결과")
    class Result(val memberDto: MemberDto, val looseDate: List<LocalDateTime>, val looseCount: Int)
}