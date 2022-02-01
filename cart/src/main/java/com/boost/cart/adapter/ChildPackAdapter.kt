package com.boost.cart.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.boost.cart.R
import com.boost.dbcenterapi.upgradeDB.model.FeaturesModel
import com.bumptech.glide.Glide


class ChildPackAdapter(list: List<FeaturesModel>?,
) : RecyclerView.Adapter<ChildPackAdapter.upgradeViewHolder>() {

    private var bundlesList = ArrayList<FeaturesModel>()
    private lateinit var context: Context

    init {
        this.bundlesList = list as ArrayList<FeaturesModel>
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): upgradeViewHolder {
        val itemView = LayoutInflater.from(parent?.context).inflate(
            R.layout.item_pack_features, parent, false
        )
        context = itemView.context

        return upgradeViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return bundlesList.size
    }

    override fun onBindViewHolder(holder: upgradeViewHolder, position: Int) {
        val selectedBundle = bundlesList.get(position)

        holder.name.setText(bundlesList.get(position).name)
        // holder.title.setText(upgradeList.get(position).target_business_usecase)

        val cryptocurrencyItem = bundlesList[position]
   //     holder.upgradeListItem(holder, cryptocurrencyItem)

        Glide.with(context).load(bundlesList.get(position).primary_image).into(holder.image)

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
//        holder.itemView.setOnClickListener {
////      val details = DetailsFragment.newInstance()
////      val args = Bundle()
////      args.putString("itemId", upgradeList.get(position).feature_code)
////      args.putBoolean("packageView", true)
////      details.arguments = args
//
//            //  activity.addFragment(details, Constants.DETAILS_FRAGMENT)
//            val intent = Intent(context, FeatureDetailsActivity::class.java)
//            intent.putExtra("itemId", upgradeList.get(position).boost_widget_key)
//            context.startActivity(intent)
//        }
    }

    fun addupdates(upgradeModel: List<FeaturesModel>) {
        val initPosition = bundlesList.size
        bundlesList.clear()
        bundlesList.addAll(upgradeModel)
        notifyItemRangeInserted(initPosition, bundlesList.size)
    }

    class upgradeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val image = itemView.findViewById<ImageView>(R.id.imageView2)!!
        val name = itemView.findViewById<TextView>(R.id.title)!!
      //  val price = itemView.findViewById<TextView>(R.id.price)!!

//        fun upgradeListItem(holder: upgradeViewHolder, updateModel: FeaturesModel) {
//            val discount = 100 - updateModel.discount_percent
//            val price = (discount * updateModel.price) / 100
//            holder.price.text =
//                "₹" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(price) + "/month"
//
//        }


    }

}