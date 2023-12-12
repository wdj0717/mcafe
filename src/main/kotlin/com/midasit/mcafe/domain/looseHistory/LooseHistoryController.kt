package com.midasit.mcafe.domain.looseHistory

import com.midasit.mcafe.domain.looseHistory.dto.LooseHistoryRequest
import com.midasit.mcafe.model.BaseController
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@Tag(name = "방 컨트롤러")
@RequestMapping("/dashboard")
class LooseHistoryController(
    val looseHistoryService: LooseHistoryService
) : BaseController {

    @Operation(summary = "패배자 등록")
    @PostMapping("/looser")
    fun postLooser(@RequestBody request: LooseHistoryRequest.Post) {
        looseHistoryService.createLooseHistory(request)
    }

}