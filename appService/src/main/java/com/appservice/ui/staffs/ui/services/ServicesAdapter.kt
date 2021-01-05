package com.appservice.ui.staffs.ui.services

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.appservice.R
import com.framework.views.customViews.CustomCheckBox

class ServicesAdapter : RecyclerView.Adapter<ServicesAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_item_service, null, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.customCheckBox.text = ServicesModel.allServices[position]
    }

    override fun getItemCount(): Int {
        return ServicesModel.allServices.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var customCheckBox: CustomCheckBox = itemView.findViewById(R.id.ccb_services)

    }
}