package com.midasit.mcafe.domain.looseHistory

import com.midasit.mcafe.domain.member.Member
import com.midasit.mcafe.model.BaseEntity
import com.midasit.mcafe.model.GameType
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "loose_history")
class LooseHistory(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_sn", nullable = false, foreignKey = ForeignKey(name = "fk_loose_history_member_sn"))
    val looser: Member,
    @Enumerated(EnumType.STRING)
    val gameType: GameType
) : BaseEntity() {

    @Column(nullable = false)
    val looseDate: LocalDateTime = LocalDateTime.now()

}