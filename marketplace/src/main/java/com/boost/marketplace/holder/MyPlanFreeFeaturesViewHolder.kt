package com.boost.marketplace.holder

import android.annotation.SuppressLint
import android.view.View
import android.widget.Toast
import com.boost.dbcenterapi.recycleritem.AppBaseRecyclerViewHolder
import com.boost.dbcenterapi.recycleritem.BaseRecyclerViewItem
import com.boost.dbcenterapi.recycleritem.RecyclerItemClickListener
import com.boost.marketplace.databinding.ItemMyplanFeaturesBinding
import com.boost.marketplace.infra.api.models.test.ViewpagerData
import com.boost.marketplace.infra.recyclerView.AppBaseRecyclerViewAdapter
import com.boost.marketplace.infra.api.models.test.Packs_Data as Packs_Data1

class MyPlanFreeFeaturesViewHolder(binding: ItemMyplanFeaturesBinding) :
    AppBaseRecyclerViewHolder<ItemMyplanFeaturesBinding>(binding), RecyclerItemClickListener {





    override fun bind(position: Int, item: com.boost.dbcenterapi.recycleritem.BaseRecyclerViewItem)
    {
        super.bind(position, item)



    }

    override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {

    }
//
//   override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
////        listener?.onItemClick(position, item, actionType)
////        binding.mainLayout.setOnClickListener {
////            Toast.makeText(getApplicationContext(), "Clicked ", Toast.LENGTH_LONG).show()
////        }
//    }
}


