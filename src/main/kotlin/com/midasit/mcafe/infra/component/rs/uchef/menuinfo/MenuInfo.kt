package com.midasit.mcafe.infra.component.rs.uchef.menuinfo

import com.fasterxml.jackson.annotation.JsonProperty

class MenuInfo(
        @JsonProperty("item_code")
        val itemCode: String,
        @JsonProperty("item_name")
        val itemName: String,
        @JsonProperty("item_price")
        val itemPrice: Long,
        @JsonProperty("item_stock")
        val itemStock: Long,
        @JsonProperty("option_group")
        val optionGroupList: ArrayList<OptionGroup>
)