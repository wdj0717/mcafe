package com.midasit.mcafe.domain.looseHistory

import com.midasit.mcafe.domain.looseHistory.dto.LooseHistoryDto
import com.midasit.mcafe.model.GameType
import java.time.LocalDateTime

interface LooseHistoryRepositoryExtend {
    fun findLooseHistoryDtoList(looserSnList: List<Long>, gType: GameType, startLooseDate: LocalDateTime, endLooseDate: LocalDateTime): List<LooseHistoryDto>
}