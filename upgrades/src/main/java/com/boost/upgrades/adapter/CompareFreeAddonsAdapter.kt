package com.boost.upgrades.adapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.boost.upgrades.R
import com.boost.upgrades.UpgradeActivity
import com.framework.upgradeDB.model.*
import com.boost.upgrades.interfaces.MyAddonsListener
import com.boost.upgrades.ui.details.DetailsFragment
import com.boost.upgrades.utils.Constants
import com.bumptech.glide.Glide


class CompareFreeAddonsAdapter(
  val activity: UpgradeActivity,
  itemList: List<FeaturesModel>?, var myAddonsListener: MyAddonsListener
) : RecyclerView.Adapter<CompareFreeAddonsAdapter.upgradeViewHolder>(), View.OnClickListener {

  private var list = ArrayList<FeaturesModel>()
  private lateinit var context: Context

  init {
    this.list = itemList as ArrayList<FeaturesModel>
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): upgradeViewHolder {
    val itemView = LayoutInflater.from(parent?.context).inflate(
      R.layout.compare_free_addons_list_item, parent, false
    )
    context = itemView.context

    itemView.setOnClickListener(this)

    return upgradeViewHolder(itemView)
  }

  override fun getItemCount(): Int {
    return list.size //5
  }

  override fun onBindViewHolder(holder: upgradeViewHolder, position: Int) {
    val cryptocurrencyItem = list[position]
    holder.upgradeListItem(cryptocurrencyItem)

//        holder.itemView.setOnClickListener{
//            val details = DetailsFragment.newInstance()
//            val args = Bundle()
//            args.putInt("pos", position)
//            details.arguments = args
//            activity.addFragment(details , Constants.DETAILS_FRAGMENT)
////            val intent = Intent(this.context, Details::class.java)
////            intent.putExtra("position",position)
////            startActivity(this.context, intent, null)
//        }

    holder.itemView.setOnClickListener {
      val details = DetailsFragment.newInstance()
      val args = Bundle()
      args.putString("itemId", list.get(position).feature_code)
      details.arguments = args
      activity.addFragment(details, Constants.DETAILS_FRAGMENT)
    }
    holder.view.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
    if (list.size - 1 == position) {
      holder.view.visibility = View.GONE
    }
  }

  fun addupdates(upgradeModel: List<FeaturesModel>?) {
    val initPosition = list.size
    list.clear()
    if (upgradeModel != null) {
      list.addAll(upgradeModel)
    }
    notifyItemRangeInserted(initPosition, list.size)
  }


  override fun onClick(v: View?) {
    myAddonsListener.onPaidAddonsClicked(v)
  }

  class upgradeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var view = itemView.findViewById<View>(R.id.paid_single_dummy_view)!!
    private var upgradeTitle = itemView.findViewById<TextView>(R.id.paid_addons_name)!!
    private var image = itemView.findViewById<ImageView>(R.id.single_paidaddon_image)!!
    private var title = itemView.findViewById<TextView>(R.id.title)!!

    private var context: Context = itemView.context


    fun upgradeListItem(updateModel: FeaturesModel) {
      upgradeTitle.text = updateModel.name
      title.text = updateModel.target_business_usecase
      Glide.with(context).load(updateModel.primary_image).into(image)

    }
  }

}