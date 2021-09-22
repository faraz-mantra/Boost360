package com.dashboard.controller.ui.customisationnav.model

import com.dashboard.R
import com.dashboard.constant.RecyclerViewItemType
import com.dashboard.recyclerView.AppBaseRecyclerViewItem
import com.framework.base.BaseResponse
import com.framework.models.BaseViewModel
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class WebsiteNavModel(

	@field:SerializedName("website_customisation")
	var websiteCustomisation: List<WebsiteCustomisationItem?>? = null
):BaseResponse(),Serializable

data class WebsiteCustomisationItem(

	@field:SerializedName("subtitle")
	var subtitle: String? = null,

	@field:SerializedName("icon")
	var icon: String? = null,

	@field:SerializedName("title")
	var title: String? = null,

	@field:SerializedName("actionBtn")
	var actionBtn: ActionBtn? = null
): Serializable, AppBaseRecyclerViewItem {

	var recyclerViewItemType: Int = RecyclerViewItemType.RECYCLER_WEBSITE_NAV.getLayout()
	override fun getViewType(): Int {
		return recyclerViewItemType
	}

	enum class IconType(var icon: Int) {
		ic_fonts(R.drawable.ic_fonts),
		ic_background_images(R.drawable.ic_background_images),
		ic_diamonds(R.drawable.ic_diamonds),
		ic_favicon(R.drawable.ic_favicon),
		ic_featured_image(R.drawable.ic_featured_image);

		companion object {
			fun fromName(name: String?): IconType? =
				values().firstOrNull { it.name.equals(name, ignoreCase = true) }
		}
	}

}

data class ActionBtn(

	@field:SerializedName("color")
	var color: String? = null,

	@field:SerializedName("text_color")
	var textColor: String? = null,

	@field:SerializedName("title")
	var title: String? = null
)
