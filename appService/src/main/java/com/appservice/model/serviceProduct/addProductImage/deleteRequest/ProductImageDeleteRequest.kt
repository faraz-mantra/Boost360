package com.appservice.model.serviceProduct.addProductImage.deleteRequest


import com.framework.base.BaseRequest
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ProductImageDeleteRequest(
    @SerializedName("Multi")
    var multi: Boolean = false,
    @SerializedName("Query")
    var query: String? = null
) : BaseRequest(), Serializable {

  fun setQueryData(id: String?) {
    this.query = String.format("{'_id':'%s'}", id)
  }
}