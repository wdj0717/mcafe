package com.midasit.mcafe.infra.component.rs.uchef.menuinfo

import com.fasterxml.jackson.annotation.JsonProperty

class SearchResult(
        @JsonProperty("list")
        val menuInfoList: ArrayList<MenuInfo>
)