package com.midasit.mcafe.infra.exception

class CustomException(
    val errorMessage: ErrorMessage
) : RuntimeException(errorMessage.message)