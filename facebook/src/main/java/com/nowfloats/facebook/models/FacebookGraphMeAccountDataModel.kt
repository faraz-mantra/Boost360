package com.nowfloats.facebook.models

data class FacebookGraphMeAccountDataModel(
    val access_token: String? = null,
    val category: String? = null,
    val category_list: ArrayList<CategoryModel> = ArrayList(),
    val id: String? = null,
    val name: String? = null,
    val tasks: ArrayList<String> = ArrayList()
)