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


class PaidAddonsAdapter( val activity: MyCurrentPlanActivity,
 itemList: List<FeaturesModel>?, var myAddonsListener: MyAddonsListener
) : RecyclerView.Adapter<PaidAddonsAdapter.upgradeViewHolder>(), View.OnClickListener {

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

  }

  fun addupdates(upgradeModel: List<FeaturesModel>) {
    val initPosition = list.size
    list.clear()
    list.addAll(upgradeModel)
    notifyItemRangeInserted(initPosition, list.size)
  }


  override fun onClick(v: View?) {
    myAddonsListener.onPaidAddonsClicked(v)
  }

  class upgradeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var view = itemView.findViewById<View>(R.id.paid_single_dummy_view)!!
    private var upgradeTitle = itemView.findViewById<TextView>(R.id.paid_addons_name)!!
    private var activateLayout = itemView.findViewById<LinearLayout>(R.id.paid_addons_activate)!!
    private var image = itemView.findViewById<ImageView>(R.id.single_paidaddon_image)!!
     var mainlayout=itemView.findViewById<ConstraintLayout>(R.id.main_layout)
     var detailsView=itemView.findViewById<ConstraintLayout>(R.id.detailsView)

    private var context: Context = itemView.context


    fun upgradeListItem(updateModel: FeaturesModel) {
      upgradeTitle.text = updateModel.name
      Glide.with(context).load(updateModel.primary_image).into(image)

    }
  }

}