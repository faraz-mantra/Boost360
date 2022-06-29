package com.boost.marketplace.adapter

import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.boost.marketplace.R
import com.boost.marketplace.interfaces.CallTrackListener
import com.boost.marketplace.ui.details.call_track.CallTrackingActivity
import com.boost.marketplace.ui.popup.call_track.VMNDiffUtilClass
import java.util.*


class NumberListAdapter(
    val activity: CallTrackingActivity,
    cryptoCurrencies: ArrayList<String>?,
    searchValue: String?,
    val listener: CallTrackListener
) : RecyclerView.Adapter<NumberListAdapter.UpgradeViewHolder>() {

    private var upgradeList = ArrayList<String>()
    private lateinit var context: Context
    private var searchItem: String? = null
    var selectedPosition: Int = -1


    init {
        this.upgradeList = cryptoCurrencies as ArrayList<String>
        this.searchItem = searchValue
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UpgradeViewHolder {
        val itemView = LayoutInflater.from(parent?.context).inflate(
            R.layout.layout_number_list, parent, false
        )
        context = itemView.context
        return UpgradeViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return upgradeList.size
    }

    override fun onBindViewHolder(holder: UpgradeViewHolder, position: Int) {
        if (position == upgradeList.size - 1) {
            holder.divider.visibility = GONE
        }
//        if (searchItem != null && searchItem!!.isNotEmpty()) {
//
//
//            val startPos = upgradeList[position].toLowerCase(Locale.US)
//                .indexOf(searchItem!!.toLowerCase(Locale.US))
//            val endPos = startPos + searchItem!!.length
//
//            if (startPos != -1) {
//                val spannable = SpannableString(upgradeList[position])
//                spannable.setSpan(
//                    ForegroundColorSpan(ContextCompat.getColor(context, R.color.black)),
//                    startPos,
//                    endPos,
//                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
//                )
//                spannable.setSpan(
//                    StyleSpan(Typeface.BOLD),
//                    startPos,
//                    endPos,
//                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
//                )
//
//                holder.title.text = spannable
//            }
//        } else {
            holder.title.text = upgradeList[position]

//        }
        if (selectedPosition == position) {
            holder.radio1.visibility =View.VISIBLE
            holder.itemView.setBackgroundResource(R.color.colorAccent2);
        }else {
            holder.radio1.visibility =View.GONE
            holder.itemView.setBackgroundResource(R.color.white);
        }
        holder.itemView.setOnClickListener {
            selectedPosition = position;
            notifyDataSetChanged();
            listener.onClicked(upgradeList[position])
        }
    }
    fun updateNumberList(numberList: ArrayList<String>) {
        val diffCallback = VMNDiffUtilClass(this.upgradeList, numberList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.upgradeList.clear()
        this.upgradeList.addAll(numberList)
        diffResult.dispatchUpdatesTo(this)
    }

    class UpgradeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var title = itemView.findViewById<TextView>(R.id.tv_title)
        var radio1 = itemView.findViewById<ImageView>(R.id.iv_radio1)
        var radio = itemView.findViewById<ImageView>(R.id.iv_radio)
        var divider = itemView.findViewById<View>(R.id.divider)

    }


}