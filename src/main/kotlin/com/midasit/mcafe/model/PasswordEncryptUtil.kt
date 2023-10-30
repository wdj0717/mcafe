package com.midasit.mcafe.model

import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder

object PasswordEncryptUtil {

    private val passwordEncoder:PasswordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder()

    fun encrypt(password:String):String{
        return passwordEncoder.encode(password)
    }

    fun match(password:String, encryptedPassword:String):Boolean {
        return passwordEncoder.matches(password, encryptedPassword)
    }
}