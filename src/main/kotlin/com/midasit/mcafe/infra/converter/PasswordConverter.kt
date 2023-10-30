package com.midasit.mcafe.infra.converter

import jakarta.persistence.AttributeConverter
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.stereotype.Component

@Component
class PasswordConverter : AttributeConverter<String, String> {

    private val passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder()
    override fun convertToDatabaseColumn(attribute: String?): String {
        return if (attribute.isNullOrBlank() || attribute.contains(BCRYPT_PREFIX)) {
            attribute!!
        } else passwordEncoder.encode(attribute)

    }

    override fun convertToEntityAttribute(dbData: String): String {
        return dbData
    }

    companion object {
        private const val BCRYPT_PREFIX = "{bcrypt}"
    }
}