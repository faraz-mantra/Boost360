package com.boost.marketplace.Adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.boost.dbcenterapi.upgradeDB.model.FeaturesModel
import com.boost.marketplace.R
import com.boost.marketplace.interfaces.MyAddonsListener
import com.boost.marketplace.ui.My_Plan.MyCurrentPlanActivity
import com.bumptech.glide.Glide
import java.lang.Long
import java.text.SimpleDateFormat
import java.util.*
import kotlin.Int


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
    val itemView = LayoutInflater.from(parent.context).inflate(
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
    holder.singleTitle.text = cryptocurrencyItem.name

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
        val item = FeaturesModel(
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
          item.createdon=  list.get(position).createdon

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
    var btn1 = itemView.findViewById<TextView>(R.id.btn1)!!
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
      val dataString = updateModel.expiryDate
      val date = Date(Long.parseLong(dataString!!.substring(6, dataString.length - 7)))
      val dateFormat = SimpleDateFormat("dd MMM yyyy")
      validity2.text= "Valid till " +(dateFormat.format(date))

      Glide.with(context).load(updateModel.primary_image).into(image)
//      if (updateModel.featureState == 1) {
//       img1.setImageResource(R.drawable.ic_action_req)
//      }
//      else {
//        img1.setImageResource(R.drawable.ic_action_req)
//      }
      val actionRequired = updateModel.actionNeeded
      val featureState = updateModel.featureState
      if (featureState != null) {
        featureEdgeCase(actionRequired!!.toInt(),featureState)
      }
    }
    fun featureEdgeCase(actionRequired: Int, featureState: Int) {

      if (actionRequired == 1 && (featureState == 1 || featureState == 2 || featureState == 3 || featureState == 4 || featureState == 5 || featureState == 6)
      ) {
        img1.setImageResource(R.drawable.vector)
        btn1.text ="Contact Support"

      } else if (actionRequired == 2 && (featureState == 1 || featureState == 2 || featureState == 3 || featureState == 4 || featureState == 5 || featureState == 6)
      ) {
        img1.setImageResource(R.drawable.vector)
        btn1.text = "Choose Domain"

      } else if (actionRequired == 3 && (featureState == 1 || featureState == 2 || featureState == 3 || featureState == 4 || featureState == 5 || featureState == 6)
      ) {
        btn1.text = "Choose VMN"
        img1.setImageResource(R.drawable.vector)

      } else if (actionRequired == 4 && (featureState == 1 || featureState == 2 || featureState == 3 || featureState == 4 || featureState == 5 || featureState == 6)
      ) {
        btn1.text = "Choose Email"
        img1.setImageResource(R.drawable.vector)

      } else if (actionRequired == 5 && (featureState == 1 || featureState == 2 || featureState == 3 || featureState == 4 || featureState == 5 || featureState == 6)) {
        btn1.text = "Choose virtual number/\n" + "Setup IVR"
        img1.setImageResource(R.drawable.vector)

      } else if (actionRequired == 6 && (featureState == 1 || featureState == 2 || featureState == 3 || featureState == 4 || featureState == 5 || featureState == 6)) {
        btn1.text = "Add staff"
        img1.setImageResource(R.drawable.vector)
      }

      else if (actionRequired == 8 && (featureState == 1 || featureState == 2 || featureState == 3 || featureState == 4 || featureState == 5 || featureState == 6)) {
        btn1.text = "Contact support"
        img1.setImageResource(R.drawable.vector)
      }

      else if (actionRequired == 9 && (featureState == 1 || featureState == 2 || featureState == 3 || featureState == 4 || featureState == 5 || featureState == 6)) {
        btn1.text = "Contact support"
        img1.setImageResource(R.drawable.vector)
      }

      else if (actionRequired == 10 && (featureState == 1 || featureState == 2 || featureState == 3 || featureState == 4 || featureState == 5 || featureState == 6)) {
        btn1.text = "Activate trip advisor rating"
        img1.setImageResource(R.drawable.vector)
      }

      else if (actionRequired == 11 && (featureState == 1 || featureState == 2 || featureState == 3 || featureState == 4 || featureState == 5 || featureState == 6)) {
        btn1.text = "Create offers"
        img1.setImageResource(R.drawable.vector)
      }

      else if (actionRequired == 12 && (featureState == 1 || featureState == 2 || featureState == 3 || featureState == 4 || featureState == 5 || featureState == 6)) {
        btn1.text = "Add places to look around"
        img1.setImageResource(R.drawable.vector)
      }

      else if (actionRequired == 13 && (featureState == 1 || featureState == 2 || featureState == 3 || featureState == 4 || featureState == 5 || featureState == 6)) {
        btn1.text = "Add testimonials"
                img1.setImageResource(R.drawable.vector)
      }

      else if (actionRequired == 14 && (featureState == 1 || featureState == 2 || featureState == 3 || featureState == 4 || featureState == 5 || featureState == 6)) {
        btn1.text = "Add upcoming batches"
        img1.setImageResource(R.drawable.vector)
      }

      else if (actionRequired == 15 && (featureState == 1 || featureState == 2 || featureState == 3 || featureState == 4
                || featureState == 5 || featureState == 6)) {
        btn1.text = "Add project portfolio"
        img1.setImageResource(R.drawable.vector)
      }

      else if (actionRequired == 16 && (featureState == 1 || featureState == 2 || featureState == 3 || featureState == 4
                || featureState == 5 || featureState == 6)) {
        btn1.text = "Add brochure"
        img1.setImageResource(R.drawable.vector)
      }

      else if (actionRequired == 17 && (featureState == 1 || featureState == 2 || featureState == 3 || featureState == 4
                || featureState == 5 || featureState == 6)) {
        btn1.text = "Add project & teams"
        img1.setImageResource(R.drawable.vector)
      }

      else if (actionRequired == 21 && (featureState == 1 || featureState == 2 || featureState == 3 || featureState == 4
                || featureState == 5 || featureState == 6)) {
      btn1.text = "Contact support"
      img1.setImageResource(R.drawable.vector)
      }

      else if (actionRequired == 22 && (featureState == 1 || featureState == 2 || featureState == 3 || featureState == 4
                || featureState == 5 || featureState == 6)) {
        btn1.text = "Activate boost keyboard"
      img1.setImageResource(R.drawable.vector)
      }

    else if (actionRequired == 0 && (featureState == 3 || featureState == 4 || featureState == 5 || featureState == 6)) {
      img1.setImageResource(R.drawable.ic_sync_blue)
        btn1.text = "Processing..."
        btn1.setTextColor(ContextCompat.getColor(context,R.color.light_blue2))
      }

    }
  }
}