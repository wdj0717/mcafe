package com.midasit.mcafe.domain.looseHistory

import com.midasit.mcafe.domain.gamedata.GameReadyRepository
import com.midasit.mcafe.domain.looseHistory.dto.LooseHistoryDto
import com.midasit.mcafe.domain.looseHistory.dto.LooseHistoryRequest
import com.midasit.mcafe.domain.member.Member
import com.midasit.mcafe.domain.member.MemberService
import com.midasit.mcafe.domain.payment.PaymentService
import com.midasit.mcafe.domain.room.Room
import com.midasit.mcafe.domain.room.RoomService
import com.midasit.mcafe.model.GameType
import com.midasit.mcafe.model.ReadyStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional(readOnly = true)
class LooseHistoryService(
    val memberService: MemberService,
    val roomService: RoomService,
    val paymentService: PaymentService,
    val looseHistoryRepository: LooseHistoryRepository,
    val gameReadyRepository: GameReadyRepository,
) {
    @Transactional
    fun createLooseHistory(request: LooseHistoryRequest.Post) {
        val member = memberService.findBySn(request.looserSn)
        val room = roomService.findBySn(request.roomSn)
        val gameType = request.gameType
        looseHistoryRepository.save(LooseHistory(member, room, gameType))

//        this.payOrder(member, room, gameType, ReadyStatus.READY)
    }

    fun getLooseHistory(memberSns: List<Long>,
                        roomSn: Long,
                        gameType: GameType,
                        startDate: LocalDateTime,
                        endDate: LocalDateTime) : List<LooseHistoryDto>{
        return looseHistoryRepository.findLooseHistoryDtoList(memberSns, roomSn, gameType, startDate, endDate)
    }

    private fun payOrder(member: Member, room: Room, gType: GameType, gameReadyStatus: ReadyStatus){
        val orderSnList = gameReadyRepository.findAllOrderByRoomSnAndGameTypeAndGameReadyStatus(room.sn, gType, gameReadyStatus).map { it.sn }
        paymentService.payOrder(member.sn, room.sn, orderSnList)
    }

}