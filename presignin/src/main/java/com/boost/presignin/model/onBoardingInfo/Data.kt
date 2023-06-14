package com.boost.presignin.model.onBoardingInfo

data class Data(
    var businessName: String = "",
    var domainName: String = "",
    var categoryLiveName: String = "",
    var desktopPreview: String = "",
    var mobilePreview: String = "",
    var phoneNumber: String = "",
    var screen: String? = null,
    var selectedCategory: String = "",
    var subCategoryID: String = "",
    var whatsappConsent: Boolean = true
)