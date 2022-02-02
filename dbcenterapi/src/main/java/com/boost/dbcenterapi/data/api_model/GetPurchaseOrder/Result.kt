package com.boost.dbcenterapi.data.api_model.GetPurchaseOrder

import com.boost.dbcenterapi.recycleritem.AppBaseRecyclerViewItem
import com.boost.dbcenterapi.recycleritem.RecyclerViewItemType
import java.io.Serializable

data class Result(
  val ComboPackageId: Any,
  val CreatedOn: String,
  val IsPartOfComboPlan: Boolean,
  val NameOfWidget: String,
  val PaymentMethod: String?,
  val ToBeActivatedOn: String,
  val _id: String,
  val _nfInternalERPId: Any,
  val _nfInternalERPInvoiceId: Any,
  val _nfInternalERPSaleDate: String,
  val baseAmount: Double,
  val clientId: String,
  val clientProductId: Any,
  val currencyCode: String,
  val discount: Double,
  val externalSourceId: Any,
  val fpId: String,
  val invoiceUrl: String,
  val isActive: Boolean,
  val isDynamicPackage: Boolean,
  val markedForDeletion: Boolean,
  val orderId: String?,
  val packageSaleTransactionType: Int,
  val paidAmount: Double,
  val paymentTransactionId: String,
  val purchasedPackageDetails: PurchasedPackageDetails,
  val taxAmount: Double,
  val totalMonthsValidity: Double,
  val widgetKey: Any
): Serializable, AppBaseRecyclerViewItem {

  override fun getViewType(): Int {
    return RecyclerViewItemType.RESULT.ordinal

  }
}