package com.inventoryorder.model.orderfilter

data class QueryObject(
  var Key: String? = null,
  var Value: String? = null,
  var QueryOperator: String? = null

) {
  enum class Operator {
    EQ, NE, GT, LT, GTE, LTE
  }

  enum class QueryKey(var value: String) {
    Identifier("SellerDetails.Identifier"),
    Mode("Mode"),
    DeliveryMode("LogisticsDetails.DeliveryMode"),
    DeliveryProvider("LogisticsDetails.DeliveryProvider"),
    Status("Status"),
    PaymentStatus("PaymentDetails.Status"),
    ReferenceNumber("ReferenceNumber"),
    BuyerFullName("BuyerDetails.ContactDetails.FullName"),
    BuyerPrimaryContactNumber("BuyerDetails.ContactDetails.PrimaryContactNumber")
  }

  enum class QueryValue {
    NF_VIDEO_CONSULATION
  }
}