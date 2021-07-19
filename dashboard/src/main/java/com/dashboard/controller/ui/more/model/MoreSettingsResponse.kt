package com.dashboard.controller.ui.more.model

import com.dashboard.R
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
	enum class IconType(var icon: Int) {
		service_product_catalogue(R.drawable.ic_product_cataloge_d),
		latest_update_tips(R.drawable.ic_daily_business_update_d),
		all_images(R.drawable.picture_gallery),
		business_profile(R.drawable.ic_customer_enquiries_d),
		testimonials(R.drawable.ic_customer_testimonial_d),
		custom_page(R.drawable.ic_custom_page_add),
		project_teams(R.drawable.ic_project_teams_d),
		unlimited_digital_brochures(R.drawable.ic_digital_brochures_d),
		toppers_institute(R.drawable.toppers_institute_d),
		upcoming_batches(R.drawable.ic_upcoming_batch_d),
		faculty_management(R.drawable.ic_upcoming_batch_d),
		places_look_around(R.drawable.places_look_around_d),
		trip_adviser_ratings(R.drawable.trip_advisor_reviews_d),
		seasonal_offers(R.drawable.ic_offer_d),
		website_theme(R.drawable.ic_website_theme);

		companion object {
			fun fromName(name: String?): IconType? =
				values().firstOrNull { it.name.equals(name, ignoreCase = true) }
		}
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
	enum class IconType(var icon: Int) {
		ria_digital_assistant(R.drawable.ria_digital_assistant),
		riv_business(R.drawable.riv_business),
		all_images(R.drawable.picture_gallery),
		business_profile(R.drawable.ic_customer_enquiries_d),
		testimonials(R.drawable.ic_customer_testimonial_d),
		custom_page(R.drawable.ic_custom_page_add),
		project_teams(R.drawable.ic_project_teams_d),
		unlimited_digital_brochures(R.drawable.ic_digital_brochures_d),
		toppers_institute(R.drawable.toppers_institute_d),
		upcoming_batches(R.drawable.ic_upcoming_batch_d),
		faculty_management(R.drawable.ic_upcoming_batch_d),
		places_look_around(R.drawable.places_look_around_d),
		trip_adviser_ratings(R.drawable.trip_advisor_reviews_d),
		seasonal_offers(R.drawable.ic_offer_d),
		website_theme(R.drawable.ic_website_theme);

		companion object {
			fun fromName(name: String?): IconType? =
				values().firstOrNull { it.name.equals(name, ignoreCase = true) }
		}
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
