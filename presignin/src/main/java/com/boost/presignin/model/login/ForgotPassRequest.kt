package com.boost.presignin.model.login

import com.framework.base.BaseRequest
import java.io.Serializable

data class ForgotPassRequest(
    val clientId: String? = null,
    val fpKey: String? = null,
):BaseRequest(),Serializable