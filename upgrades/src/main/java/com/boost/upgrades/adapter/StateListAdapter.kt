package com.boost.upgrades.adapter

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.boost.upgrades.R
import com.boost.upgrades.UpgradeActivity
import com.boost.upgrades.data.api_model.customerId.StateModel
import com.boost.upgrades.data.api_model.stateCode.Data
import com.boost.upgrades.data.model.FeaturesModel
import com.boost.upgrades.interfaces.MyAddonsListener
import com.boost.upgrades.interfaces.StateListener
import com.framework.enums.setTextStyle


class StateListAdapter(
  val activity: UpgradeActivity,
  itemList: List<StateModel>?, itemList1: List<Data>?, var mListener: StateListener
) : RecyclerView.Adapter<StateListAdapter.upgradeViewHolder>() {

  private var list = ArrayList<StateModel>()
  private var list1 = ArrayList<Data>()
  private var selectedState: String? = null
  private var selectedStateTin: String? = null
  private var selectedItem: Int? = 0
  private lateinit var context: Context

  init {
    this.list = itemList as ArrayList<StateModel>
    this.list1 = itemList1 as ArrayList<Data>
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): upgradeViewHolder {
    val itemView = LayoutInflater.from(parent?.context).inflate(
      R.layout.state_list_tem, parent, false
    )
    context = itemView.context

    return upgradeViewHolder(itemView)
  }

  override fun getItemCount(): Int {
//    return list.size //5
    return list1.size
  }

  override fun onBindViewHolder(holder: upgradeViewHolder, position: Int) {
//    val items = list[position]
    val items = list1[position]
    holder.upgradeListItem(items)

    if (items.state == selectedState) {
      holder.state_name.setTypeface(null, Typeface.BOLD)
      holder.stateTin.setTypeface(null,Typeface.BOLD)
      holder.selected_state.setImageDrawable(context.resources.getDrawable(R.drawable.ic_checked))
    }

    holder.itemView.setOnClickListener {
      holder.state_name.setTypeface(null, Typeface.BOLD)
      holder.stateTin.setTypeface(null,Typeface.BOLD)
      holder.selected_state.setImageDrawable(context.resources.getDrawable(R.drawable.ic_checked))
      mListener.stateSelected1(items)
    }
  }


//  fun addupdates(upgradeModel: List<StateModel>, state: String?) {
//    selectedState = state
//    val initPosition = list.size
//    list.clear()
//    list.addAll(upgradeModel)
//    notifyItemRangeInserted(initPosition, list.size)
//  }

  fun addupdates1(upgradeModel1: ArrayList<Data>, state: String?,stateTin:String?) {
    selectedState = state
    selectedStateTin = stateTin
    val initPosition = list1.size
    list1.clear()
    list1.addAll(upgradeModel1)
    notifyItemRangeInserted(initPosition, list1.size)
  }

  class upgradeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var state_name = itemView.findViewById<TextView>(R.id.state_name)!!
    var stateTin = itemView.findViewById<TextView>(R.id.state_tin)!!
    var selected_state = itemView.findViewById<ImageView>(R.id.selected_state)!!

    private var context: Context = itemView.context


    //    fun upgradeListItem(updateModel: StateModel) {
//      state_name.text = updateModel.state
//    }
    fun upgradeListItem(updateModel: Data) {
      state_name.text = updateModel.state
      stateTin.text = "("+updateModel.stateTin +")"
    }
  }
}