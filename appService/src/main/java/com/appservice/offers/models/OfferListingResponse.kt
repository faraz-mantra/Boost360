package com.appservice.offers.models

import com.appservice.constant.RecyclerViewItemType
import com.appservice.recyclerView.AppBaseRecyclerViewItem
import com.framework.base.BaseResponse
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class OfferListingResponse(

		@field:SerializedName("StatusCode")
		val statusCode: Int? = null,

		@field:SerializedName("Result")
		val result: Result? = null,
): Serializable, BaseResponse()

data class DataItem(

		@field:SerializedName("Description")
		val description: String? = null,

		@field:SerializedName("Category")
		val category: String? = null,

		@field:SerializedName("KeySpecifications")
		val keySpecifications: Any? = null,

		@field:SerializedName("BuyOnlineLink")
		val buyOnlineLink: Any? = null,

		@field:SerializedName("DiscountAmount")
		val discountAmount: Double? = null,

		@field:SerializedName("OfferTimings")
		val offerTimings: OfferTimings? = null,

		@field:SerializedName("Name")
		val name: String? = null,

		@field:SerializedName("OfferType")
		val offerType: Int? = null,

		@field:SerializedName("OfferId")
		val offerId: String? = null,

		@field:SerializedName("CurrencyCode")
		val currencyCode: String? = null,

		@field:SerializedName("ReferenceId")
		val referenceId: String? = null,

		@field:SerializedName("Price")
		val price: Double? = null,

		@field:SerializedName("OtherSpecifications")
		val otherSpecifications: List<Any?>? = null,

		@field:SerializedName("IsAvailable")
		val isAvailable: Boolean? = null,

		@field:SerializedName("UniquePaymentUrl")
		val uniquePaymentUrl: Any? = null,

		@field:SerializedName("FeaturedImage")
		val featuredImage: FeaturedImage? = null,

		@field:SerializedName("SecondaryImages")
		val secondaryImages: List<Any?>? = null,

		@field:SerializedName("Tags")
		val tags: Any? = null,
) : Serializable, BaseResponse(), AppBaseRecyclerViewItem {

	var recyclerViewItem = RecyclerViewItemType.OFFER_LISTING_VIEW.getLayout()

	override fun getViewType(): Int {
		return recyclerViewItem
	}

	fun getLoaderItem(): DataItem {
		this.recyclerViewItem = RecyclerViewItemType.PAGINATION_LOADER.getLayout()
		return this
	}
}

data class Result(

		@field:SerializedName("Paging")
		val paging: Paging? = null,

		@field:SerializedName("Data")
		val data: ArrayList<DataItem>? = null,
): Serializable, BaseResponse()

data class SecondaryImagesItem(

		@field:SerializedName("ImageId")
		val imageId: String? = null,

		@field:SerializedName("ActualImage")
		val actualImage: String? = null,

		@field:SerializedName("TileImage")
		val tileImage: String? = null,
): Serializable, BaseResponse()

data class FeaturedImage(

		@field:SerializedName("ImageId")
		val imageId: String? = null,

		@field:SerializedName("ActualImage")
		val actualImage: String? = null,

		@field:SerializedName("TileImage")
		val tileImage: String? = null,
): Serializable, BaseResponse()

data class TimingsItem(

		@field:SerializedName("From")
		val from: String? = null,

		@field:SerializedName("To")
		val to: String? = null,

		@field:SerializedName("Day")
		val day: String? = null,
): Serializable, BaseResponse()

data class Paging(

		@field:SerializedName("Skip")
		val skip: Int? = null,

		@field:SerializedName("Limit")
		val limit: Int? = null,

		@field:SerializedName("Count")
		val count: Int? = null,
): Serializable, BaseResponse()

data class OfferTimings(

		@field:SerializedName("Timings")
		val timings: List<TimingsItem?>? = null,

		@field:SerializedName("Duration")
		val duration: Int? = null,
): Serializable, BaseResponse()
