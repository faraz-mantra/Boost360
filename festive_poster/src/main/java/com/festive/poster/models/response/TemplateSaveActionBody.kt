package com.festive.poster.models.response

data class TemplateSaveActionBody(
    val action: String,
   // val extraProperties: List<ExtraProperty>,
    val favourite: Boolean,
    val floatingPointId: String,
    val floatingPointTag: String,
    val templateId: String
){
    enum class ActionType{
        UPDATE_CREATED,
        FAVOURITE,
        SHARE,
        VIEW_DETAILS
    }
}