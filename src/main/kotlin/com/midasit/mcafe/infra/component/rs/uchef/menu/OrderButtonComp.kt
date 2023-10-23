package com.midasit.mcafe.infra.component.rs.uchef.menu

import com.fasterxml.jackson.annotation.JsonProperty

class OrderButtonComp(
        @JsonProperty("item_code")
        val itemCode: String,
        @JsonProperty("index")
        val index: Long,
        @JsonProperty("unit")
        val unit: String,
        @JsonProperty("price")
        val price: Long,
        @JsonProperty("name")
        val name: String,
        @JsonProperty("menutitle")
        val menuitle: String,
        @JsonProperty("stock")
        val stock: Long
)