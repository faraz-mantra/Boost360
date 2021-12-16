package com.boost.upgrades.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.boost.dbcenterapi.upgradeDB.local.AppDatabase
import com.boost.upgrades.R
import com.boost.upgrades.UpgradeActivity
import com.boost.upgrades.interfaces.HomeListener
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers


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

    }
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
    var image = itemView.findViewById<ImageView>(R.id.imageView2)

  }

  fun getFeaturesCount(holder: upgradeViewHolder, position: Int) {
    CompositeDisposable().add(
      AppDatabase.getInstance(activity.application)!!
        .featuresDao()
        .getFeatureTypeCount(upgradeList.get(position), "MERCHANT_TRAINING")
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