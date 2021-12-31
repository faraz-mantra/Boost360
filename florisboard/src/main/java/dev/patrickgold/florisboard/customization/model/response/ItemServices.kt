package dev.patrickgold.florisboard.customization.model.response

import com.appservice.model.serviceProduct.service.ItemsItem
import com.google.gson.annotations.SerializedName
import dev.patrickgold.florisboard.customization.adapter.BaseRecyclerItem
import dev.patrickgold.florisboard.customization.adapter.FeaturesEnum
import java.io.Serializable

data class ItemServices(
  @SerializedName("Data")
  var data: ItemsItem? = null
) : Serializable, BaseRecyclerItem() {

  var recyclerViewItem: Int = FeaturesEnum.SERVICES.ordinal

  override fun getViewType(): Int {
    return recyclerViewItem
  }
  fun getLoaderItem(): ItemServices {
    this.recyclerViewItem = FeaturesEnum.LOADER.ordinal
    return this
  }
}

fun getCopyNewItems(list: ArrayList<ItemsItem?>?): ArrayList<ItemServices> {
  return ArrayList<ItemServices>().apply { list?.forEach { it?.let { add(ItemServices(it))} } }
}