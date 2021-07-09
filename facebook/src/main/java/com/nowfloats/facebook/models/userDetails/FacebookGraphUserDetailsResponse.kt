package com.nowfloats.facebook.models.userDetails

import com.nowfloats.facebook.models.BaseFacebookGraphResponse

data class FacebookGraphUserDetailsResponse(
    var email: String? = null,
    var name: String? = null,
    var id: String? = null,
    var error: String? = null
): BaseFacebookGraphResponse()