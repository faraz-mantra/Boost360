package com.appservice.offers.models

import com.appservice.constant.RecyclerViewItemType
import com.appservice.model.KeySpecification
import com.appservice.model.servicev1.ImageModel
import com.appservice.recyclerView.AppBaseRecyclerViewItem
import com.framework.base.BaseResponse
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class OfferListingResponse(

		@field:SerializedName("StatusCode")
		val statusCode: Int? = null,

		@field:SerializedName("Result")
		val result: Result? = null,
) : Serializable, BaseResponse()

class OfferType {
	companion object {
		const val MERCHANT = 0
		const val SERVICE = 1
		const val PRODUCT = 2
	}
}

data class OfferModel(
		var isApplicableToAll: Boolean? = null,
		@SerializedName(value = "clientId", alternate = ["ClientId"])
		var ClientId: String? = null,
		@field:SerializedName("Description")
		var description: String? = null,
		@SerializedName(value = "FloatingPointTag", alternate = ["FPTag"])
		var FPTag: String? = null,
		@field:SerializedName("Category")
		var category: String? = null,

		@field:SerializedName("KeySpecifications")
		var keySpecifications: KeySpecification? = null,

		@field:SerializedName("BuyOnlineLink")
		var buyOnlineLink: String? = null,

		@field:SerializedName("DiscountAmount")
		var discountAmount: Double? = null,

		@field:SerializedName("OfferTimings")
		var offerTimings: OfferTimings? = null,

		@field:SerializedName("Name")
		var name: String? = null,

		@field:SerializedName("OfferType")
		var offerType: Int? = null,

		@field:SerializedName("OfferId")
		var offerId: String? = null,

		@field:SerializedName("CurrencyCode")
		var currencyCode: String? = null,

		@field:SerializedName("ReferenceId")
		var referenceId: String? = null,

		@field:SerializedName("Price")
		var price: Double? = null,

		@field:SerializedName("OtherSpecifications")
		var otherSpecifications: List<KeySpecification?>? = null,

		@field:SerializedName("IsAvailable")
		var isAvailable: Boolean? = null,

		@field:SerializedName("UniquePaymentUrl")
		var uniquePaymentUrl: Any? = null,

		@field:SerializedName("FeaturedImage")
		var featuredImage: FeaturedImage? = null,

		@field:SerializedName("SecondaryImages")
		var secondaryImages: List<ImageModel?>? = null,

		@field:SerializedName("Tags")
		var tags: ArrayList<String>? = null,
) : Serializable, BaseResponse(), AppBaseRecyclerViewItem {
	var recyclerViewItem = RecyclerViewItemType.OFFER_LISTING_VIEW.getLayout()

	override fun getViewType(): Int {
		return recyclerViewItem
	}

	fun getLoaderItem(): OfferModel {
		this.recyclerViewItem = RecyclerViewItemType.PAGINATION_LOADER.getLayout()
		return this
	}

	fun isServiceApplicableToAll(): Boolean {
		return false

	}
}

data class Result(

		@field:SerializedName("Paging")
		val paging: Paging? = null,

		@field:SerializedName("Data")
		val data: ArrayList<OfferModel>? = null,
) : Serializable, BaseResponse()

data class SecondaryImagesItem(

		@field:SerializedName("ImageId")
		val imageId: String? = null,

		@field:SerializedName("ActualImage")
		val actualImage: String? = null,

		@field:SerializedName("TileImage")
		val tileImage: String? = null,
) : Serializable, BaseResponse()

data class FeaturedImage(

		@field:SerializedName("ImageId")
		val imageId: String? = null,

		@field:SerializedName("ActualImage")
		val actualImage: String? = null,

		@field:SerializedName("TileImage")
		val tileImage: String? = null,
) : Serializable, BaseResponse()

data class TimingsItem(

		@field:SerializedName("From")
		val from: String? = null,

		@field:SerializedName("To")
		val to: String? = null,

		@field:SerializedName("Day")
		val day: String? = null,
) : Serializable, BaseResponse()

data class Paging(

		@field:SerializedName("Skip")
		val skip: Int? = null,

		@field:SerializedName("Limit")
		val limit: Int? = null,

		@field:SerializedName("Count")
		val count: Int? = null,
) : Serializable, BaseResponse()

data class OfferTimings(

		@field:SerializedName("Timings")
		val timings: List<TimingsItem?>? = null,

		@field:SerializedName("Duration")
		val duration: Int? = null,
) : Serializable, BaseResponse()
