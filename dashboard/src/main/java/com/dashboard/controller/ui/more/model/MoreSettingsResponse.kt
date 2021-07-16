package com.dashboard.controller.ui.more.model

import com.dashboard.constant.RecyclerViewItemType
import com.dashboard.recyclerView.AppBaseRecyclerViewItem
import com.framework.base.BaseResponse
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class MoreSettingsResponse(

	@field:SerializedName("useful_links")
	var usefulLinks: List<UsefulLinksItem?>? = null,

	@field:SerializedName("about_app_section")
	var aboutAppSection: List<AboutAppSectionItem?>? = null
): BaseResponse(),Serializable

data class AboutAppSectionItem(

	@field:SerializedName("subtitle")
	var subtitle: String? = null,

	@field:SerializedName("icon")
	var icon: String? = null,

	@field:SerializedName("title")
	var title: String? = null
):Serializable, AppBaseRecyclerViewItem {

	var recyclerViewItemType: Int = RecyclerViewItemType.RECYCLER_ABOUT_APP.getLayout()
	override fun getViewType(): Int {
		return recyclerViewItemType
	}
}

data class UsefulLinksItem(

	@field:SerializedName("isActionButtonEnabled")
	var isActionButtonEnabled: Boolean? = false,

	@field:SerializedName("actionBtn")
	var actionBtn: ActionBtn? = null,

	@field:SerializedName("subtitle")
	var subtitle: String? = null,

	@field:SerializedName("icon")
	var icon: String? = null,

	@field:SerializedName("title")
	var title: String? = null
):Serializable, AppBaseRecyclerViewItem {

	var recyclerViewItemType: Int = RecyclerViewItemType.RECYCLER_USEFUL_LINKS.getLayout()
	override fun getViewType(): Int {
		return recyclerViewItemType
	}
}
data class ActionBtn(

	@field:SerializedName("color")
	var color: String? = null,

	@field:SerializedName("title")
	var title: String? = null,

	@field:SerializedName("text_color")
	var textColor: String? = null
)
