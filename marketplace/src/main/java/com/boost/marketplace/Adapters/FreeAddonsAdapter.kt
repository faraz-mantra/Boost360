package com.boost.marketplace.Adapters

import android.content.Context
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


class FreeAddonsAdapter( val activity: MyCurrentPlanActivity,
                         itemList: List<FeaturesModel>?, var myAddonsListener: MyAddonsListener
) : RecyclerView.Adapter<FreeAddonsAdapter.upgradeViewHolder>()
{

  private var list = ArrayList<FeaturesModel>()
  private lateinit var context: Context

  init {
    this.list = itemList as ArrayList<FeaturesModel>
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): upgradeViewHolder {
    val itemView = LayoutInflater.from(parent?.context).inflate(
      R.layout.item_myplan_features, parent, false
    )
    context = itemView.context
    return upgradeViewHolder(itemView)
  }

  override fun getItemCount(): Int {
    return   list.size
  }

  override fun onBindViewHolder(holder: upgradeViewHolder, position: Int) {
    val cryptocurrencyItem = list[position]
    holder.upgradeListItem(cryptocurrencyItem)
    holder.singleTitle.text = cryptocurrencyItem.expiryDate

    //Check here

//    if (cryptocurrencyItem.featureState !=null && cryptocurrencyItem.featureState != 1){
//      holder.itemView.visibility=View.VISIBLE
//      holder.itemView.layoutParams =
//        RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
//    }
//    else{
//      holder.itemView.visibility=View.GONE
//      holder.itemView.layoutParams = RecyclerView.LayoutParams(0, 0)
//    }

//    if (cryptocurrencyItem.is_premium.equals(true)){
//      holder.paid_addons_activate.visibility=View.VISIBLE
//    }
//    else{
//      holder.paid_addons_activate.visibility=View.GONE
//    }

    if(position == list.size-1){
      holder.paid_single_dummy_view.visibility = View.GONE
    }

//    holder.mainLayout.setOnClickListener {
//
//      if (holder.detailsView.visibility == View.GONE) {
//        TransitionManager.beginDelayedTransition(holder.detailsView, AutoTransition())
//        holder.detailsView.visibility = View.VISIBLE
//      } else {
//        TransitionManager.beginDelayedTransition(holder.detailsView, AutoTransition())
//        holder.detailsView.visibility = View.GONE
//      }
//    }

      holder.itemView.setOnClickListener {
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
          item.featureState=  list.get(position).featureState

        myAddonsListener.onFreeAddonsClicked(item)
      }

    }


  fun addupdates(upgradeModel: List<FeaturesModel>) {
    val initPosition = list.size
    list.clear()
    list.addAll(upgradeModel)
    notifyItemRangeInserted(initPosition, list.size)
  }

  class upgradeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var singleTitle = itemView.findViewById<TextView>(R.id.free_addons_name)!!
    var validity2 = itemView.findViewById<TextView>(R.id.validity2)!!
    var image = itemView.findViewById<ImageView>(R.id.single_freeaddon_image)!!
    var mainLayout=itemView.findViewById<ConstraintLayout>(R.id.main_layout)
    var cardViews=itemView.findViewById<LinearLayout>(R.id.cardViews)
    var detailsView=itemView.findViewById<ConstraintLayout>(R.id.detailsView)
    var paid_addons_activate=itemView.findViewById<LinearLayout>(R.id.paid_addons_activate)
    var img1=itemView.findViewById<ImageView>(R.id.img1)!!
    var paid_single_dummy_view=itemView.findViewById<View>(R.id.paid_single_dummy_view)!!
    private var context: Context = itemView.context

    fun upgradeListItem(updateModel: FeaturesModel) {
      singleTitle.text = updateModel.name
      val date: String? =
        DateUtils.parseDate(updateModel.expiryDate, DateUtils.FORMAT_SERVER_DATE1, DateUtils.FORMAT1_DD_MM_YYYY)
      validity2.text= "Valid till " + date
      Glide.with(context).load(updateModel.primary_image).into(image)
      if (updateModel.featureState == 1) {
       img1.setImageResource(R.drawable.ic_action_req)
      }
      else {
        img1.setImageResource(R.drawable.ic_action_req)
      }
    }
  }
}