package com.boost.presignin.model.category;

enum class CategoryType(val iconType: String) {
    DOCTORS("/icons/healthcare.svg"),
    CLINICS_HOSPITALS("/icons/hospital.svg"),
    EDUCATION_COACHING("/icons/edu.svg"),
    HOTELS_MOTELS("/icons/hotels.svg"),
    MANUFACTURING_EQUIPMENT("/icons/mfg.svg"),
    SPAS_WELLNESS("/icons/spa.svg"),
    SALON("/icons/salon.svg"),
    RESTAURANT_CAFES("/icons/cafe.svg"),
    RETAIL_BUSINESS("/icons/rtl.svg"),
    SERVICES_BUSINESS("/icons/svc.svg");

    companion object {
        fun from(findValue: String): CategoryType = values().first { it.iconType == findValue }
    }
}