package com.midasit.mcafe.domain.looseHistory

import com.midasit.mcafe.domain.looseHistory.dto.LooseHistoryRequest
import com.midasit.mcafe.domain.member.MemberService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

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
        looseHistoryRepository.save(LooseHistory(member, gameType))
        // 추후 주문 API 추가 할 것.
    }

    fun getLooseHistory(request: LooseHistoryRequest.Get) {

    }

}