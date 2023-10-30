package com.midasit.mcafe.infra.converter

import com.midasit.mcafe.model.PasswordEncryptUtil.encrypt
import jakarta.persistence.AttributeConverter
import org.springframework.stereotype.Component

@Component
class PasswordConverter : AttributeConverter<String, String> {

    override fun convertToDatabaseColumn(attribute: String?): String {
        return if (attribute.isNullOrBlank() || attribute.contains(BCRYPT_PREFIX)) {
            attribute!!
        } else encrypt(attribute)

    }

    override fun convertToEntityAttribute(dbData: String): String {
        return dbData
    }

    companion object {
        private const val BCRYPT_PREFIX = "{bcrypt}"
    }
}