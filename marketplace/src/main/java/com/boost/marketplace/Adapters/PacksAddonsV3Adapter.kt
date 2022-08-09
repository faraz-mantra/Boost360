package com.boost.marketplace.Adapters

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.boost.dbcenterapi.data.api_model.packageAddonsCompares.PackageAddonsCompares
import com.boost.marketplace.R
import com.boost.marketplace.interfaces.AddonsListenerV3

class PacksAddonsV3Adapter(
    cryptoCurrencies: List<PackageAddonsCompares>?,
    val activity: Activity,var myAddonsListener: AddonsListenerV3
) : RecyclerView.Adapter<PacksAddonsV3Adapter.upgradeViewHolder>() {

    private var upgradeList = ArrayList<PackageAddonsCompares>()
    private lateinit var context: Context

    init {
        this.upgradeList = cryptoCurrencies as ArrayList<PackageAddonsCompares>
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): upgradeViewHolder {
        val itemView = LayoutInflater.from(parent?.context).inflate(
            R.layout.item_packsv3_addons, parent, false
        )
        context = itemView.context
        return upgradeViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return upgradeList.size
    }

    override fun onBindViewHolder(holder: upgradeViewHolder, position: Int) {
        holder.name.setText(upgradeList.get(position).title)
        val adapter = PacksAddonsV3ImageAdapter(upgradeList.get(position).packsAvailableIn, activity)
        holder.recyclerView.adapter = adapter
        if(position == upgradeList.size-1){
            holder.dummyLine.visibility = View.GONE
        }
        holder.name.setOnClickListener {

            myAddonsListener.onPaidAddonsClicked(upgradeList.get(position).featureCode)
        }
    }

    fun addupdates(upgradeModel: List<PackageAddonsCompares>) {
        val initPosition = upgradeList.size
        upgradeList.clear()
        upgradeList.addAll(upgradeModel)
        notifyItemRangeInserted(initPosition, upgradeList.size)
    }

    class upgradeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name = itemView.findViewById<TextView>(R.id.addon_title)!!
        val recyclerView = itemView.findViewById<RecyclerView>(R.id.addons_available_recycler)!!
        val dummyLine = itemView.findViewById<View>(R.id.dummy_view)!!
    }
}