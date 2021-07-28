package com.nowfloats.facebook.models.userPages

import kotlin.collections.ArrayList

data class FacebookGraphUserPagesDataModel(
  val access_token: String? = null,
  val category: String? = null,
  val category_list: ArrayList<CategoryModel> = ArrayList(),
  val id: String? = null,
  val name: String? = null,
  val tasks: ArrayList<String> = ArrayList(),
  var profilePicture: String? = null
) {

  fun getPageUrl() {
    "https://www.facebook.com/${name?.replace(' ', '-') ?: ""}-${id ?: ""}"
  }

  fun getShopUrl(): String {
    return "${getPageUrl()}/shop/"
  }
}