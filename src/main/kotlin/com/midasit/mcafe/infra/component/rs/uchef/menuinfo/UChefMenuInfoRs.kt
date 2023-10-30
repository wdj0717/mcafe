package com.midasit.mcafe.infra.component.rs.uchef.menuinfo

import com.fasterxml.jackson.annotation.JsonProperty

class UChefMenuInfoRs(
        @JsonProperty("searchResult")
        val searchResult: SearchResult
)

class SearchResult(
        @JsonProperty("list")
        val menuInfoList: ArrayList<MenuInfo>
)

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

data class OptionGroup (
        @JsonProperty("group_name")
        val name: String,
        @JsonProperty("group_min")
        val selectMin: Long,
        @JsonProperty("group_max")
        val selectMax: Long,
        @JsonProperty("options")
        val optionList: ArrayList<Option>
)

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