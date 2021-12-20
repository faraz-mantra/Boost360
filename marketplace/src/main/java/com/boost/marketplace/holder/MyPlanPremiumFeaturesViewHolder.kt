package com.boost.marketplace.holder


import android.view.View
import com.boost.dbcenterapi.recycleritem.AppBaseRecyclerViewHolder
import com.boost.dbcenterapi.upgradeDB.model.FeaturesModel
import com.boost.marketplace.databinding.ItemMyplanFeaturesBinding

class MyPlanPremiumFeaturesViewHolder(binding: ItemMyplanFeaturesBinding):
    AppBaseRecyclerViewHolder<ItemMyplanFeaturesBinding>(binding) {

    private var list = ArrayList<FeaturesModel>()



    override fun bind(position: Int, item: com.boost.dbcenterapi.recycleritem.BaseRecyclerViewItem) {
        super.bind(position, item)
//        val currentItem = list[position]
//        binding.paidAddonsName.text = currentItem.heading
//        val isVisble: Boolean = currentItem.visibility
//
//        binding.detailsView.visibility = if (isVisble) View.VISIBLE else View.GONE
//        binding.mainLayout.setOnClickListener {
//            currentItem.visibility = !currentItem.visibility
//            notifyItemChanged(position)
//        }
//        holder.validity2.text=currentItem.validity2



    }



}