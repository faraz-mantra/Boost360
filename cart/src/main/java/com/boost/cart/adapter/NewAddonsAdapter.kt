package com.boost.cart.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.boost.cart.R
import com.boost.cart.interfaces.CartFragmentListener
import com.boost.cart.utils.SharedPrefs
import com.boost.dbcenterapi.upgradeDB.model.CartModel
import com.boost.dbcenterapi.upgradeDB.model.FeaturesModel
import com.bumptech.glide.Glide

class NewAddonsAdapter(
    val upgradeList: List<FeaturesModel>,
    val listener: CartFragmentListener,
    val bundleItem: CartModel,
    val activity: Activity
) : RecyclerView.Adapter<NewAddonsAdapter.upgradeViewHolder>() {

    var minMonth = 1
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): upgradeViewHolder {
        val itemView = LayoutInflater.from(parent?.context).inflate(
                R.layout.item_pack_features, parent, false
        )
        context = itemView.context

        return upgradeViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return upgradeList.size
    }

    override fun onBindViewHolder(holder: upgradeViewHolder, position: Int) {

        if(upgradeList.get(position).feature_code.equals("DOMAINPURCHASE") && upgradeList.get(position).name!!.contains(".")){
            holder.name.setText(upgradeList.get(position).name)
            holder.name.setTypeface(ResourcesCompat.getFont(context, R.font.bold))
            holder.notify.visibility = View.GONE
            val prefs = SharedPrefs(activity)
            if(prefs.getDomainOrderType() == 0)
                holder.edit.visibility = View.VISIBLE
            else
                holder.edit.visibility = View.GONE
        }else{
            if(upgradeList.get(position).feature_code.equals("DOMAINPURCHASE")){
                holder.notify.visibility = View.VISIBLE
            }else{
                holder.notify.visibility = View.GONE
            }
            holder.edit.visibility = View.GONE
            holder.name.setText(upgradeList.get(position).name)
            holder.name.setTypeface(ResourcesCompat.getFont(context, R.font.regular))
        }

        Glide.with(context).load(upgradeList.get(position).primary_image).into(holder.image)

        holder.edit.setOnClickListener {
            listener.editSelectedDomain(bundleItem)
        }


//        holder.itemView.setOnClickListener {
//            val details = DetailsFragment.newInstance()
//            val args = Bundle()
//            args.putString("itemId", upgradeList.get(position).feature_code)
//            args.putBoolean("packageView", true)
//            details.arguments = args
//
//            activity.addFragment(details, Constants.DETAILS_FRAGMENT)
//        }
    }

//    fun addupdates(upgradeModel: List<FeaturesModel>, noOfMonth: Int) {
//        minMonth = noOfMonth
//        val initPosition = upgradeList.size
//        upgradeList.clear()
//        upgradeList.addAll(upgradeModel)
//        notifyItemRangeInserted(initPosition, upgradeList.size)
//    }

    class upgradeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image = itemView.findViewById<ImageView>(R.id.imageView2)!!
        val name = itemView.findViewById<TextView>(R.id.title)!!
        val edit = itemView.findViewById<TextView>(R.id.edit)!!
        val notify = itemView.findViewById<ImageView>(R.id.notify)!!

    }


}