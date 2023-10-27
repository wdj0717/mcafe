package com.midasit.mcafe.infra.component.rs.uchef.menuinfo

import com.fasterxml.jackson.annotation.JsonProperty

class OptionGroup (
        @JsonProperty("group_name")
        val name: String,
        @JsonProperty("group_min")
        val selectMin: Long,
        @JsonProperty("group_max")
        val selectMax: Long,
        @JsonProperty("options")
        val optionList: ArrayList<Option>
)