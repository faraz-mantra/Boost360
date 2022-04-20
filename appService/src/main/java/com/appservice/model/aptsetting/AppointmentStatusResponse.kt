package com.appservice.model.aptsetting

import android.app.Activity
import android.text.Spanned
import com.appservice.AppServiceApplication
import com.appservice.R
import com.appservice.constant.RecyclerViewItemType
import com.appservice.recyclerView.AppBaseRecyclerViewItem
import com.appservice.ui.aptsetting.ui.getProductType
import com.appservice.utils.capitalizeUtil
import com.framework.base.BaseResponse
import com.framework.pref.UserSessionManager
import com.framework.utils.fromHtml
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class AppointmentStatusResponse(
  @field:SerializedName("StatusCode")
  var statusCode: Int? = null,
  @field:SerializedName("Result")
  var result: ResultS? = null
) : BaseResponse(), Serializable {

  data class TilesModel(
    var icon: String? = null,
    var description: String? = null,
    var title: String? = null,
    var tile: Any? = null,
    var isEnabled: Boolean? = false,
  ) : Serializable, AppBaseRecyclerViewItem {

    override fun getViewType(): Int {
      return RecyclerViewItemType.CATALOG_SETTING_TILES.getLayout()
    }
  }
}

enum class IconType(var icon: Int) {
  policies(R.drawable.ic_policies_for_customer),
  customer_invoice_setup(R.drawable.ic_customer_invoice),
  payment_collection(R.drawable.ic_payment_collection),
  delivery_setup(R.drawable.ic_delivery_ecomm_setup),
  catalog_setup(R.drawable.ic_business_services_pro),
  catalog_setup_ecommerce(R.drawable.ic_ecom_catalog_setup);

  companion object {
    fun fromName(name: String?): IconType? =
      values().firstOrNull { it.name.equals(name, ignoreCase = true) }
  }

}

data class ResultS(


  @field:SerializedName("CustomerInvoicesSetup")
  var customerInvoicesSetup: CustomerInvoicesSetup? = null,

  @field:SerializedName("DeliverySetup")
  var deliverySetup: DeliverySetupTile? = null,

  @field:SerializedName("PoliciesSetup")
  var policiesSetup: PoliciesSetup? = null,

  @field:SerializedName("CatalogSetup")
  var catalogSetup: CatalogSetup? = null,

  @field:SerializedName("ConsultationSetup")
  var consultationSetup: ConsultationSetup? = null,

  @field:SerializedName("PaymentCollectionSetup")
  var paymentCollectionSetup: PaymentCollectionSetup? = null
) : Serializable {

  fun getAppointmentTilesArray(): ArrayList<AppointmentStatusResponse.TilesModel> {
    return arrayListOf(
      AppointmentStatusResponse.TilesModel("catalog_setup", "Service categories, Catalog display text,applicable tax slabs", "Catalog setup", this.catalogSetup),
      AppointmentStatusResponse.TilesModel("payment_collection", "Preferred payment gateway", "Payment collection setup", this.paymentCollectionSetup),
      AppointmentStatusResponse.TilesModel("customer_invoice_setup", "GST declaration, Bank UPI for offline appointments,signature", "Customer invoice setup", this.customerInvoicesSetup)
//      AppointmentStatusResponse.TilesModel("policies", "Refund, cancellation, privacy policies for your customers", "Policies for customers", this.policiesSetup)
    )
  }

  fun getEcommerceTilesArray(): ArrayList<AppointmentStatusResponse.TilesModel> {
    return arrayListOf(
      AppointmentStatusResponse.TilesModel("catalog_setup_ecommerce", "Service categories, Catalog display text,applicable tax slabs", "Catalog setup", this.catalogSetup),
      AppointmentStatusResponse.TilesModel("payment_collection", "Preferred payment gateway", "Payment collection setup", this.paymentCollectionSetup),
      AppointmentStatusResponse.TilesModel("customer_invoice_setup", "GST declaration, Bank UPI for offline appointments,signature", "Customer invoice setup", this.customerInvoicesSetup),
      AppointmentStatusResponse.TilesModel("delivery_setup", "GST declaration, Bank UPI for offline appointments,signature", "Delivery setup", this.deliverySetup)
//      AppointmentStatusResponse.TilesModel("policies", "Refund, cancellation, privacy policies for your customers", "Policies for customers", this.policiesSetup)
    )
  }
}

data class DeliverySetupTile(
  @field:SerializedName("IsHomeDeliveryAllowed")
  var isHomeDeliveryAllowed: Boolean? = null,
  @field:SerializedName("IsPickupAllowed")
  var isPickUpAllowed: Boolean? = null,
  @field:SerializedName("IsPending")
  var isPending: Boolean? = null
) : Serializable {

  fun getTitle(): Spanned? {
    return fromHtml(if (this.isPending == true || this.isHomeDeliveryAllowed == false || isPickUpAllowed == false) "<pre> Mode: <span style=\"color: #EB5757;\"><em>not selected</em></span></pre>" else if (isHomeDeliveryAllowed == true && isPickUpAllowed == true) "Mode:<b> Home delivery, Self pickup</b>" else if (isHomeDeliveryAllowed == true) "Mode:<b> Home delivery</b>" else "Mode:<b> Self Pickup</b>")
  }
}

