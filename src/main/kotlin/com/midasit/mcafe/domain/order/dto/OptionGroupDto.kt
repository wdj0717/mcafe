package com.midasit.mcafe.domain.order.dto

import com.midasit.mcafe.infra.component.rs.uchef.menuinfo.OptionGroup

class OptionGroupDto(
    val name: String,
    val selectMin: Long,
    val selectMax: Long,
    val optionList: List<OptionDto>
) {
    companion object {
        fun of(optionGroup: OptionGroup, optionList: List<OptionDto>): OptionGroupDto =
            OptionGroupDto(
                optionGroup.name,
                optionGroup.selectMin,
                optionGroup.selectMax,
                optionList
            )
    }
}