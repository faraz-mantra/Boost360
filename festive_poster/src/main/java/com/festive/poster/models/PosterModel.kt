package com.festive.poster.models

import com.festive.poster.constant.RecyclerViewItemType
import com.festive.poster.recyclerView.AppBaseRecyclerViewItem
import com.framework.glide.customsvgloader.PosterKeyModel
import com.google.gson.Gson


open class PosterModel(
    val active: Boolean,
    val createdOn: String,
    val details: PosterDetailsModel,
    val id: String,
    var keys: List<PosterKeyModel>,
    val tags: List<String>,
    val updatedOn: String,
    val variants: List<PosterVariantModel>,
    var isPurchased:Boolean=false,
    var greeting_message:String?
): AppBaseRecyclerViewItem {
    override fun getViewType(): Int {
        return if (isPurchased) RecyclerViewItemType.POSTER_PURCHASED.getLayout() else RecyclerViewItemType.POSTER.getLayout()
    }

    fun clone(): PosterModel? {
        val stringAnimal = Gson().toJson(this, PosterModel::class.java)
        return Gson().fromJson(stringAnimal, PosterModel::class.java)
    }
}