package com.midasit.mcafe.domain.looseHistory.dto

import com.midasit.mcafe.model.GameType
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

class LooseHistoryRequest {

    @Schema(name = "DashBoardRequestPost", description = "패배자 생성 rq")
    class Post(val looserSn: Long, val gameType: GameType)

    @Schema(name = "DashBoardRequestGet", description = "패배자 조회 rq")
    class Get(val memberSns: List<Long>, val gameType: GameType, val startDate: LocalDateTime, val endDate: LocalDateTime)

}