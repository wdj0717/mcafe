package com.midasit.mcafe.model

import com.midasit.mcafe.infra.exception.CustomException
import com.midasit.mcafe.infra.exception.ErrorMessage

fun validate(errorMessage: ErrorMessage = ErrorMessage.INVALID_REQUEST, block: () -> Boolean) {
    if (block().not()) throw CustomException(errorMessage)
}