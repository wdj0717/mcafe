package com.midasit.mcafe.domain.looseHistory.dto

import com.midasit.mcafe.model.GameType
import io.swagger.v3.oas.annotations.media.Schema

class LooseHistoryRequest {

    @Schema(name = "DashBoardRequestPost", description = "패배자 생성 rq")
    class Post(val looserSn: Long, val gameType: GameType)

}