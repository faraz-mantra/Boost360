package com.boost.marketplace.infra.utils

object StringUtils {
    fun isEmpty(str: CharSequence?): Boolean {
        return str == null || str.toString().trim { it <= ' ' }.isEmpty()
    }

    fun isNotEmpty(str: CharSequence?): Boolean {
        return str != null && !str.toString().trim { it <= ' ' }.isEmpty()
    }
}