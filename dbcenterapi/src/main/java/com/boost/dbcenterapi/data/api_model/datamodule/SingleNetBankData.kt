package com.boost.dbcenterapi.data.api_model.datamodule

data class SingleNetBankData(
  val bankCode: String,
  val bankName: String,
  val bankImage: String? = null
)