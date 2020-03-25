package com.nowfloats.facebook.models

data class FacebookGraphMeAccountResponse(
        val data: ArrayList<FacebookGraphMeAccountDataModel> = ArrayList(),
        val paging: PagingModel? = null,
        val error: FacebookGraphMeAccountErrorModel? = null
)