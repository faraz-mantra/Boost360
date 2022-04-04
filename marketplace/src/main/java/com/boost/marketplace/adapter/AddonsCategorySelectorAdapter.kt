package com.boost.marketplace.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.boost.dbcenterapi.upgradeDB.local.AppDatabase
import com.boost.marketplace.R
import com.boost.marketplace.interfaces.CategorySelectorListener
import com.framework.analytics.SentryController
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.lang.StringBuilder


class AddonsCategorySelectorAdapter(
  val activity: Activity,
  var upgradeList: List<String>,
  val listener: CategorySelectorListener
) : RecyclerView.Adapter<AddonsCategorySelectorAdapter.upgradeViewHolder>() {

  private lateinit var context: Context

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): upgradeViewHolder {
    val itemView = LayoutInflater.from(parent?.context).inflate(
      R.layout.addons_category_selector_list_item, parent, false
    )
    context = itemView.context

    return upgradeViewHolder(itemView)
  }

  override fun getItemCount(): Int {
    return upgradeList.size
  }

  override fun onBindViewHolder(holder: upgradeViewHolder, position: Int) {

    holder.title.setText(upgradeList.get(position))
    try {
      getFeaturesCount(holder, position)
    } catch (e: Exception) {
      SentryController.captureException(e)
    }
    holder.itemView.setOnClickListener {
      listener.onCategoryClicked(upgradeList.get(position))
    }
    if (position == upgradeList.size - 1) {
      holder.view.visibility = View.GONE
    }
  }

  fun addupdates(upgradeModel: List<String>) {
    upgradeList = upgradeModel
    notifyDataSetChanged()
  }

  class upgradeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var view = itemView.findViewById<View>(R.id.dummy_view)
    var title = itemView.findViewById<TextView>(R.id.title)
    var addonsList = itemView.findViewById<TextView>(R.id.addons_list)

  }

  fun getFeaturesCount(holder: upgradeViewHolder, position: Int) {
    CompositeDisposable().add(
      AppDatabase.getInstance(activity.application)!!
        .featuresDao()
        .getFeatureTypeCountPremium(upgradeList.get(position), "MERCHANT_TRAINING", true)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({
          holder.title.setText(upgradeList.get(position) + " (" + it + ")")
          getFeaturesByType(holder, position)
        }, {
          it.printStackTrace()
        })
    )
  }

  fun getFeaturesByType(holder: upgradeViewHolder, position: Int) {
      CompositeDisposable().add(
        AppDatabase.getInstance(activity.application)!!
          .featuresDao()
          .getFeaturesItemsByType(upgradeList.get(position), "MERCHANT_TRAINING")
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe({
            val temp = StringBuilder()
                for(singleitem in it){
                  temp.append(singleitem.name).append(", ")
                }
            holder.addonsList.setText(temp)
          }, {
            it.printStackTrace()
          })
      )
    }
}