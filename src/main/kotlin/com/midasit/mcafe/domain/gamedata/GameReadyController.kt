package com.midasit.mcafe.domain.gamedata

import com.midasit.mcafe.domain.gamedata.dto.GameDataRequest
import com.midasit.mcafe.domain.gamedata.dto.GameDataResponse
import com.midasit.mcafe.model.BaseController
import com.midasit.mcafe.model.GameType
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*

@RestController
@Tag(name = "게임 컨트롤러")
@RequestMapping("/game")
class GameReadyController(
    private val gameReadyService: GameReadyService
) : BaseController {

    @Operation(summary = "게임 준비 상태 업데이트")
    @PutMapping("/ready")
    fun putGameReadyStatus(@RequestBody request: GameDataRequest.Put) : GameDataResponse.Result {
        return GameDataResponse.Result.of(gameReadyService.updateGameReadyStatus(request.memberSn, request.roomSn, request.gameType, request.readyStatus))
    }

    @Operation(summary = "게임 준비 상태 조회")
    @GetMapping("/ready")
    fun getGameReadyStatusOfRoomMember(@RequestParam roomSn: Long,
                                       @RequestParam gameType: GameType): GameDataResponse.Results {
        return GameDataResponse.Results.of(gameReadyService.getGameReadyStatusOfRoomMember(roomSn, gameType))
    }
}