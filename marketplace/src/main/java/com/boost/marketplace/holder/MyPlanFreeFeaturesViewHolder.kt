package com.boost.marketplace.holder

import android.app.Activity
import android.os.Bundle
import android.view.View
import com.boost.dbcenterapi.data.api_model.GetAllFeatures.response.MarketPlaceOffers
import com.boost.dbcenterapi.recycleritem.AppBaseRecyclerViewHolder
import com.boost.dbcenterapi.upgradeDB.model.FeaturesModel
import com.boost.marketplace.databinding.ItemMyplanFeaturesBinding
import com.bumptech.glide.Glide

class MyPlanFreeFeaturesViewHolder(binding: ItemMyplanFeaturesBinding) :
    AppBaseRecyclerViewHolder<ItemMyplanFeaturesBinding>(binding) {

    private var list = ArrayList<FeaturesModel>()

    override fun bind(position: Int, item: com.boost.dbcenterapi.recycleritem.BaseRecyclerViewItem)
    {
        super.bind(position, item)
        val cryptocurrencyItem = list[position]
        upgradeListItem(cryptocurrencyItem)
        var visibility: Boolean = false
        val isVisble: Boolean = true
        binding.detailsView.visibility = if (isVisble) View.VISIBLE else View.GONE
//        binding.mainLayout.setOnClickListener {
//            cryptocurrencyItem.visibility = !cryptocurrencyItem.visibility
//
//        }

    }
    fun upgradeListItem(updateModel: FeaturesModel) {
        binding.paidAddonsName.text = updateModel.name
        Glide.with(Activity()).load(updateModel.primary_image).into(binding.singlePaidaddonImage)

    }
}
