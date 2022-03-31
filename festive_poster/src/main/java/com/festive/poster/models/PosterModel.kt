package com.festive.poster.models

import com.festive.poster.constant.RecyclerViewItemType
import com.festive.poster.recyclerView.AppBaseRecyclerViewItem
import com.framework.glide.customsvgloader.PosterKeyModel
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName


open class PosterModel(
    @SerializedName("active")
    val active: Boolean?=null,
    @SerializedName("createdOn")
    val createdOn: String?=null,
    @SerializedName("details")
    val details: PosterDetailsModel?=null,
    @SerializedName("id")
    val id: String?=null,
    @SerializedName("keys")
    var keys: List<PosterKeyModel>?=null,
    @SerializedName("tags")
    val tags: List<String>?=null,
    @SerializedName("updatedOn")
    val updatedOn: String?=null,
    @SerializedName("variants")
    val variants: List<PosterVariantModel>?=null,
    var greeting_message:String?=null,
    var layout_id:Int,
    var shouldShowFavOn:Boolean=false,
    ): AppBaseRecyclerViewItem {
    override fun getViewType(): Int {
        return layout_id
    }

    fun clone(): PosterModel? {
        val stringAnimal = Gson().toJson(this, PosterModel::class.java)
        return Gson().fromJson(stringAnimal, PosterModel::class.java)
    }

    var isPurchased:Boolean=false
    get() = details?.isPurchased == true

    fun url(): String? {
        return variants?.firstOrNull()?.svgUrl
    }



}