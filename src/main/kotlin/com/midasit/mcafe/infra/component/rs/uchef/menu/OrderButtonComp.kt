package com.midasit.mcafe.infra.component.rs.uchef.menu

import com.fasterxml.jackson.annotation.JsonProperty

class OrderButtonComp(
        @JsonProperty("item_code")
        val code: String,
        @JsonProperty("unit")
        val unit: String,
        @JsonProperty("price")
        val price: Long,
        @JsonProperty("menutitle")
        val name: String,
        @JsonProperty("stock")
        val stock: Long
)