package com.boost.payment.datamodule

data class SingleNetBankData(
  val bankCode: String,
  val bankName: String,
  val bankImage: String? = null
)