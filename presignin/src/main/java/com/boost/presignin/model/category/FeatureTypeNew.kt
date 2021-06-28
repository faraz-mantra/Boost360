package com.boost.presignin.model.category

enum class FeatureTypeNew(val iconType: String) {
    DIGITAL_CONTENT("/icons/digital_content.svg"),
    DIGITAL_APPOINTMENT("/icons/digital_appointment.svg"),
    DIGITAL_CLINIC("/icons/digital_clinic.svg"),
    DIGITAL_PAYMENT("/icons/digital_payment.svg"),
    DIGITAL_SECURITY("/icons/digital_security.svg"),
    DIGITAL_COMLIANCE("/icons/digital_compliance.svg"),
    DIGITAL_PROFILES("/icons/digital_profiles.svg"),
    HOTEL_RESERVATION("/icons/digital_hotel_reservation.svg"),
    DIGITAL_ASSISTANT("/icons/digital_assistant_ria.svg"),
    CUSTOMER_REVIEWS("/icons/customer_reviews.svg"),
    DIGITAL_FOOD_ORDER("/icons/digital_food_ordering.svg"),
    DIGITAL_QUOTATIONS("/icons/qet_a_quote.svg"),
    DIGITAL_STOREFRONT("/icons/digital_storefront.svg");

    companion object {
        fun from(findValue: String): FeatureTypeNew = values().first { it.iconType == findValue }
    }
}