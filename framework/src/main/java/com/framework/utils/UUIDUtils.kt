package com.framework.utils

import java.util.*

object UUIDUtils {

    fun generateRandomUUID(): String {
        return UUID.randomUUID().toString()
    }
}