package com.midasit.mcafe.model

import org.springframework.security.core.Authentication

interface BaseController {
    fun getMemberSn(authentication: Authentication): Long {
        return (authentication.principal as Long)
    }
}