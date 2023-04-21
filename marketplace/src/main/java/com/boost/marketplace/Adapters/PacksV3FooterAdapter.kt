package com.boost.marketplace.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.boost.cart.utils.Utils
import com.boost.marketplace.R
import com.boost.marketplace.interfaces.PacksV3FooterListener
import com.boost.marketplace.ui.comparePacksV3.ComparePacksV3Activity
import com.framework.utils.RootUtil
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class PacksV3FooterAdapter(
    var list: ArrayList<String>,
    val activity: ComparePacksV3Activity,listen: PacksV3FooterListener
) : RecyclerView.Adapter<PacksV3FooterAdapter.ParentViewHolder>() {

    lateinit var context: Context
    var selectedPosition: Int = 0
    private lateinit var listener: PacksV3FooterListener

    init {
        listener = listen
    }

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        i: Int
    ): ParentViewHolder {
        val view = LayoutInflater
            .from(viewGroup.context)
            .inflate(
                R.layout.item_packsv3footer,
                viewGroup, false
            )
        context = view.context
        return ParentViewHolder(view)
    }

    override fun onBindViewHolder(
        parentViewHolder: ParentViewHolder,
        position: Int
    ) {
//        val sameAddonsInCart = ArrayList<String>()
//        val addonsListInCart = ArrayList<String>()
        val parentItem = list.get(position).split("##")
        val bundleId = parentItem[0]
        val bundleName = parentItem[1]
        val price = parentItem[2]

        if (selectedPosition == position) {
            parentViewHolder.itemView.setBackgroundResource(R.drawable.mp_home_share_click_effect)
        }else {
            parentViewHolder.itemView.setBackgroundResource(R.drawable.edit_txt_packsv3)
        }
        parentViewHolder.maincl.setOnClickListener {
            selectedPosition = position;
            listener.onSelectedPack(bundleId)
            notifyDataSetChanged();

        }

        parentViewHolder.PackageItemTitle.text = bundleName ?: ""
        val data = bundleName?.substring(7) ?: ""
        val items = data!!.split(" ".toRegex())
        if (items.size == 1) {
            parentViewHolder.PackageItemTitle.text = items[0]
        } else if (items.size == 2) {
            parentViewHolder.PackageItemTitle.text = items[0] + " \n" + items[1]
        } else if (items.size == 3) {
            parentViewHolder.PackageItemTitle.text = items[0] + " \n" + items[1] + " " + items[2]
        } else if (items.size == 4) {
            parentViewHolder.PackageItemTitle.text =
                items[0] + " " + items[1] + " \n" + items[2] + " " + items[3]
        } else if (items.size == 5) {
            parentViewHolder.PackageItemTitle.text =
                items[0] + " " + items[1] + " \n" + items[2] + " " + items[3] + " " + items[4]
        }

        val tempPrice = Utils.priceCalculatorForYear(price.toDouble(), "", activity)
        parentViewHolder.tv_price.setText(
            "₹" + NumberFormat.getNumberInstance(Locale.ENGLISH)
                .format(RootUtil.round(tempPrice,0))
        )
        parentViewHolder.footer_pack_mode.setText(Utils.yearlyOrMonthlyOrEmptyValidity("", activity))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun addFooterUpdates(footerValues: ArrayList<String>) {
        list = footerValues
        notifyDataSetChanged()
    }

    inner class ParentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val PackageItemTitle: TextView
        val tv_price: TextView
        val footer_pack_mode: TextView
        val maincl: View

        init {
            PackageItemTitle = itemView
                .findViewById(
                    R.id.footer_pack
                )
            tv_price = itemView
                .findViewById(
                    R.id.footer_pack_total
                )
            footer_pack_mode = itemView
                .findViewById(
                    R.id.footer_pack_mode
                )
            maincl =itemView.findViewById(R.id.maincl)
        }
    }

}