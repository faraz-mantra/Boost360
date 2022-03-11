package com.boost.marketplace.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
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
        if(position == 0){
            holder.titleLayout.visibility = View.GONE
        }else{
            holder.titleLayout.visibility = View.VISIBLE
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
        val titleLayout = itemView.findViewById<LinearLayout>(R.id.feature_name_ll)
        val recyclerview = itemView.findViewById<RecyclerView>(R.id.recyclerView)
    }


    fun getFeaturesByType(holder: ViewHolder, position: Int) {
        if (position == 0) {
            CompositeDisposable().add(
                AppDatabase.getInstance(activity.application)!!
                    .featuresDao()
                    .getAllFeatures()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        val adapter = BrowseChildFeaturesAdapter(ArrayList(it.subList(0,4)) , addonsListener)
                        holder.recyclerview.adapter = adapter
                        adapter.notifyDataSetChanged()
                    }, {
                        it.printStackTrace()
                    })
            )
        } else {
            CompositeDisposable().add(
                AppDatabase.getInstance(activity.application)!!
                    .featuresDao()
                    .getFeaturesItemsByType(upgradeList.get(position), "MERCHANT_TRAINING")
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        val adapter = BrowseChildFeaturesAdapter(it, addonsListener)
                        holder.recyclerview.adapter = adapter
                        adapter.notifyDataSetChanged()
                    }, {
                        it.printStackTrace()
                    })
            )
        }
    }


}