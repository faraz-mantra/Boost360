package com.boost.marketplace.holder

import android.view.View
import com.boost.dbcenterapi.data.api_model.GetPurchaseOrder.Result
import com.boost.dbcenterapi.recycleritem.AppBaseRecyclerViewHolder
import com.boost.marketplace.databinding.ItemHistoryOrdersBindingImpl

import java.lang.Long
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class MyPlanHistoryOrdersViewHolder(binding:ItemHistoryOrdersBindingImpl):
    AppBaseRecyclerViewHolder<ItemHistoryOrdersBindingImpl>(binding)
{

    private var list = ArrayList<Result>()

    override fun bind(
        position: Int,
        item: com.boost.dbcenterapi.recycleritem.BaseRecyclerViewItem) {
        super.bind(position, item)

//        val itemLists = StringBuilder()
//        if (list.get(position).purchasedPackageDetails.WidgetPacks.size > 1) {
//            for (item in 0 until list.get(position).purchasedPackageDetails.WidgetPacks.size) {
//                binding.title.append(list.get(position).purchasedPackageDetails.WidgetPacks.get(item).Name)
//                if (item != list.get(position).purchasedPackageDetails.WidgetPacks.size - 1) {
//                    binding.title.append(", ")
//                }
//            }
//
//        } else {
//            binding.title.append(list.get(position).purchasedPackageDetails.WidgetPacks.get(0).Name)
//
//        }
//        binding.title.text = itemLists
//
//        val dataString = list.get(position).CreatedOn
//        val date = Date(Long.parseLong(dataString.substring(6, dataString.length - 2)))
//        val dateFormat = SimpleDateFormat("dd-MMM-yyyy (HH:mm)")
//        binding.validity.text = dateFormat.format(date)
//
   }

}