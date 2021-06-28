package com.dashboard.model.websitetheme

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class WebsiteThemeUpdateRequest(

		@field:SerializedName("Customization")
		var customization: Customization? = null,

		@field:SerializedName("FloatingPointId")
		var floatingPointId: String? = null,
):Serializable

data class Customization(

		@field:SerializedName("Colors")
		var colors: Colors? = null,

		@field:SerializedName("Fonts")
		var fonts: Fonts? = null,
)


data class Fonts(

		@field:SerializedName("Secondary")
		var secondary: SecondaryItem? = null,

		@field:SerializedName("Primary")
		var primary: PrimaryItem? = null,
)


data class Colors(

		@field:SerializedName("Secondary")
		var secondary: String? = null,

		@field:SerializedName("Tertiary")
		var tertiary: String? = null,

		@field:SerializedName("Primary")
		var primary: String? = null,

		@field:SerializedName("Name")
		var name: String? = null,
)
