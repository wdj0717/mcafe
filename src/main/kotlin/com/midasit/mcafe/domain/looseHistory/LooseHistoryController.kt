package com.midasit.mcafe.domain.looseHistory

import com.midasit.mcafe.domain.looseHistory.dto.LooseHistoryRequest
import com.midasit.mcafe.domain.looseHistory.dto.LooserHistoryResponse
import com.midasit.mcafe.model.BaseController
import com.midasit.mcafe.model.GameType
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@Tag(name = "통계 컨트롤러")
@RequestMapping("/dashboard")
class LooseHistoryController(
    val looseHistoryService: LooseHistoryService
) : BaseController {

    @Operation(summary = "패배자 등록")
    @PostMapping("/looser")
    fun postLooser(@RequestBody request: LooseHistoryRequest.Post) {
        looseHistoryService.createLooseHistory(request)
    }

    @Operation(summary = "패배자 조회")
    @GetMapping("/looser")
    fun getLooserData(@RequestParam memberSns: List<Long>,
                      @RequestParam gameType: GameType,
                      @RequestParam startDate: LocalDateTime,
                      @RequestParam endDate: LocalDateTime
                      ) : LooserHistoryResponse.Results {
        return LooserHistoryResponse.Results.of(looseHistoryService.getLooseHistory(memberSns, gameType, startDate, endDate))
    }

}