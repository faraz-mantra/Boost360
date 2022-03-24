package com.appservice.model.aptsetting

import android.app.Activity
import android.text.Spanned
import com.appservice.AppServiceApplication
import com.appservice.R
import com.appservice.base.getProductType
import com.appservice.constant.RecyclerViewItemType
import com.appservice.recyclerView.AppBaseRecyclerViewItem
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
  consultation_settings(R.drawable.ic_consulation_setting_appt),
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

  fun getAppointmentTilesArray(isDoctor: Boolean): ArrayList<AppointmentStatusResponse.TilesModel> {
    return ArrayList<AppointmentStatusResponse.TilesModel>().apply {
      add(AppointmentStatusResponse.TilesModel("catalog_setup", "Service categories, Catalog display text,applicable tax slabs", "Catalog setup", catalogSetup))
      if (isDoctor) add(AppointmentStatusResponse.TilesModel("consultation_settings", "General appointment: #", "General appointments", consultationSetup))
      add(AppointmentStatusResponse.TilesModel("payment_collection", "Preferred payment gateway", "Payment collection setup", paymentCollectionSetup))
      add(AppointmentStatusResponse.TilesModel("customer_invoice_setup", "GST declaration, Bank UPI for offline appointments,signature", "Customer invoice setup", customerInvoicesSetup))
      //add(AppointmentStatusResponse.TilesModel("policies", "Refund, cancellation, privacy policies for your customers", "Policies for customers", policiesSetup))    }
    }
  }

  fun getEcommerceTilesArray(): ArrayList<AppointmentStatusResponse.TilesModel> {
    return arrayListOf(
      AppointmentStatusResponse.TilesModel("catalog_setup_ecommerce", "Service categories, Catalog display text,applicable tax slabs", "Catalog setup", this.catalogSetup),
      AppointmentStatusResponse.TilesModel("payment_collection", "Preferred payment gateway", "Payment collection setup", this.paymentCollectionSetup),
      AppointmentStatusResponse.TilesModel("customer_invoice_setup", "GST declaration, Bank UPI for offline appointments,signature", "Customer invoice setup", this.customerInvoicesSetup),
      AppointmentStatusResponse.TilesModel("delivery_setup", "GST declaration, Bank UPI for offline appointments,signature", "Delivery setup", this.deliverySetup)
      //AppointmentStatusResponse.TilesModel("policies", "Refund, cancellation, privacy policies for your customers", "Policies for customers", this.policiesSetup)
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
    return fromHtml("Payment gateway: <i><b>${if (paymentGateway.isNullOrEmpty().not()) "$paymentGateway" else "<font color='#${getColorInactive()}'>Pending</font>"}</b></i>")

  }

  fun getSubtitle(): Spanned? {
    return fromHtml("Bank account: <i><b>${if ((this.bankAccountNumber == null || this.bankAccountNumber == "" || isBankAccountConnected == false).not()) "$bankAccountNumber" else "<font color='#${getColorInactive()}'>Not connected</font>"}</b></i>")
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

  fun getGstSlabInt(): Int {
    return defaultGSTSlab?.toInt() ?: 0
  }

  fun getTitle(activity: Activity?): Spanned? {
    val s = UserSessionManager(activity ?: AppServiceApplication.instance)
    return fromHtml("Display text: <i><b>${if (productCategoryVerb.isNullOrEmpty()) getProductType(s.fP_AppExperienceCode) else "${productCategoryVerb?.capitalizeUtil()}"}</b><i>")
  }

  fun getSubtitle(): Spanned? {
    return fromHtml("Tax slab: <i><b>${if ((this.isDefaultGSTSlabSelected == false && this.defaultGSTSlab == 0.0).not()) "${this.defaultGSTSlab}" else "<font color='#${getColorInactive()}'>Not selected</font>"}</b></i>")
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
    return fromHtml("GST declaration: <i><b>${if (isGSTDeclarationComplete == true) "${getGstin()}" else "<font color='#${getColorInactive()}'>Setup incomplete</font>"}</b></i>")

  }

  fun getGstin(): String {
    return if (gSTIN.isNullOrEmpty() || gSTIN.equals("null")) "" else gSTIN!!
  }

  fun getSubtitle(): Spanned? {
    return fromHtml("Tax invoice: <i><b>${if (isTaxInvoiceSetupComplete == true) "Setup completed" else "<font color='#${getColorInactive()}'>Setup incomplete</font>"}</b></i>")

  }
}


data class ConsultationSetup(
  @field:SerializedName("IsGeneralAppointmentEnabled")
  var isGeneralAppointmentEnabled: Boolean? = null,
  @field:SerializedName("IsPending")
  var isPending: Boolean? = null
) : Serializable {

  fun getIsEnableText(): String {
    return "<i><b>${if (isGeneralAppointmentEnabled == true) "Enabled" else "<font color='#${getColorInactive()}'>Not selected</font>"}</b></i>"
  }
}

fun getColorInactive(): String {
  val labelColor: Int = AppServiceApplication.instance.resources.getColor(R.color.color_error_code)
  return String.format("%X", labelColor).substring(2)
}
