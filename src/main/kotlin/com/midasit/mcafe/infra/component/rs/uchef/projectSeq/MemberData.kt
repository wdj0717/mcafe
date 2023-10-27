package com.midasit.mcafe.infra.component.rs.uchef.projectSeq

import com.fasterxml.jackson.annotation.JsonProperty

class MemberData(
        @JsonProperty("default_project_seq")
        val defaultProjectSeq: Int
)