package com.inventoryorder.utils

fun getAptType(category_code: String?): String {
    return when (category_code) {
        "SPA", "SAL", "SVC" -> "SPA_SAL_SVC"
        "DOC", "HOS" -> "DOC_HOS"
        else -> "OTHERS"
    }
}