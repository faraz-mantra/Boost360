package com.festive.poster.models.response

import retrofit2.http.Query

data class GetTodaysTemplateBody(
   val floatingPointId:String?,
   val floatingPointTag:String?,
) {
}