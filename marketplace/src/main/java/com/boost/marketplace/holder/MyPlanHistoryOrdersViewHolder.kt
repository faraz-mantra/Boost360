package com.boost.marketplace.holder

import com.boost.dbcenterapi.recycleritem.AppBaseRecyclerViewHolder
import com.boost.marketplace.databinding.ItemOrderHistoryBinding

class MyPlanHistoryOrdersViewHolder(binding:ItemOrderHistoryBinding):
    AppBaseRecyclerViewHolder<ItemOrderHistoryBinding>(binding)
{
    override fun bind(
        position: Int,
        item: com.boost.dbcenterapi.recycleritem.BaseRecyclerViewItem) {
        super.bind(position, item)
    }

}