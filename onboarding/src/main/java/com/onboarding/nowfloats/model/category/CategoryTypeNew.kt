package com.onboarding.nowfloats.model.category

enum class CategoryTypeNew(val iconType: String) {
    DOCTORS_CLINICS("/icons/healthcare.svg"),
    EDUCATION_COACHING(""),
    HOTELS_MOTELS(""),
    MANUFACTURING_EQUIPMENT(""),
    SPAS_SALONS("/icons/spa.svg"),
    RESTAURANT_CAFES(""),
    RETAIL_BUSINESS(""),
    SERVICES_BUSINESS("");

    companion object {
        fun from(findValue: String): CategoryTypeNew = values().first { it.iconType == findValue }
    }
}