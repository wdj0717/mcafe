package com.midasit.mcafe.domain.order.dto

class OptionGroupDto(
        val name: String,
        val selectMin: Long,
        val selectMax: Long,
        val optionList: ArrayList<OptionDto>
)