package com.boost.marketplace.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.boost.dbcenterapi.upgradeDB.model.FeaturesModel
import com.boost.marketplace.R
import com.bumptech.glide.Glide
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList


class CompareItemAdapter(cryptoCurrencies: List<FeaturesModel>?) :
    RecyclerView.Adapter<CompareItemAdapter.upgradeViewHolder>() {

    private var upgradeList = ArrayList<FeaturesModel>()
    var minMonth = 1
    private lateinit var context: Context

    init {
        this.upgradeList = cryptoCurrencies as ArrayList<FeaturesModel>
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): upgradeViewHolder {
        val itemView = LayoutInflater.from(parent?.context).inflate(
            R.layout.item_pack_list, parent, false
        )
        context = itemView.context

        return upgradeViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return upgradeList.size
    }

    override fun onBindViewHolder(holder: upgradeViewHolder, position: Int) {

        holder.name.setText(upgradeList.get(position).name)
        // holder.title.setText(upgradeList.get(position).target_business_usecase)

        val cryptocurrencyItem = upgradeList[position]
        holder.upgradeListItem(holder, cryptocurrencyItem)

        Glide.with(context).load(upgradeList.get(position).primary_image).into(holder.image)
//    holder.view.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
//    if (position == upgradeList.size - 1) {
//      holder.view.visibility = View.INVISIBLE
//    }

//    holder.arrow_icon.setOnClickListener {
//      val details = DetailsFragment.newInstance()
//      val args = Bundle()
//      args.putString("itemId", upgradeList.get(position).feature_code)
//      args.putBoolean("packageView", true)
//      details.arguments = args
//
//      activity.addFragment(details, Constants.DETAILS_FRAGMENT)
//    }
        holder.itemView.setOnClickListener {
//      val details = DetailsFragment.newInstance()
//      val args = Bundle()
//      args.putString("itemId", upgradeList.get(position).feature_code)
//      args.putBoolean("packageView", true)
//      details.arguments = args

            //  activity.addFragment(details, Constants.DETAILS_FRAGMENT)
        }
    }

    fun addupdates(upgradeModel: List<FeaturesModel>, noOfMonth: Int) {
        minMonth = noOfMonth
        val initPosition = upgradeList.size
        upgradeList.clear()
        upgradeList.addAll(upgradeModel)
        notifyItemRangeInserted(initPosition, upgradeList.size)
    }

    class upgradeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val image = itemView.findViewById<ImageView>(R.id.imageView2)!!
        val name = itemView.findViewById<TextView>(R.id.title)!!
        val price = itemView.findViewById<TextView>(R.id.price)!!

        fun upgradeListItem(holder: upgradeViewHolder, updateModel: FeaturesModel) {
            val discount = 100 - updateModel.discount_percent
            val price = (discount * updateModel.price) / 100
            holder.price.text =
                "₹" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(price) + "/month"

        }


    }

}