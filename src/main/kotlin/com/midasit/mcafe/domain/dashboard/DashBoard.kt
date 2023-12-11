package com.midasit.mcafe.domain.dashboard

import com.midasit.mcafe.domain.member.Member
import com.midasit.mcafe.model.BaseEntity
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "dash_board")
class DashBoard(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_sn", nullable = false, foreignKey = ForeignKey(name = "fk_dashboard_member_sn"))
    val looser: Member,
    @Column(nullable = false)
    val looseDate: LocalDateTime
) : BaseEntity()