package com.dashboard.model

import com.dashboard.R
import com.dashboard.constant.RecyclerViewItemType
import com.dashboard.recyclerView.AppBaseRecyclerViewItem
import com.framework.base.BaseResponse

class ManageBusinessData(
    var title: String? = null,
    var icon1: Int? = null,
    var isLock: Boolean = false,
) : BaseResponse(), AppBaseRecyclerViewItem {

  var recyclerViewItemType: Int = RecyclerViewItemType.MANAGE_BUSINESS_ITEM_VIEW.getLayout()

  override fun getViewType(): Int {
    return recyclerViewItemType
  }

  fun getData(): ArrayList<ManageBusinessData> {
    val list = ArrayList<ManageBusinessData>()
    list.add(ManageBusinessData(title = "Projects and Teams", icon1 = R.drawable.ic_project_terms_d, isLock = true))
    list.add(ManageBusinessData(title = "Unlimited Digital Brochures", icon1 = R.drawable.ic_digital_brochures_d))
    list.add(ManageBusinessData(title = "Customer Calls", icon1 = R.drawable.ic_customer_call_d))
    list.add(ManageBusinessData(title = "Customer Enquiries", icon1 = R.drawable.ic_customer_enquiries_d))
    list.add(ManageBusinessData(title = "Daily Business Updates", icon1 = R.drawable.ic_daily_business_update_d))
    list.add(ManageBusinessData(title = "Products Catalogue", icon1 = R.drawable.ic_product_cataloge_d))
    list.add(ManageBusinessData(title = "Customer Testimonials", icon1 = R.drawable.ic_customer_testimonial_d))
    list.add(ManageBusinessData(title = "Business Keyboard", icon1 = R.drawable.ic_business_keyboard_d, isLock = true))
    return list
  }
}