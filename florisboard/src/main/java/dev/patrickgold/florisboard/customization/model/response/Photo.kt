package dev.patrickgold.florisboard.customization.model.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import dev.patrickgold.florisboard.R
import dev.patrickgold.florisboard.customization.adapter.BaseRecyclerItem
import dev.patrickgold.florisboard.customization.adapter.FeaturesEnum

data class Photo(
  @SerializedName("ImageURL")
  @Expose
  var imageUri: String? = null,
  @SerializedName("selected")
  @Expose
  var selected: Boolean = false,

  var gridType: ViewGridType = ViewGridType.FOUR_GRID
) : BaseRecyclerItem() {
  override fun getViewType(): Int = FeaturesEnum.PHOTOS.ordinal

  enum class ViewGridType(var width:Int,var countGrid:Int,var icon:Int){
    FIRST_GRID(280,1, R.drawable.ic_layout_one_f),SECOND_GRID(210,2,R.drawable.ic_layout_two_f),THIRD_GRID(164,3,R.drawable.ic_layout_three_f),FOUR_GRID(132,4,R.drawable.ic_layout_four_f),
  }
}