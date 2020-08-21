package com.nowfloats.education.model

data class ErrorModel(
        val ErrorId: String,
        val Errors: List<Error>
)

data class Error(
        val ErrorMessage: String,
        val MemberNames: List<Any>
)