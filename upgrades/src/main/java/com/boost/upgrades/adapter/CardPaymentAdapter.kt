package com.boost.upgrades.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.boost.upgrades.R
import com.boost.upgrades.UpgradeActivity
import com.boost.upgrades.data.model.UpdatesModel
import com.boost.upgrades.interfaces.MyAddonsListener
import com.boost.upgrades.utils.Utils.hideSoftKeyboard
import com.bumptech.glide.Glide


class CardPaymentAdapter(val activity: FragmentActivity, itemList: List<UpdatesModel>?) :
    RecyclerView.Adapter<CardPaymentAdapter.upgradeViewHolder>(), View.OnClickListener {

    private var list = ArrayList<UpdatesModel>()
    private lateinit var context: Context

    init {
        this.list = itemList as ArrayList<UpdatesModel>
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
        return 1 //list.size
    }

    override fun onBindViewHolder(holder: upgradeViewHolder, position: Int) {

        holder.view.visibility = View.GONE
        holder.cardCVV.setOnEditorActionListener { v, actionId, event ->
            if(actionId == EditorInfo.IME_ACTION_DONE){
                holder.cardCVV.clearFocus()
                hideSoftKeyboard(activity)
            }
            return@setOnEditorActionListener false
        }
    }

    override fun onClick(v: View?) {

    }

    fun addupdates(upgradeModel: List<UpdatesModel>) {
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