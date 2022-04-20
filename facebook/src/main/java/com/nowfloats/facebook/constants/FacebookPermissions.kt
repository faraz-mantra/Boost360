package com.nowfloats.facebook.constants

import java.util.ArrayList

enum class FacebookPermissions {
  pages_manage_instant_articles,
  pages_manage_metadata,
  pages_messaging,
  catalog_management,
  email,
  public_profile,
  read_insights,
  business_management,
  pages_show_list,
  pages_manage_cta,
  manage_pages,
  publish_pages,
  ads_management,
  pages_read_engagement,
  pages_manage_posts,
  pages_read_user_content,
  instagram_basic,
  instagram_content_publish,
  instagram_manage_insights;

  companion object {
    fun fromName(name: String): FacebookPermissions? = values().firstOrNull { name.trim().contains(it.name) }
    fun permissionValues(permissions: List<String>): ArrayList<FacebookPermissions> {
      val listPermission = ArrayList<FacebookPermissions>()
      permissions.forEach { fromName(it)?.apply { listPermission.add(this) } }
      return listPermission
    }
  }

}


