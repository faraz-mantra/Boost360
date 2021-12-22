package com.boost.marketplace.holder

import com.boost.dbcenterapi.recycleritem.AppBaseRecyclerViewHolder
import com.boost.dbcenterapi.recycleritem.BaseRecyclerViewItem
import com.boost.marketplace.databinding.ActivityPacksBinding
import com.boost.marketplace.databinding.ItemPacksListBinding

class Packs_ViewHolder(binding:ItemPacksListBinding)  :
    AppBaseRecyclerViewHolder<ItemPacksListBinding>(binding) {

    override fun bind(position: Int, item: BaseRecyclerViewItem) {
        super.bind(position, item)
    }
}