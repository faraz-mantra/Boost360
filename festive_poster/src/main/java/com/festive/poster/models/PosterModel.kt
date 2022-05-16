package com.festive.poster.models

import com.festive.poster.constant.RecyclerViewItemType
import com.festive.poster.recyclerView.AppBaseRecyclerViewItem
import com.framework.glide.customsvgloader.PosterKeyModel
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName


open class PosterModel(
    @SerializedName("active")
    val active: Boolean?,
    @SerializedName("createdOn")
    val createdOn: String?,
    @SerializedName("details")
    val details: PosterDetailsModel?,
    @SerializedName("id")
    val id: String?,
    @SerializedName("keys")
    var keys: List<PosterKeyModel>?,
    @SerializedName("tags")
    val tags: List<String>?,
    @SerializedName("updatedOn")
    val updatedOn: String?,
    @SerializedName("variants")
    val variants: List<PosterVariantModel>?,
    var greeting_message:String?,
    var shareLayout:Boolean=false
): AppBaseRecyclerViewItem {
    override fun getViewType(): Int {
        return if (shareLayout) RecyclerViewItemType.POSTER_SHARE.getLayout() else RecyclerViewItemType.POSTER.getLayout()
    }

    fun clone(): PosterModel? {
        val stringAnimal = Gson().toJson(this, PosterModel::class.java)
        return Gson().fromJson(stringAnimal, PosterModel::class.java)
    }

    var isPurchased:Boolean=false
    get() = details?.isPurchased == true

}