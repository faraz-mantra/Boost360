package com.boost.upgrades.adapter

import android.content.Context
import android.os.Bundle
import android.text.SpannableString
import android.text.style.StrikethroughSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.biz2.nowfloats.boost.updates.persistance.local.AppDatabase
import com.boost.upgrades.R
import com.boost.upgrades.UpgradeActivity
import com.boost.upgrades.data.model.FeaturesModel
import com.boost.upgrades.interfaces.HomeListener
import com.boost.upgrades.ui.details.DetailsFragment
import com.boost.upgrades.utils.Constants
import com.bumptech.glide.Glide
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList


class AddonsCategoryAdapter(
        val activity: UpgradeActivity,
        cryptoCurrencies: List<String>?,
        val listener: HomeListener
) : RecyclerView.Adapter<AddonsCategoryAdapter.upgradeViewHolder>() {

    private var upgradeList = ArrayList<String>()
    private lateinit var context: Context

    init {
        this.upgradeList = cryptoCurrencies as ArrayList<String>
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): upgradeViewHolder {
        val itemView = LayoutInflater.from(parent?.context).inflate(
                R.layout.addons_category_list_item, parent, false
        )
        context = itemView.context

        return upgradeViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return upgradeList.size
    }

    override fun onBindViewHolder(holder: upgradeViewHolder, position: Int) {
        holder.title.setText(upgradeList.get(position))
        getFeaturesCount(holder, position)
        holder.itemView.setOnClickListener {
            listener.onAddonsCategoryClicked(upgradeList.get(position))
        }
        if (position == upgradeList.size - 1) {
            holder.view.visibility = View.GONE
        }
    }

    fun addupdates(upgradeModel: List<String>) {
        val initPosition = upgradeList.size
        upgradeList.clear()
        upgradeList.addAll(upgradeModel)
        notifyItemRangeInserted(initPosition, upgradeList.size)
    }

    class upgradeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var view = itemView.findViewById<View>(R.id.dummy_view)
        var title = itemView.findViewById<TextView>(R.id.title)

    }

    fun getFeaturesCount(holder: upgradeViewHolder, position: Int) {
        CompositeDisposable().add(
                AppDatabase.getInstance(activity.application)!!
                        .featuresDao()
                        .getFeatureTypeCount(upgradeList.get(position))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            holder.title.setText(upgradeList.get(position) + " (" + it + ")")
                        }, {
                            it.printStackTrace()
                        })
        )
    }
}