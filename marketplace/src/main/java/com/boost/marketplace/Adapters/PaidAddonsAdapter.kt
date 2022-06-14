package com.boost.marketplace.Adapters

import android.content.Context
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.boost.dbcenterapi.upgradeDB.model.FeaturesModel
import com.boost.marketplace.R
import com.boost.marketplace.interfaces.MyAddonsListener
import com.boost.marketplace.ui.My_Plan.MyCurrentPlanActivity
import com.bumptech.glide.Glide
import com.framework.utils.DateUtils


class PaidAddonsAdapter( val activity: MyCurrentPlanActivity,
                         itemList: List<FeaturesModel>?, var myAddonsListener: MyAddonsListener
) : RecyclerView.Adapter<PaidAddonsAdapter.upgradeViewHolder>()
 {

  private var list = ArrayList<FeaturesModel>()
  private lateinit var context: Context

  init {
    this.list = itemList as ArrayList<FeaturesModel>
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): upgradeViewHolder {
    val itemView = LayoutInflater.from(parent?.context).inflate(
      R.layout.item_myplan_paid_features, parent, false
    )
    context = itemView.context
    return upgradeViewHolder(itemView)
  }

  override fun getItemCount(): Int {
    return list.size
  }

  override fun onBindViewHolder(holder: upgradeViewHolder, position: Int) {
    val cryptocurrencyItem = list[position]
    holder.upgradeListItem(cryptocurrencyItem)
    holder.view.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
    if (list.size - 1 == position) {
      holder.view.visibility = View.GONE
    }
    holder.mainlayout.setOnClickListener {
      if (holder.detailsView.visibility==View.GONE) {
        TransitionManager.beginDelayedTransition(holder.detailsView, AutoTransition())
        holder.detailsView.visibility= View.VISIBLE
      } else {
        TransitionManager.beginDelayedTransition(holder.detailsView, AutoTransition())
        holder.detailsView.visibility= View.GONE
      }
    }
    holder.detailsView.setOnClickListener {
      val item: FeaturesModel = FeaturesModel(
        list.get(position).feature_id,
        list.get(position).boost_widget_key,
        list.get(position).name,
        list.get(position).feature_code,
        list.get(position).description!!,
        list.get(position).description_title,
        list.get(position).createdon,
        list.get(position).updatedon,
        list.get(position).websiteid,
        list.get(position).isarchived,
        list.get(position).is_premium,
        list.get(position).target_business_usecase,
        list.get(position).feature_importance,
        list.get(position).discount_percent,
        list.get(position).price,
        list.get(position).time_to_activation,
        list.get(position).primary_image)
        item.expiryDate=list.get(position).expiryDate
        item.activatedDate= list.get(position).activatedDate
        item.status=  list.get(position).status
      myAddonsListener.onPaidAddonsClicked(item)
    }
  }

  fun addupdates(upgradeModel: List<FeaturesModel>) {
    val initPosition = list.size
    list.clear()
    list.addAll(upgradeModel)
    notifyItemRangeInserted(initPosition, list.size)
  }

  class upgradeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var view = itemView.findViewById<View>(R.id.paid_single_dummy_view)!!
    private var upgradeTitle = itemView.findViewById<TextView>(R.id.paid_addons_name)!!
    private var activateLayout = itemView.findViewById<LinearLayout>(R.id.paid_addons_activate)!!
    var validity2=itemView.findViewById<TextView>(R.id.validity2)
    private var image = itemView.findViewById<ImageView>(R.id.single_paidaddon_image)!!
    var mainlayout=itemView.findViewById<ConstraintLayout>(R.id.main_layout)
    var detailsView=itemView.findViewById<ConstraintLayout>(R.id.detailsView)
    var img1=itemView.findViewById<ImageView>(R.id.img1)!!
    private var context: Context = itemView.context

    fun upgradeListItem(updateModel: FeaturesModel) {
      upgradeTitle.text = updateModel.name
      val date: String? =DateUtils.parseDate(updateModel.expiryDate, DateUtils.FORMAT_SERVER_DATE1, DateUtils.FORMAT1_DD_MM_YYYY)
      validity2.text= "Valid till " + date
      Glide.with(context).load(updateModel.primary_image).into(image)
      if (updateModel.status == 1) {
        img1.setImageResource(R.drawable.ic_active)
      }
      else {
        img1.setImageResource(R.drawable.ic_action_req)
      }
    }
  }
}