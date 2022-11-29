package com.appservice.model.aptsetting

import android.app.Activity
import android.text.Spanned
import androidx.annotation.ColorRes
import com.appservice.AppServiceApplication
import com.appservice.R
import com.appservice.base.getProductType
import com.appservice.base.isDoctorClinicProfile
import com.appservice.constant.RecyclerViewItemType
import com.appservice.model.kycData.BUSINESS_KYC_SAVE
import com.appservice.model.kycData.DataKyc
import com.appservice.recyclerView.AppBaseRecyclerViewItem
import com.appservice.utils.capitalizeUtil
import com.framework.BaseApplication
import com.framework.base.BaseResponse
import com.framework.pref.UserSessionManager
import com.framework.utils.*
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

  companion object{
    const val PREF_KEY="AppointmentStatusResponse_PREF"
    fun getFromPref(): AppointmentStatusResponse? {
      return convertStringToObj(PreferencesUtils.instance.getData(PREF_KEY, "") ?: "")
    }
  }


  fun saveInPref() {
    PreferencesUtils.instance.saveData(PREF_KEY,
      convertObjToString(this))
  }


}

enum class IconType(var icon: Int) {
  policies(R.drawable.ic_policies_for_customer),
  customer_invoice_setup(R.drawable.ic_customer_invoice),
  payment_collection(R.drawable.ic_payment_collection),
  delivery_setup(R.drawable.ic_delivery_ecomm_setup),
  consultation_settings(R.drawable.ic_consulation_setting_appt),
  catalog_setup(R.drawable.ic_business_services_pro),
  catalog_setup_ecommerce(R.drawable.ic_ecom_catalog_setup),
  business_verification(R.drawable.ic_business_verification);

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
      //add(AppointmentStatusResponse.TilesModel("customer_invoice_setup", "GST declaration, Bank UPI for offline appointments,signature", "Customer invoice setup", customerInvoicesSetup))
      //add(AppointmentStatusResponse.TilesModel("policies", "Refund, cancellation, privacy policies for your customers", "Policies for customers", policiesSetup))    }
      add(AppointmentStatusResponse.TilesModel("business_verification", "desc", "Business verification", customerInvoicesSetup))
    }
  }

  fun getEcommerceTilesArray(): ArrayList<AppointmentStatusResponse.TilesModel> {
    return arrayListOf(
      AppointmentStatusResponse.TilesModel("catalog_setup_ecommerce", "Service categories, Catalog display text,applicable tax slabs", "Catalog setup", this.catalogSetup),
      AppointmentStatusResponse.TilesModel("payment_collection", "Preferred payment gateway", "Payment collection setup", this.paymentCollectionSetup),
      AppointmentStatusResponse.TilesModel("delivery_setup", "GST declaration, Bank UPI for offline appointments,signature", "Delivery setup", this.deliverySetup),
      //AppointmentStatusResponse.TilesModel("customer_invoice_setup", "GST declaration, Bank UPI for offline appointments,signature", "Customer invoice setup", this.customerInvoicesSetup),
      //AppointmentStatusResponse.TilesModel("policies", "Refund, cancellation, privacy policies for your customers", "Policies for customers", this.policiesSetup)
      AppointmentStatusResponse.TilesModel("business_verification", "desc", "Business verification", customerInvoicesSetup)

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
  var isPending: Boolean? = null,
  @field:SerializedName("BankAccountVerificationStatus")
  var bankAccountVerificationStatus: String? = null
) : Serializable {

  fun getTitle(): Spanned? {
    return fromHtml("Mode selection: <i><b>${if (paymentGateway.isNullOrEmpty().not()) "$paymentGateway" else "<font color='#${getColorString()}'>Pending</font>"}</b></i>")
  }

  fun getSubtitle(): Spanned? {
    val isEmpty = this.bankAccountNumber.isNullOrEmpty() || isBankAccountConnected == false
    val title = if (isEmpty) "Bank account" else "Account no"
    return fromHtml("$title: <i><b>${if (isEmpty) "<font color='#${getColorString()}'>not added</font>" else getTestBankAccountStatus()}</b></i>")
  }

  fun getAccountStatus(): StatusSetting {
    return StatusSetting.fromName(bankAccountVerificationStatus) ?: StatusSetting.PENDING
  }

  fun getTestBankAccountStatus(): String {
    return when (getAccountStatus()) {
      StatusSetting.PENDING -> "<font color='#${getColorString(R.color.colorAccent)}'>Verification pending</font>"
      StatusSetting.FAILED -> "<font color='#${getColorString()}'>Verification failed</font>"
      StatusSetting.VERIFIED -> "$bankAccountNumber"
    }

  }

  fun isEmptyData(): Boolean {
    return (bankAccountNumber.isNullOrEmpty() || paymentGateway.isNullOrEmpty())
  }

  fun getBgColorUsingStatus(): Int {
    return if (!isEmptyData() && getAccountStatus() == StatusSetting.FAILED) R.color.red_E39595 else R.color.white
  }

  fun getPendingFailedIconUsingStatus(): Int {
    return if (!isEmptyData() && getAccountStatus() == StatusSetting.FAILED) R.drawable.ic_failed_status else R.drawable.ic_clock_filled
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
    val s = UserSessionManager(BaseApplication.instance)

    return fromHtml("Tax slab: <i><b>${if ((this.isDefaultGSTSlabSelected == false && this.defaultGSTSlab == null).not()|| isDoctorClinicProfile(s.fP_AppExperienceCode)) "${this.getGstSlabInt()}%" else "<font color='#${getColorString()}'>Not selected</font>"}</b></i>")
  }

  fun isEmptyData(): Boolean {
    return (productCategoryVerb.isNullOrEmpty() || defaultGSTSlab == null)
  }
}


data class CustomerInvoicesSetup(
  @field:SerializedName("GSTIN")
  var gSTIN: String? = null,
  @field:SerializedName("GSTINVerificationStatus")
  var GSTINVerificationStatus: String? = null,
  @field:SerializedName("PANNo")
  var panNo: String? = null,
  @field:SerializedName("PANVerificationStatus")
  var PANVerificationStatus: String? = null,
  @field:SerializedName("IsTaxInvoiceSetupComplete")
  var isTaxInvoiceSetupComplete: Boolean? = null,
  @field:SerializedName("IsPending")
  var isPending: Boolean? = null
) : Serializable {

  fun getGSTDeclarationComplete(): StatusSetting {
    return StatusSetting.fromName(GSTINVerificationStatus) ?: StatusSetting.PENDING
  }

  fun getPANVerificationStatus(): StatusSetting {
    return StatusSetting.fromName(PANVerificationStatus) ?: StatusSetting.PENDING
  }

  fun getTitle(): Spanned? {
    return fromHtml("GST declaration: <i><b>${if (getGstin().isNotEmpty()) getGstinTextStatus() else "<font color='#${getColorString()}'>Setup incomplete</font>"}</b></i>")
  }

  fun getGstin(): String {
    return if (gSTIN.isNullOrEmpty() || gSTIN.equals("null")) "" else gSTIN!!
  }

  fun getGstinTextStatus(): String {
    return when (getGSTDeclarationComplete()) {
      StatusSetting.PENDING -> "Verification pending"
      StatusSetting.FAILED -> getGstin()
      StatusSetting.VERIFIED -> "<font color='#${getColorString()}'>Verification failed</font>"
    }
  }

  fun getPanNumberStatus(): String {
    return when (getPANVerificationStatus()) {
      StatusSetting.PENDING -> "Verification pending"
      StatusSetting.FAILED -> getPanNumber()
      StatusSetting.VERIFIED -> "<font color='#${getColorString()}'>Verification failed</font>"
    }
  }

  fun getPanNumber(): String {
    return if (panNo.isNullOrEmpty() || panNo.equals("null")) "" else panNo!!
  }

  fun getSubtitle(): Spanned? {
    return fromHtml("PAN No: <i><b>${if (getPanNumber().isNotEmpty()) getPanNumberStatus() else "<font color='#${getColorString()}'>Setup incomplete</font>"}</b></i>")
  }

  fun isEmptyData(): Boolean {
    return (gSTIN.isNullOrEmpty() || panNo.isNullOrEmpty())
  }

  fun getBgColorUsingStatus():Int{
   return if (!isEmptyData() && (getGSTDeclarationComplete() == StatusSetting.FAILED || getPANVerificationStatus() == StatusSetting.FAILED)) {
      R.color.red_E39595
    } else R.color.white
  }

  fun getPendingFailedIconUsingStatus(): Int {
    return if (!isEmptyData() && (getGSTDeclarationComplete() == StatusSetting.FAILED || getPANVerificationStatus() == StatusSetting.FAILED)) R.drawable.ic_failed_status else R.drawable.ic_clock_filled
  }
}


data class ConsultationSetup(
  @field:SerializedName("IsGeneralAppointmentEnabled")
  var isGeneralAppointmentEnabled: Boolean? = null,
  @field:SerializedName("IsPending")
  var isPending: Boolean? = null
) : Serializable {

  fun getIsEnableText(): String {
    return "<i><b>${if (isGeneralAppointmentEnabled == true) "Enabled" else "<font color='#${getColorString()}'>Not selected</font>"}</b></i>"
  }
}

fun getColorString(@ColorRes color: Int = R.color.color_error_code): String {
  val labelColor: Int = AppServiceApplication.instance.resources.getColor(color)
  return String.format("%X", labelColor).substring(2)
}

enum class StatusSetting {
  PENDING, FAILED, VERIFIED;

  companion object {
    fun fromName(name: String?): StatusSetting? = values().firstOrNull { it.name.equals(name, ignoreCase = true) }
  }
}
