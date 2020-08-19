package com.nowfloats.education.helper

import com.google.gson.annotations.SerializedName


class Error {
    @SerializedName("error")
    var error: String? = null
    @SerializedName("error_description")
    var error_description: String? = null
    @SerializedName("error_code")
    var error_code = 0

    constructor(message: String) {
        this.error = message
    }

    constructor(errorCode: Int, message: String) {
        this.error_code = errorCode
        this.error = message
    }

    constructor(errorCode: Int, message: String, errorDescription: String) {
        this.error_code = errorCode
        this.error = message
        this.error_description = errorDescription
    }

    fun getMessageWithCode() = "$error_code - $error"

    fun getMessageWithDesc() = "$error_code - $error\nDescription : $error_description"

}