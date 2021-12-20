package com.boost.marketplace.holder

import com.boost.dbcenterapi.recycleritem.AppBaseRecyclerViewHolder
import com.boost.dbcenterapi.upgradeDB.model.FeaturesModel
import com.boost.marketplace.databinding.ItemMyplanFeaturesBinding

class MyPlanFreeFeaturesViewHolder (binding: ItemMyplanFeaturesBinding):
    AppBaseRecyclerViewHolder<ItemMyplanFeaturesBinding>(binding) {

    private var list = ArrayList<FeaturesModel>()

    override fun bind(
        position: Int,
        item: com.boost.dbcenterapi.recycleritem.BaseRecyclerViewItem) {
        super.bind(position, item)
    }
}