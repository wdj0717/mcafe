package com.midasit.mcafe.infra.component.rs.uchef.login

import com.fasterxml.jackson.annotation.JsonProperty

class UChefLoginRs(
        @JsonProperty("resultCode")
        val resultCode: String
)