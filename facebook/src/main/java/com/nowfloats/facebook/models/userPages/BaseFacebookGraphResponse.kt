package com.nowfloats.facebook.models.userPages

import com.nowfloats.facebook.models.BaseFacebookGraphResponse

data class FacebookGraphUserPagesResponse(
        val data: ArrayList<FacebookGraphUserPagesDataModel> = ArrayList(),
        val paging: PagingModel? = null,
        val error: FacebookGraphUserPagesErrorModel? = null
): BaseFacebookGraphResponse()