package com.appservice.staffs.model

import com.appservice.constant.RecyclerViewItemType
import com.appservice.recyclerView.AppBaseRecyclerViewItem
import java.io.Serializable

data class ExperienceModel(
    var title: String? = null,
    var value: Int? = null,
    var isSelected: Boolean = false,
    var recyclerViewItem: Int = RecyclerViewItemType.EXPERIENCE_RECYCLER_ITEM.getLayout(),
) : AppBaseRecyclerViewItem, Serializable {

  override fun getViewType(): Int {
    return recyclerViewItem
  }

  fun experienceData(): ArrayList<ExperienceModel> {
    val list = ArrayList<ExperienceModel>()
    list += ExperienceModel("0-1 Year", value = 0)
    list += ExperienceModel("1 Year", value = 1)
    list += ExperienceModel("2 Year’s", value = 2)
    list += ExperienceModel("3 Year’s", value = 3)
    list += ExperienceModel("4 Year’s", value = 4)
    list += ExperienceModel("5 Year’s", value = 5)
    list += ExperienceModel("5+ Year’s", value = 6)
    return list
  }
}