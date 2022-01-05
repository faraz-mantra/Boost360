package com.boost.marketplace.holder

import android.app.Activity
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.boost.dbcenterapi.recycleritem.AppBaseRecyclerViewHolder
import com.boost.dbcenterapi.upgradeDB.model.FeaturesModel
import com.boost.marketplace.R
import com.boost.marketplace.constant.RecyclerViewActionType
import com.boost.marketplace.databinding.ItemMyplanFeaturesBinding
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog

class MyPlanPremiumFeaturesViewHolder(binding: ItemMyplanFeaturesBinding) :
    AppBaseRecyclerViewHolder<ItemMyplanFeaturesBinding>(binding) {

    private var list = ArrayList<FeaturesModel>()


    override fun bind(
        position: Int,
        item: com.boost.dbcenterapi.recycleritem.BaseRecyclerViewItem) {
        super.bind(position, item)

        binding.mainLayout.setOnClickListener {
            if (binding.detailsView.visibility == View.GONE) {
                TransitionManager.beginDelayedTransition(binding.mainLayout, AutoTransition())
                binding.detailsView.visibility = View.VISIBLE
            } else {
                TransitionManager.beginDelayedTransition(binding.mainLayout, AutoTransition())
                binding.detailsView.visibility = View.GONE
            }
        }
        binding.detailsInfo.setOnClickListener {
            listener?.onItemClick(position, item, RecyclerViewActionType.MY_PLAN_PREMIUM_CLICK.ordinal)

        }


    }

    fun upgradeListItem(updateModel: FeaturesModel) {
        binding.paidAddonsName.text = updateModel.name
        Glide.with(Activity()).load(updateModel.primary_image)
            .into(binding.singlePaidaddonImage)
    }
}



