package com.midasit.mcafe.infra.component.rs.uchef.menuinfo

import com.fasterxml.jackson.annotation.JsonProperty

data class MenuInfo(
        @JsonProperty("item_code")
        val code: String,
        @JsonProperty("item_name")
        val name: String,
        @JsonProperty("item_price")
        val price: Long,
        @JsonProperty("item_stock")
        val stock: Long,
        @JsonProperty("option_group")
        val optionGroupList: ArrayList<OptionGroup>
)