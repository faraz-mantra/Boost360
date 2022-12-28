package com.boost.dbcenterapi.data.api_model.GetAllFeatures.response

import com.boost.dbcenterapi.recycleritem.AppBaseRecyclerViewItem
import com.boost.dbcenterapi.recycleritem.RecyclerViewItemType
import java.io.Serializable

data class PartnerZone(
  val _kid: String,
  val _parentClassId: String,
  val _parentClassName: String,
  val _propertyName: String,
  val createdon: String,
  val cta_feature_key: String,
  val cta_web_link: String,
  val cta_bundle_identifier: String,
  val exclusive_to_categories: List<String>,
  val exclusive_to_customers: List<String>,
  val image: Image,
  val importance: Int,
  val isarchived: Boolean,
  val title: String,
  val updatedon: String,
  val websiteid: String
):Serializable, AppBaseRecyclerViewItem {

  var recyclerViewItem =  RecyclerViewItemType.PARTNER.ordinal


  override fun getViewType(): Int {
    return recyclerViewItem
  }
}