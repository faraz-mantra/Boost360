package com.boost.upgrades.adapter

import android.content.Context
import android.os.Bundle
import android.text.SpannableString
import android.text.style.StrikethroughSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.boost.upgrades.R
import com.boost.upgrades.UpgradeActivity
import com.boost.upgrades.data.model.FeaturesModel
import com.boost.upgrades.ui.details.DetailsFragment
import com.boost.upgrades.utils.Constants
import com.bumptech.glide.Glide
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList


class UpgradeAdapter(
    val activity: UpgradeActivity,
    cryptoCurrencies: List<FeaturesModel>?
) : RecyclerView.Adapter<UpgradeAdapter.upgradeViewHolder>() {

    private var upgradeList = ArrayList<FeaturesModel>()
    private lateinit var context: Context

    init {
        this.upgradeList = cryptoCurrencies as ArrayList<FeaturesModel>
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): upgradeViewHolder {
        val itemView = LayoutInflater.from(parent?.context).inflate(
            R.layout.upgrade_list_item, parent, false
        )
        context = itemView.context



        return upgradeViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return 4 //upgradeList.size
    }

    override fun onBindViewHolder(holder: upgradeViewHolder, position: Int) {
        val cryptocurrencyItem = upgradeList[position]
        holder.upgradeListItem(cryptocurrencyItem)

        holder.itemView.setOnClickListener {
            val details = DetailsFragment.newInstance()
            val args = Bundle()
            args.putString("itemId", upgradeList.get(position).feature_code)
            details.arguments = args
            activity.addFragment(details, Constants.DETAILS_FRAGMENT)

//            val intent = Intent(this.context, Details::class.java)
//            intent.putExtra("position",position)
//            startActivity(this.context, intent, null)
        }
    }

    fun addupdates(upgradeModel: List<FeaturesModel>) {
        val initPosition = upgradeList.size
        upgradeList.clear()
        upgradeList.addAll(upgradeModel)
        notifyItemRangeInserted(initPosition, upgradeList.size)
    }

    class upgradeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var upgradeTitle = itemView.findViewById<TextView>(R.id.title)!!
        private var upgradeDetails = itemView.findViewById<TextView>(R.id.details)!!
        private var upgradePrice = itemView.findViewById<TextView>(R.id.upgrade_list_price)!!
        private var upgradeMRP = itemView.findViewById<TextView>(R.id.upgrade_list_orig_cost)!!
        private var upgradeDiscount = itemView.findViewById<TextView>(R.id.upgrade_list_discount)!!
        private var image = itemView.findViewById<ImageView>(R.id.imageView2)!!
        private var view = itemView.findViewById<View>(R.id.view)!!

        private var context: Context = itemView.context


        fun upgradeListItem(updateModel: FeaturesModel) {
            view.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
            val discount = 100 - updateModel.discount_percent
            val price = (discount * updateModel.price) / 100
            if(updateModel.target_business_usecase !=null) {
                upgradeTitle.text = updateModel.target_business_usecase
            }else{
                upgradeTitle.visibility = View.GONE
            }
            upgradeDetails.text = updateModel.name
            upgradePrice.text = "₹" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(price) + "/month"
            if(updateModel.discount_percent>0){
                upgradeDiscount.visibility = View.VISIBLE
                upgradeDiscount.text = ""+updateModel.discount_percent+"%"
                spannableString(updateModel.price)
            }else{
                upgradeDiscount.visibility = View.GONE
                upgradeMRP.visibility = View.GONE
            }
            if(updateModel.primary_image!=null) {
                Glide.with(context).load(updateModel.primary_image).into(image)
            }

        }

        fun spannableString(value: Int) {
            val origCost = SpannableString("₹" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(value) + "/month")

            origCost.setSpan(
                    StrikethroughSpan(),
                    0,
                    origCost.length,
                    0
            )
            upgradeMRP.setText(origCost)
        }
    }
}