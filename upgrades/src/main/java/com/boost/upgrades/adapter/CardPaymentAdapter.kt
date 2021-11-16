package com.boost.upgrades.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.boost.upgrades.R
import com.framework.upgradeDB.model.*
import com.boost.upgrades.utils.Utils.hideSoftKeyboard


class CardPaymentAdapter(val activity: FragmentActivity, itemList: List<WidgetModel>?) :
  RecyclerView.Adapter<CardPaymentAdapter.upgradeViewHolder>(), View.OnClickListener {

  private var list = ArrayList<WidgetModel>()
  private lateinit var context: Context

  init {
    this.list = itemList as ArrayList<WidgetModel>
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): upgradeViewHolder {
    val itemView = LayoutInflater.from(parent?.context).inflate(
      R.layout.card_payment_item, parent, false
    )
    context = itemView.context


    itemView.setOnClickListener(this)
    return upgradeViewHolder(itemView)
  }

  override fun getItemCount(): Int {
    return list.size
  }

  override fun onBindViewHolder(holder: upgradeViewHolder, position: Int) {

    holder.view.visibility = View.GONE
    holder.cardCVV.setOnEditorActionListener { v, actionId, event ->
      if (actionId == EditorInfo.IME_ACTION_DONE) {
        holder.cardCVV.clearFocus()
        hideSoftKeyboard(activity)
      }
      return@setOnEditorActionListener false
    }
  }

  override fun onClick(v: View?) {

  }

  fun addupdates(upgradeModel: List<WidgetModel>) {
    val initPosition = list.size
    list.clear()
    list.addAll(upgradeModel)
    notifyItemRangeInserted(initPosition, list.size)
  }

  class upgradeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var view = itemView.findViewById<View>(R.id.card_payment_addons_view)!!
    var cardCVV = itemView.findViewById<EditText>(R.id.card_cvv)!!
//
//        private var context: Context = itemView.context
//
//
//        fun upgradeListItem(updateModel: UpdatesModel) {
//
//        }
  }
}