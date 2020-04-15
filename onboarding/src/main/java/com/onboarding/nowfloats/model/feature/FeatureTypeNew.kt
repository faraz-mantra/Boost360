package com.onboarding.nowfloats.model.feature

enum class FeatureTypeNew(val iconType: String) {
    DIGITAL_CONTENT("/icons/digital_content.svg"),
    DIGITAL_APPOINTMENT("/icons/digital_appointment.svg"),
    DIGITAL_PAYMENT("/icons/digital_payment.svg"),
    DIGITAL_SECURITY("/icons/digital_security.svg"),
    DIGITAL_COMLIANCE("/icons/digital_compliance.svg"),
    DIGITAL_ASSISTANT("/icons/digital_assistant_ria.svg");

    companion object {
        fun from(findValue: String): FeatureTypeNew = values().first { it.iconType == findValue }
    }
}