package dev.patrickgold.florisboard.customization.model.response

import java.util.*

data class CustomerDetails(
        var AliasTag: String? = null,
        var city: String? = null,
        var externalSourceId: String? = null,
        var imageUri: String? = null,
        var name: String? = null,
        var tag: String? = null,
        var address: String? = null,
        var contactName: String? = null,
        var email: String? = null,
        var primaryNumber: String? = null,
        var lat: Double = 0.0,
        var lng: Double = 0.0,
        var enterpriseEmailContact: String? = null,
        var fPWebWidgets: ArrayList<String>? = null,
        var Country: String? = null
)