package com.inventoryorder.model.orderRequest

import com.framework.base.BaseRequest
import com.framework.utils.DateUtils
import com.google.gson.annotations.SerializedName
import com.inventoryorder.constant.RecyclerViewItemType
import com.inventoryorder.model.ordersdetails.ExtraPropertiesN
import com.inventoryorder.recyclerView.AppBaseRecyclerViewItem
import java.io.Serializable

data class OrderInitiateRequestNew(

	@field:SerializedName("Mode")
	var mode: String? = null,

	@field:SerializedName("ShippingDetails")
	var shippingDetails: ShippingDetailsN? = null,

	@field:SerializedName("SellerID")
	var sellerID: String? = null,

	@field:SerializedName("Items")
	var items: List<ItemsItemNew?>? = null,

	@field:SerializedName("PaymentDetails")
	var paymentDetails: PaymentDetailsN? = null,

	@field:SerializedName("TransactionCharges")
	var transactionCharges: Int? = null,

	@field:SerializedName("BuyerDetails")
	var buyerDetails: BuyerDetailsN? = null
): BaseRequest(), Serializable {
	var isVideoConsult: Boolean = false

}

data class AppointmentItem(

	@field:SerializedName("duration")
	var duration: String? = null,

	@field:SerializedName("scheduledDay")
	var scheduledDay: String? = null,

	@field:SerializedName("scheduledDateTime")
	var scheduledDateTime: String? = null,

	@field:SerializedName("staffName")
	var staffName: String? = null,

	@field:SerializedName("startTime")
	var startTime: String? = null,

	@field:SerializedName("_id")
	var id: String? = null,

	@field:SerializedName("endTime")
	var endTime: String? = null,

	@field:SerializedName("staffId")
	var staffId: String? = null
)

data class ItemsItemNew(

	@field:SerializedName("Description")
	var description: String? = null,

	@field:SerializedName("Quantity")
	var quantity: Int? = 0,

	@field:SerializedName("ImageUri")
	var imageUri: String? = null,

	@field:SerializedName("DiscountAmount")
	var discountAmount: Double? = null,

	@field:SerializedName("Name")
	var name: String? = null,

	@field:SerializedName("CurrencyCode")
	var currencyCode: String? = null,

	@field:SerializedName("Type")
	var type: String? = null,

	@field:SerializedName("Price")
	var price: Double? = null,

	@field:SerializedName("ProductDetails")
	var productDetails: ProductDetailsN? = null,

	@field:SerializedName("IsAvailable")
	var isAvailable: Boolean? = null,

	@field:SerializedName("ShippingCost")
	var shippingCost: Double? = null,

	@field:SerializedName("_id")
	var id: String? = null,

	@field:SerializedName("ProductOrOfferId")
	var productOrOfferId: String? = null
): Serializable, AppBaseRecyclerViewItem {

	var recyclerViewItem: Int = RecyclerViewItemType.PRODUCT_ITEM_SELECTED.getLayout()

	override fun getViewType(): Int {
		return recyclerViewItem
	}

//	fun getActualPriceAmount(): Double {
//		return productDetails?.getActualPrice()?.times(quantity?:0) ?: 0.0
//	}
//
//	fun getPayablePriceAmount(): Double {
//		return productDetails?.getPayablePrice()?.times(quantity?:0) ?: 0.0
//	}
//
//	fun getTotalDisPriceAmount(): Double {
//		return productDetails?.getDiscountAmount()?.times(quantity?:0) ?: 0.0
//	}
}

data class ProductDetailsN(

	@field:SerializedName("ExtraProperties")
	var extraProperties: ExtraPropertieN? = null
)

data class BuyerDetailsN(

	@field:SerializedName("Address")
	var address: AddressN? = null,

	@field:SerializedName("ContactDetails")
	var contactDetails: ContactDetailsN? = null
)

data class AddressN(

	@field:SerializedName("AddressLine1")
	var addressLine1: String? = null,

	@field:SerializedName("Zipcode")
	var zipcode: String? = null,

	@field:SerializedName("Country")
	var country: String? = null,

	@field:SerializedName("Region")
	var region: String? = null,

	@field:SerializedName("City")
	var city: String? = null
)

data class PaymentDetailsN(

	@field:SerializedName("Method")
	var method: String? = null
)

data class ExtraPropertieN(

	@field:SerializedName("Appointment")
	var appointment: List<AppointmentItem?>? = null,

	@field:SerializedName("businessLicense")
	var businessLicense: String? = null,

	@field:SerializedName("patientName")
	var patientName: String? = null,

	@field:SerializedName("doctorSpeciality")
	var doctorSpeciality: String? = null,

	@field:SerializedName("consultationFor")
	var consultationFor: String? = null,

	@field:SerializedName("patientMobileNumber")
	var patientMobileNumber: String? = null,

	@field:SerializedName("gender")
	var gender: String? = null,

	@field:SerializedName("businessLogo")
	var businessLogo: String? = null,

	@field:SerializedName("businessName")
	var businessName: String? = null,

	@field:SerializedName("patientEmailId")
	var patientEmailId: String? = null,

	@field:SerializedName("scheduledDateTime")
	var scheduledDateTime: String? = null,

	@field:SerializedName("doctorQualification")
	var doctorQualification: String? = null,

	@field:SerializedName("doctorSignature")
	var doctorSignature: String? = null,

	@field:SerializedName("referenceId")
	var referenceId: String? = null,

	@field:SerializedName("duration")
	var duration: Int? = null,

	@field:SerializedName("doctorName")
	var doctorName: String? = null,

	@field:SerializedName("appointmentMessage")
	var appointmentMessage: String? = null,

	@field:SerializedName("doctorId")
	var doctorId: String? = null,

	@field:SerializedName("startTime")
	var startTime: String? = null,

	@field:SerializedName("endTime")
	var endTime: String? = null,

	@field:SerializedName("age")
	var age: Int? = null
): Serializable {

	fun startTime(): String {
		return startTime ?: ""
	}

	fun endTime(): String {
		return endTime ?: ""
	}

	fun getScheduledDateN(): String? {
		var dateString = DateUtils.parseDate(
			scheduledDateTime,
			DateUtils.FORMAT_SERVER_DATE,
			DateUtils.FORMAT_YYYY_MM_DD
		)
		if (dateString.isNullOrEmpty()) dateString = DateUtils.parseDate(
			scheduledDateTime,
			DateUtils.FORMAT_SERVER_1_DATE,
			DateUtils.FORMAT_YYYY_MM_DD
		)
		return dateString ?: ""
	}
}

data class ContactDetailsN(

	@field:SerializedName("EmailId")
	var emailId: String? = null,

	@field:SerializedName("FullName")
	var fullName: String? = null,

	@field:SerializedName("PrimaryContactNumber")
	var primaryContactNumber: String? = null
)

data class ShippingDetailsN(

	@field:SerializedName("CurrencyCode")
	var currencyCode: String? = null,

	@field:SerializedName("ShippingCost")
	var shippingCost: Double? = null,

	@field:SerializedName("DeliveryMode")
	var deliveryMode: String? = null,

	@field:SerializedName("ShippedBy")
	var shippedBy: String? = null
)  
enum class ShippedBy {
	SELLER
}

enum class DeliveryProvider {
	NF_VIDEO_CONSULATION
}
