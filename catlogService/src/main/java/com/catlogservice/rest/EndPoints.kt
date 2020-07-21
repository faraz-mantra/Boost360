package com.catlogservice.rest

object EndPoints {

  //TODO NFX API WITH FLOAT
  const val WITH_FLOATS_BASE = "https://api.withfloats.com/"
  const val USER_ACCOUNT_DETAIL = "discover/v9/business/paymentProfile/{fpId}"
  const val CREATE_PAYMENT = "discover/v9/business/paymentProfile/create"
  const val UPDATE_PAYMENT = "discover/v9/business/paymentProfile/bankDetails/update/{fpId}"


  //TODO NFX API 2 WITH FLOAT
  const val WITH_FLOATS_TWO_BASE = "https://api2.withfloats.com/"
  const val CREATE_SERVICE = "Product/v1/Create"

  //TODO NFX API 2 WITH FLOAT
  const val RAZOR_API_BASE = "https://ifsc.razorpay.com/"
  const val RAZOR_IFSC_DETAIL = "{ifsc}"

}

