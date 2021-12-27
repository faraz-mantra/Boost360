package com.boost.marketplace.holder

import android.os.Bundle
import android.view.View
import com.boost.dbcenterapi.recycleritem.AppBaseRecyclerViewHolder
import com.boost.dbcenterapi.recycleritem.BaseRecyclerViewItem
import com.boost.dbcenterapi.upgradeDB.model.FeaturesModel
import com.boost.marketplace.R
import com.boost.marketplace.databinding.ActivityPacksBinding
import com.boost.marketplace.databinding.ItemPacksListBinding
import com.bumptech.glide.Glide
import com.framework.glide.util.glideLoad

class Packs_ViewHolder(binding:ItemPacksListBinding)  :
    AppBaseRecyclerViewHolder<ItemPacksListBinding>(binding) {

    private var upgradeList = ArrayList<FeaturesModel>()
    var minMonth = 1

    override fun bind(position: Int, item: BaseRecyclerViewItem) {
        super.bind(position, item)

        binding.details.setText(upgradeList.get(position).name)
        binding.title.setText(upgradeList.get(position).target_business_usecase)

       Glide.with(activity!!).load(upgradeList.get(position).primary_image).into(binding.imageView2)

//        binding.itemView.setOnClickListener {
//            val details = DetailsFragment.newInstance()
//            val args = Bundle()
//            args.putString("itemId", upgradeList.get(position).feature_code)
//            args.putBoolean("packageView", true)
//            details.arguments = args
//
//            activity.addFragment(details, Constants.DETAILS_FRAGMENT)
//        }

        itemView.setOnClickListener {

        }




    }

    }
