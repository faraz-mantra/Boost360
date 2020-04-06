package com.boost.upgrades.adapter

import android.content.Context
import android.os.Bundle
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
            args.putString("itemId", upgradeList.get(position).boost_widget_key)
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

        private var context: Context = itemView.context


        fun upgradeListItem(updateModel: FeaturesModel) {
            val discount = 100 - updateModel.discount_percent
            val price = (discount * updateModel.price) / 100
            upgradeTitle.text = updateModel.target_business_usecase
            upgradeDetails.text = updateModel.name
            upgradePrice.text = "₹" + price + "/month"
            if(updateModel.discount_percent>0){
                upgradeDiscount.visibility = View.VISIBLE
                upgradeDiscount.text = "- "+ updateModel.discount_percent+"%"
                upgradeMRP.text = "₹" + updateModel.price + "/month"
            }else{
                upgradeDiscount.visibility = View.GONE
                upgradeMRP.visibility = View.GONE
            }
            if(updateModel.primary_image!=null) {
                Glide.with(context).load(updateModel.primary_image).into(image)
            }

        }
    }
}