data class PaymentCollectionSetup(
  @field:SerializedName("IsBankAccountConnected")
  var isBankAccountConnected: Boolean? = null,
  @field:SerializedName("PaymentGateway")
  var paymentGateway: String? = null,
  @field:SerializedName("BankAccountNumber")
  var bankAccountNumber: String? = null,
  @field:SerializedName("IsPending")
  var isPending: Boolean? = null
) : Serializable {

  fun getTitle(): Spanned? {
    return fromHtml("Payment gateway: <b>${if (paymentGateway.isNullOrEmpty()) "Pending" else "$paymentGateway"}</b>")
  }

  fun getSubtitle(): Spanned? {
    return fromHtml(if (this.bankAccountNumber == null || this.bankAccountNumber == "" || isBankAccountConnected == false) "<pre> Bank account: <span style=\"color: #EB5757;\"><em>not connected</em></span></pre>" else "Bank account: <b>$bankAccountNumber</b>")
  }
}

data class PoliciesSetup(

  @field:SerializedName("IsPrivacyPolicySetup")
  var isPrivacyPolicySetup: Boolean? = null,

  @field:SerializedName("IsRefundPolicySetup")
  var isRefundPolicySetup: Boolean? = null,

  @field:SerializedName("IsCancellationPolicySetup")
  var isCancellationPolicySetup: Boolean? = null,

  @field:SerializedName("IsReturnPolicySetup")
  var isReturnPolicySetup: Boolean? = null,

  @field:SerializedName("IsPending")
  var isPending: Boolean? = null
) : Serializable


data class CatalogSetup(
  @field:SerializedName("DefaultGSTSlab")
  var defaultGSTSlab: Double? = null,
  @field:SerializedName("ProductCategoryVerb")
  var productCategoryVerb: String? = null,
  @field:SerializedName("IsDefaultGSTSlabSelected")
  var isDefaultGSTSlabSelected: Boolean? = null,
  @field:SerializedName("IsPending")
  var isPending: Boolean? = null
) : Serializable {

  fun getGstSlabInt():Int{
    return defaultGSTSlab?.toInt()?:0
  }
  fun getTitle(activity: Activity?): Spanned? {
    val s = UserSessionManager(activity ?: AppServiceApplication.instance)
    return fromHtml("Display text: <b>${if (productCategoryVerb.isNullOrEmpty()) getProductType(s.fP_AppExperienceCode) else "${productCategoryVerb?.capitalizeUtil()}"}</b>")
  }

  fun getSubtitle(): Spanned? {
    return fromHtml(if (this.isDefaultGSTSlabSelected == false && this.defaultGSTSlab == 0.0) "<pre>Applicable tax slabs: <span style=\"color: #EB5757;\"><em>not selected</em></span></pre>" else "Applicable tax slabs: <b>${this.defaultGSTSlab}</b>")
  }
}


data class CustomerInvoicesSetup(
  @field:SerializedName("IsGSTDeclarationComplete")
  var isGSTDeclarationComplete: Boolean? = null,
  @field:SerializedName("GSTIN")
  var gSTIN: String? = null,
  @field:SerializedName("IsTaxInvoiceSetupComplete")
  var isTaxInvoiceSetupComplete: Boolean? = null,
  @field:SerializedName("IsPending")
  var isPending: Boolean? = null
) : Serializable {
  fun getTitle(): Spanned? {
    return if (isGSTDeclarationComplete == false || getGstin().isEmpty()) fromHtml("<pre>GST declaration: <span style=\"color: #EB5757;\"><em>incomplete</em></span></pre>")
    else fromHtml("<pre>GST declaration: <strong>${getGstin()}</strong>&nbsp;</pre>")
  }

  fun getGstin(): String {
    return if (gSTIN.isNullOrEmpty() || gSTIN.equals("null")) "" else gSTIN!!
  }

  fun getSubtitle(): Spanned? {
    return fromHtml(" ${if (this.isTaxInvoiceSetupComplete == false) "<pre>Tax invoice: <span style=\"color: #EB5757;\"><em>Setup incomplete</em></span></pre>" else "Tax invoice: <b>Setup completed</b>"}")
  }
}


data class ConsultationSetup(
  @field:SerializedName("IsGeneralAppointmentEnabled")
  var isGeneralAppointmentEnabled: Boolean? = null,
  @field:SerializedName("IsPending")
  var isPending: Boolean? = null
) : Serializable
