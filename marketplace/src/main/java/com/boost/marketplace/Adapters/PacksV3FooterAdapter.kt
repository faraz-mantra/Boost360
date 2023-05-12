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
    val activity: ComparePacksV3Activity,
    val listener: PacksV3FooterListener
) : RecyclerView.Adapter<PacksV3FooterAdapter.PackViewHolder>() {

    lateinit var context: Context
    var selectedPosition: Int = 0

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        i: Int
    ): PackViewHolder {
        val view = LayoutInflater
            .from(viewGroup.context)
            .inflate(
                R.layout.item_packsv3footer,
                viewGroup, false
            )
        context = view.context
        return PackViewHolder(view)
    }

    override fun onBindViewHolder(
        packViewHolder: PackViewHolder,
        position: Int
    ) {
        val parentItem = list.get(position).split("##")
        val bundleId = parentItem[0]
        val bundleName = parentItem[1]
        val price = parentItem[2]

        if (selectedPosition == position) {
            packViewHolder.itemView.setBackgroundResource(R.drawable.mp_home_share_click_effect)
        }else {
            packViewHolder.itemView.setBackgroundResource(R.drawable.edit_txt_packsv3)
        }
        packViewHolder.maincl.setOnClickListener {
            selectedPosition = position;
            listener.onSelectedPack(bundleId)
            notifyDataSetChanged();
        }

        packViewHolder.PackageItemTitle.text = bundleName ?: ""
        val data = bundleName?.substring(7) ?: ""
        val items = data!!.split(" ".toRegex())
        if (items.size == 1) {
            packViewHolder.PackageItemTitle.text = items[0]
        } else if (items.size == 2) {
            packViewHolder.PackageItemTitle.text = items[0] + " \n" + items[1]
        } else if (items.size == 3) {
            packViewHolder.PackageItemTitle.text = items[0] + " \n" + items[1] + " " + items[2]
        } else if (items.size == 4) {
            packViewHolder.PackageItemTitle.text =
                items[0] + " " + items[1] + " \n" + items[2] + " " + items[3]
        } else if (items.size == 5) {
            packViewHolder.PackageItemTitle.text =
                items[0] + " " + items[1] + " \n" + items[2] + " " + items[3] + " " + items[4]
        }

        val tempPrice = Utils.priceCalculatorForYear(price.toDouble(), "", activity)
        packViewHolder.tv_price.setText(
            "₹" + NumberFormat.getNumberInstance(Locale.ENGLISH)
                .format(RootUtil.round(tempPrice,0))
        )
        packViewHolder.footer_pack_mode.setText(Utils.yearlyOrMonthlyOrEmptyValidity("", activity))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun addFooterUpdates(footerValues: ArrayList<String>) {
        list = footerValues
        notifyDataSetChanged()
    }

    class PackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val PackageItemTitle = itemView.findViewById<TextView>(R.id.footer_pack)
        val tv_price = itemView.findViewById<TextView>(R.id.footer_pack_total)
        val footer_pack_mode = itemView.findViewById<TextView>(R.id.footer_pack_mode)
        val maincl = itemView.findViewById<View>(R.id.maincl)
    }

}