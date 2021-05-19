package com.dashboard.model.websitetheme

import com.dashboard.constant.RecyclerViewItemType
import com.dashboard.recyclerView.AppBaseRecyclerViewItem
import com.framework.base.BaseResponse
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class WebsiteThemeResponse(

		@field:SerializedName("StatusCode")
		var statusCode: Int? = null,

		@field:SerializedName("Result")
		var result: Result? = null,
) : BaseResponse(), Serializable

data class Result(

		@field:SerializedName("ThemeId")
		var themeId: String? = null,

		@field:SerializedName("Colors")
		var colors: ArrayList<ColorsItem>? = null,

		@field:SerializedName("Fonts")
		var fonts: FontsList? = null,
) : Serializable

data class PrimaryItem(

		@field:SerializedName("Description")
		var description: String? = null,

		@field:SerializedName("DefaultFont")
		var defaultFont: Boolean? = null,

		@field:SerializedName("IsSelected")
		var isSelected: Boolean? = null,

		@field:SerializedName("Url")
		var url: String? = null,
		var recyclerViewItemType: Int = RecyclerViewItemType.WEBSITE_FONT_VIEW.getLayout(),

		) : Serializable, AppBaseRecyclerViewItem {
	override fun getViewType(): Int {
		return recyclerViewItemType
	}
}

data class FontsList(

		@field:SerializedName("Secondary")
		var secondary: List<SecondaryItem?>? = null,

		@field:SerializedName("Primary")
		var primary: List<PrimaryItem?>? = null,
) : Serializable

data class ColorsItem(

		@field:SerializedName("Secondary")
		var secondary: String? = null,

		@field:SerializedName("Tertiary")
		var tertiary: String? = null,

		@field:SerializedName("Primary")
		var primary: String? = null,

		@field:SerializedName("IsSelected")
		var isSelected: Boolean? = null,

		@field:SerializedName("DefaultColor")
		var defaultColor: Boolean? = null,

		@field:SerializedName("Name")
		var name: String? = null,
		var recyclerViewItemType: Int = RecyclerViewItemType.WEBSITE_COLOR_VIEW.getLayout(),

		) : Serializable, AppBaseRecyclerViewItem {
	override fun getViewType(): Int {
		return recyclerViewItemType
	}
}

data class SecondaryItem(

		@field:SerializedName("Description")
		var description: String? = null,

		@field:SerializedName("DefaultFont")
		var defaultFont: Boolean? = null,

		@field:SerializedName("IsSelected")
		var isSelected: Boolean? = null,

		@field:SerializedName("Url")
		var url: String? = null,
		var recyclerViewItemType: Int = RecyclerViewItemType.WEBSITE_FONT_VIEW.getLayout(),


		) : Serializable, AppBaseRecyclerViewItem {
	override fun getViewType(): Int {
		return recyclerViewItemType
	}
}

