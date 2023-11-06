package com.midasit.mcafe.domain.favoritemenu

import com.midasit.mcafe.domain.member.Member
import com.midasit.mcafe.model.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "favorite_menu")
class FavoriteMenu(
        @Column(nullable = false)
        val menuCode :String,
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "member_id", nullable = false, foreignKey = ForeignKey(name = "fk_favorite_menu_member_id"))
        val member: Member
) :BaseEntity()