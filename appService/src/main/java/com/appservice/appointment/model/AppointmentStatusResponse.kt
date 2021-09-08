package com.appservice.appointment.model

import android.text.Spanned
import com.appservice.R
import com.appservice.constant.RecyclerViewItemType
import com.framework.base.BaseResponse
import com.framework.utils.fromHtml
import com.google.gson.annotations.SerializedName
import com.inventoryorder.recyclerView.AppBaseRecyclerViewItem
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
    var recyclerViewItem: Int = RecyclerViewItemType.CATALOG_SETTING_TILES.getLayout()
  ) : Serializable, AppBaseRecyclerViewItem, com.appservice.recyclerView.AppBaseRecyclerViewItem {
    override fun getViewType(): Int {
      return recyclerViewItem
    }
  }


}

enum class IconType(var icon: Int) {
  policies(R.drawable.ic_policies_for_customer),
  customer_invoice_setup(R.drawable.ic_customer_invoice),
  payment_collection(R.drawable.ic_payment_collection),
  catalog_setup(R.drawable.ic_business_services_pro);

  companion object {
    fun fromName(name: String?): IconType? =
      values().firstOrNull { it.name.equals(name, ignoreCase = true) }
  }

}

data class ResultS(

  @field:SerializedName("CustomerInvoicesSetup")
  var customerInvoicesSetup: CustomerInvoicesSetup? = null,

  @field:SerializedName("PoliciesSetup")
  var policiesSetup: PoliciesSetup? = null,

  @field:SerializedName("CatalogSetup")
  var catalogSetup: CatalogSetup? = null,

  @field:SerializedName("ConsultationSetup")
  var consultationSetup: ConsultationSetup? = null,

  @field:SerializedName("PaymentCollectionSetup")
  var paymentCollectionSetup: PaymentCollectionSetup? = null
) {

  fun getTilesArray(): ArrayList<AppointmentStatusResponse.TilesModel> {
    return arrayListOf(
      AppointmentStatusResponse.TilesModel("catalog_setup", "Service categories, Catalog display text,applicable tax slabs", "Catalog Setup", this.catalogSetup),
      AppointmentStatusResponse.TilesModel("payment_collection", "Preferred payment gateway", "Payment collection setup", this.paymentCollectionSetup),
      AppointmentStatusResponse.TilesModel("customer_invoice_setup", "GST declaration, Bank UPI for offline appointments,signature", "Customer invoice setup", this.customerInvoicesSetup)
//      ,
//      AppointmentStatusResponse.TilesModel("policies", "Refund, cancellation, privacy policies for your customers", "Policies for customers", this.policiesSetup)
    )
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
){
  fun getTitle(): Spanned? {
    return fromHtml("Payment gateway: <b>$paymentGateway</b>")
  }
  fun getSubtitle(): Spanned? {
    return fromHtml(if (this.bankAccountNumber==null||this.bankAccountNumber==""||isBankAccountConnected==false) "<pre> Bank account: <span style=\"color: #EB5757;\"><em>not connected</em></span></pre>" else "Bank account: <b>$bankAccountNumber</b>")
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
)


data class CatalogSetup(

  @field:SerializedName("DefaultGSTSlab")
  var defaultGSTSlab: Double? = null,

  @field:SerializedName("ProductCategoryVerb")
  var productCategoryVerb: String? = null,

  @field:SerializedName("IsDefaultGSTSlabSelected")
  var isDefaultGSTSlabSelected: Boolean? = null,

  @field:SerializedName("IsPending")
  var isPending: Boolean? = null
) {
  fun getTitle(): Spanned? {
    return fromHtml("Display text: <b>$productCategoryVerb</b>")
  }
  fun getSubtitle(): Spanned? {
    return fromHtml(if (this.isDefaultGSTSlabSelected==false&&this.defaultGSTSlab==0.0) "<pre>Applicable tax slabs: <span style=\"color: #EB5757;\"><em>not selected</em></span></pre>" else "Applicable tax slabs: <b>${this.defaultGSTSlab}</b>")
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
){
  fun getTitle(): Spanned? {
    return if (isGSTDeclarationComplete==true) fromHtml("<pre>GST declaration: <span style=\"color: #EB5757;\"><em>incomplete</em></span></pre>")
    else fromHtml("<pre>GST declaration: <strong>$gSTIN</strong>&nbsp;</pre>")
  }
  fun getSubtitle(): Spanned? {
    return fromHtml(" ${if (this.isTaxInvoiceSetupComplete==true) "<pre>Tax invoice: <span style=\"color: #EB5757;\"><em>Setup incomplete</em></span></pre>" else "Tax invoice: <b>Setup completed</b>"}")
  }
}


data class ConsultationSetup(

  @field:SerializedName("IsGeneralAppointmentEnabled")
  var isGeneralAppointmentEnabled: Boolean? = null,

  @field:SerializedName("IsPending")
  var isPending: Boolean? = null
)
