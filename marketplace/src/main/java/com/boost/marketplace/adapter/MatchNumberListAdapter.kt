package com.boost.marketplace.adapter

import android.content.Context
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.boost.marketplace.R
import com.boost.marketplace.interfaces.CallTrackListener
import com.boost.marketplace.ui.details.call_track.CallTrackingActivity
import java.util.*


class MatchNumberListAdapter(
    val activity: CallTrackingActivity,
    cryptoCurrencies: MutableList<String>?,
    searchValue: String?,
    val listener: CallTrackListener
) : RecyclerView.Adapter<MatchNumberListAdapter.upgradeViewHolder>() {

    private var upgradeList: MutableList<String> = ArrayList()
    private lateinit var context: Context
    private var searchItem: String? = null
    var selectedPosition: Int = -1


    init {
        this.upgradeList = cryptoCurrencies!!
        this.searchItem = searchValue
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): upgradeViewHolder {
        val itemView = LayoutInflater.from(parent?.context).inflate(
            R.layout.layout_number_list, parent, false
        )
        context = itemView.context
        return upgradeViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return upgradeList.size
    }

    override fun onBindViewHolder(holder: upgradeViewHolder, position: Int) {
        if (searchItem != null && searchItem!!.isNotEmpty()) {
            val spannable = SpannableString(upgradeList[position])

            for (i in searchItem!!.indices) {
                for (j in 4 until upgradeList[position].length){
                    if (upgradeList[position][j] == searchItem!![i]) {
                            spannable.setSpan(
                                ForegroundColorSpan(ContextCompat.getColor(context, R.color.black)),
                                j,
                                j+1,
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                            )
                            spannable.setSpan(
                                StyleSpan(Typeface.BOLD),
                                j,
                                j+1,
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                            )
                        break
                    }

                }
            }
            holder.title.text = spannable
        } else {
            holder.title.text = upgradeList[position]
        }
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

//    fun updateNumberList(numberList: MutableList<String>?) {
//        val diffCallback = VMNDiffUtilClass(this.upgradeList, numberList!!)
//        val diffResult = DiffUtil.calculateDiff(diffCallback)
//        this.upgradeList.clear()
//        this.upgradeList.addAll(numberList)
//        diffResult.dispatchUpdatesTo(this)
//    }

    class upgradeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var title = itemView.findViewById<TextView>(R.id.tv_title)
        var radio1 = itemView.findViewById<ImageView>(R.id.iv_radio1)
        var radio = itemView.findViewById<ImageView>(R.id.iv_radio)
        var divider = itemView.findViewById<View>(R.id.divider)

    }


}