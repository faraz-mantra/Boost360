package com.boost.dbcenterapi.data.api_model.GetAllFeatures.response

import com.boost.dbcenterapi.recycleritem.AppBaseRecyclerViewItem
import com.boost.dbcenterapi.recycleritem.RecyclerViewItemType
import java.io.Serializable

data class Bundles(
  val _kid: String,
  val included_features: List<IncludedFeature>,
  val min_purchase_months: Int?,
  val name: String?,
  val overall_discount_percent: Double,
  val primary_image: PrimaryImage?,
  val target_business_usecase: String?,
  val exclusive_to_categories: List<String>?,
  val exclusive_for_customers: List<String>?,
  val how_to_activate: List<HowToActivate>?,
  val testimonials: List<Testimonial>?,
  val frequently_asked_questions: List<FrequentlyAskedQuestion>?,
  val benefits: List<String>?,
  val desc: String?,
  val exclusive_To_clientIds:List<String>? =null
):Serializable,AppBaseRecyclerViewItem {

  var recyclerViewItem =  RecyclerViewItemType.PACKS.ordinal

  override fun getViewType(): Int {
    return recyclerViewItem
  }
}