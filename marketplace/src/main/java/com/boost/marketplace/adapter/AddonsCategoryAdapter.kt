package com.boost.marketplace.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.boost.dbcenterapi.upgradeDB.local.AppDatabase
import com.boost.dbcenterapi.upgradeDB.model.FeaturesModel
import com.boost.marketplace.R
import com.boost.marketplace.interfaces.HomeListener
import com.boost.marketplace.ui.home.MarketPlaceActivity
import com.framework.analytics.SentryController
import com.framework.utils.toArrayList
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers


class AddonsCategoryAdapter(
  val activity: MarketPlaceActivity,
  cryptoCurrencies: List<String>?,
  val listener: HomeListener
) : RecyclerView.Adapter<AddonsCategoryAdapter.upgradeViewHolder>() {

  private var upgradeList = ArrayList<String>()
  private var featuresList = ArrayList<FeaturesModel>()
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
    when (upgradeList.get(position)) {
      "Marketing" -> {
        holder.image.setImageResource(R.drawable.addons_category_marketing)
      }
      "Communication" -> {
        holder.image.setImageResource(R.drawable.addons_category_communication)
      }
      "Identity" -> {
        holder.image.setImageResource(R.drawable.addons_category_identity)
      }
      "Content Management" -> {
        holder.image.setImageResource(R.drawable.addons_category_contentmanagement)
      }
      "Support" -> {
        holder.image.setImageResource(R.drawable.addons_category_support)
      }
      "E-Commerce" -> {
        holder.image.setImageResource(R.drawable.addons_category_ecommerce)
      }
      "Reports" -> {
        holder.image.setImageResource(R.drawable.addons_category_reports)
      }
      "Security" -> {
        holder.image.setImageResource(R.drawable.addons_category_security)
      }
      "Booking" -> {
        holder.image.setImageResource(R.drawable.addons_category_booking)
      }
      "Catalogue" -> {
        holder.image.setImageResource(R.drawable.addons_category_catalogue)
      }
      "Staff Management" -> {
        holder.image.setImageResource(R.drawable.ic_staff)
      }

    }
    holder.title.setText(upgradeList.get(position))
    try {
      getFeaturesCount(holder, position)
    } catch (e: Exception) {
      SentryController.captureException(e)
    }
    holder.itemView.setOnClickListener {
      listener.onAddonsCategoryClicked(upgradeList.get(position))
    }
    if (position == upgradeList.size - 1) {
      holder.view.visibility = View.GONE
    }
  }

  fun addupdates(upgradeModel: List<String>, list: ArrayList<FeaturesModel>) {
    val initPosition = upgradeList.size
    upgradeList.clear()
    upgradeList.addAll(upgradeModel)
    featuresList = list
    notifyItemRangeInserted(initPosition, upgradeList.size)
  }

  class upgradeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var view = itemView.findViewById<View>(R.id.dummy_view)
    var title = itemView.findViewById<TextView>(R.id.title)
    var image = itemView.findViewById<ImageView>(R.id.imageView2)

  }

  fun getFeaturesCount(holder: upgradeViewHolder, position: Int) {
    CompositeDisposable().add(
      AppDatabase.getInstance(activity.application)!!
        .featuresDao()
        .getFeatureTypeCountPremium(upgradeList.get(position), "MERCHANT_TRAINING", true)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({
//          checking if Limited content feature available
//          if available remove the count
          var state = false
          for(singlefeature in featuresList) {
            if (singlefeature.feature_code!!.equals("LIMITED_CONTENT")){
              state = true
            }
          }
          holder.title.setText(upgradeList.get(position) + " (" + (if(upgradeList.get(position).equals("Content Management") && state) it-1 else it) + ")")
        }, {
          it.printStackTrace()
        })
    )
  }
}