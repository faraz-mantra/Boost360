package com.boost.marketplace.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.boost.dbcenterapi.upgradeDB.local.AppDatabase
import com.boost.marketplace.R
import com.boost.marketplace.interfaces.AddonsListener
import com.boost.marketplace.ui.browse.BrowseFeaturesActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class BrowseParentFeaturesAdapter(
    var upgradeList: ArrayList<String>,
    val activity: BrowseFeaturesActivity,
    val addonsListener: AddonsListener
) : RecyclerView.Adapter<BrowseParentFeaturesAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val item = View.inflate(parent.context, R.layout.item_browse_features, null)
        return ViewHolder(item)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.title.setText(upgradeList.get(position))
        getFeaturesByType(holder, position)
        if(position==0){
            holder.options.visibility = View.VISIBLE
        }else{
            holder.options.visibility = View.GONE
        }

        holder.options.setOnClickListener {

        }
    }

    override fun getItemCount(): Int {
        return upgradeList.size
    }

    fun addupdates(upgradeModel: List<String>) {
        val initPosition = upgradeList.size
        upgradeList.clear()
        upgradeList.addAll(upgradeModel)
        notifyItemRangeInserted(initPosition, upgradeList.size)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title = itemView.findViewById<TextView>(R.id.title)
        val desc = itemView.findViewById<TextView>(R.id.desc)
        val options = itemView.findViewById<ImageView>(R.id.options)
        val recyclerview = itemView.findViewById<RecyclerView>(R.id.recyclerView)
    }


    fun getFeaturesByType(holder: ViewHolder, position: Int) {
        CompositeDisposable().add(
            AppDatabase.getInstance(activity.application)!!
                .featuresDao()
                .getFeaturesItemsByType(upgradeList.get(position), "MERCHANT_TRAINING")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    val adapter = BrowseChildFeaturesAdapter(it,addonsListener)
                    holder.recyclerview.adapter = adapter
                    adapter.notifyDataSetChanged()
                }, {
                    it.printStackTrace()
                })
        )
    }


}