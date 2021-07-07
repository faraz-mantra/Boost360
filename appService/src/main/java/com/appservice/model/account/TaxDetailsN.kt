package com.appservice.model.account


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class TaxDetailsN(
  @SerializedName("GSTDetails")
  var gSTDetails: GSTDetailsN? = null,
  @SerializedName("PanDetails")
  var panDetails: PanDetailsN? = null,
  @SerializedName("TanDetails")
  var tanDetails: TanDetailsN? = null
) : Serializable {

  fun taxOb(): TaxDetailsN {
    return TaxDetailsN(
      gSTDetails = GSTDetailsN("", "", "", ""),
      panDetails = PanDetailsN("", "", "", "", ""),
      tanDetails = TanDetailsN("", "", "", "")
    )
  }
}