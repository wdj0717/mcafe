package com.midasit.mcafe.domain.looseHistory.dto

import com.midasit.mcafe.domain.member.Member
import com.midasit.mcafe.model.GameType

data class LooseHistoryDto(
    val looser: Member,
    val gameType: GameType,
    val looseCount: Int
) {
}