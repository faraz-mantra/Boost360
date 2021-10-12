package com.festive.poster.models

import com.festive.poster.R
import com.festive.poster.recyclerView.AppBaseRecyclerViewItem
import com.google.gson.Gson


open class PosterModel(
    val Active: Boolean,
    val CreatedOn: String,
    val Details: PosterDetailsModel,
    val Id: String,
    var Keys: List<PosterKeyModel>,
    val Tags: List<String>,
    val UpdatedOn: String,
    val Variants: List<PosterVariantModel>,
    var isPurchased:Boolean=false,
    var greeting_message:String?
): AppBaseRecyclerViewItem {
    override fun getViewType(): Int {
        return if (isPurchased)
            R.layout.list_item_purchased_poster
        else
            R.layout.list_item_poster
    }

    fun clone(): PosterModel? {
        val stringAnimal = Gson().toJson(this, PosterModel::class.java)
        return Gson().fromJson(stringAnimal, PosterModel::class.java)
    }
}