package com.midasit.mcafe.infra.component.rs.uchef.menu

import com.fasterxml.jackson.annotation.JsonProperty

class UChefMenuRs(
        @JsonProperty("searchResult")
        val searchResult: SearchResult
)

class SearchResult(
        @JsonProperty("jsonData")
        val jsonData: JsonData
)

class JsonData(
        @JsonProperty("PAGELIST")
        val pageList: PageList
)

class PageList(
        @JsonProperty("PAGE")
        val page: Array<Page>
)

class Page(
        @JsonProperty("name")
        val name: String,
        @JsonProperty("LISTCOMP")
        val listComp: ListComp
)

class ListComp(
        @JsonProperty("LISTROW")
        val listRow: Array<ListRow>
)

class ListRow(
        @JsonProperty("ORDERBUTTONCOMP")
        val orderButtonComp: Array<OrderButtonComp>
)

data class OrderButtonComp(
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
