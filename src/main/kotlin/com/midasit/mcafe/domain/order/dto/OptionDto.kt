package com.midasit.mcafe.domain.order.dto

import com.midasit.mcafe.infra.component.rs.uchef.menuinfo.Option

class OptionDto(
    val name: String,
    val code: Long,
    val price: Long,
    val isDefault: Boolean
) {
    companion object {
        fun from(option: Option): OptionDto =
            OptionDto(
                option.name,
                option.code,
                option.price,
                option.default
            )
    }
}