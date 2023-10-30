package com.midasit.mcafe.infra.component.rs.uchef.menuinfo

import com.fasterxml.jackson.annotation.JsonProperty

data class Option(
        @JsonProperty("option_name")
        val name: String,
        @JsonProperty("option_seq")
        val code: Long,
        @JsonProperty("option_price")
        val price: Long,
        @JsonProperty("option_default")
        val default: Boolean,
)