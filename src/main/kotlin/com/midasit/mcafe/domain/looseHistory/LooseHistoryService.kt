package com.midasit.mcafe.domain.looseHistory

import com.midasit.mcafe.domain.looseHistory.dto.LooseHistoryDto
import com.midasit.mcafe.domain.looseHistory.dto.LooseHistoryRequest
import com.midasit.mcafe.domain.member.MemberService
import com.midasit.mcafe.model.GameType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional(readOnly = true)
class LooseHistoryService(
    val memberService: MemberService,
    val looseHistoryRepository: LooseHistoryRepository
) {
    @Transactional
    fun createLooseHistory(request: LooseHistoryRequest.Post) {
        val member = memberService.findBySn(request.looserSn)
        val gameType = request.gameType
        println(LocalDateTime.now())
        looseHistoryRepository.save(LooseHistory(member, gameType))
        // 추후 주문 API 추가 할 것.
    }

    fun getLooseHistory(memberSns: List<Long>,
                        gameType: GameType,
                        startDate: LocalDateTime,
                        endDate: LocalDateTime) : List<LooseHistoryDto>{
        return looseHistoryRepository.findLooseHistoryDtoList(memberSns, gameType, startDate, endDate)
    }

}