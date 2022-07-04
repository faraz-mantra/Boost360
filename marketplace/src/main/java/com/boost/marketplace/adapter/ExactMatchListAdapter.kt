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
import androidx.recyclerview.widget.RecyclerView
import com.boost.marketplace.R
import com.boost.marketplace.interfaces.CallTrackListener
import com.boost.marketplace.ui.details.call_track.CallTrackingActivity
import java.util.*

class ExactMatchListAdapter(
    val activity: CallTrackingActivity,
    cryptoCurrencies: ArrayList<String>?,
    searchValue: String?,
    val listener: CallTrackListener
) : RecyclerView.Adapter<ExactMatchListAdapter.UpgradeViewHolder>() {

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
        if (searchItem != null && searchItem!!.isNotEmpty()) {
            val num = upgradeList[position].replace("+91 ", ""). replace("-", "")
            val diff = upgradeList[position].length - num.length -1
            System.out.println("Difference---->"+diff)


            if (searchItem != null && searchItem!!.isNotEmpty()) {

                val startPos =num.toLowerCase(Locale.US)
                    .indexOf(searchItem!!.toLowerCase(Locale.US))
                val endPos = startPos + searchItem!!.length
                val startPosForDisplayString = startPos + diff
                val endPosForDisplayString  = endPos + diff
                System.out.println("start Pos without extra string---->"+startPos)
                System.out.println("end Pos without extra string---->"+endPos)
                System.out.println("start position with extra string---->"+startPosForDisplayString)
                System.out.println("end Pos with extra string---->"+endPosForDisplayString)



                if (startPosForDisplayString != -1) {
                    val spannable = SpannableString(upgradeList[position])
                    spannable.setSpan(
                        ForegroundColorSpan(ContextCompat.getColor(context, R.color.black)),
                        startPosForDisplayString,
                        endPosForDisplayString+1,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    spannable.setSpan(
                        StyleSpan(Typeface.BOLD),
                        startPosForDisplayString,
                        endPosForDisplayString+1,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )

                    holder.title.text = spannable
                }
            } else {
                holder.title.text = upgradeList[position]

            }
        }

            if (selectedPosition == position) {
                holder.radio1.visibility = View.VISIBLE
                holder.itemView.setBackgroundResource(R.color.colorAccent2);
            } else {
                holder.radio1.visibility = View.GONE
                holder.itemView.setBackgroundResource(R.color.white);
            }
            holder.itemView.setOnClickListener {
                selectedPosition = position
                notifyDataSetChanged()
                listener.onClicked(upgradeList[position])
            }
        }

        class UpgradeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var title = itemView.findViewById<TextView>(R.id.tv_title)
            var radio1 = itemView.findViewById<ImageView>(R.id.iv_radio1)
            var radio = itemView.findViewById<ImageView>(R.id.iv_radio)
            var divider = itemView.findViewById<View>(R.id.divider)

        }


    }