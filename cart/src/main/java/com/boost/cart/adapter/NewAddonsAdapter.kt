package com.boost.cart.adapter

import android.app.Activity
import android.content.Context
import android.graphics.Color
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
        val prefs = SharedPrefs(activity)

        if(upgradeList.get(position).feature_code.equals("DOMAINPURCHASE")
           // && upgradeList.get(position).name!!.contains(".")
        ){
          //  holder.name.setText(upgradeList.get(position).name)
            if(!prefs.getSelectedDomainName().isNullOrEmpty()) {
                holder.name.text = upgradeList.get(position).name
                holder.desc.text = prefs.getSelectedDomainName()
                holder.desc.visibility=View.VISIBLE
                holder.desc_image.visibility=View.VISIBLE
                holder.edit.visibility=View.VISIBLE
                holder.select.visibility=View.GONE
                holder.notify.visibility=View.GONE
                holder.desc_image.setImageResource(R.drawable.ic_green_tickss)
            }else{
                holder.name.text = upgradeList.get(position).name
                holder.desc.text = "Domain not Selected yet"
                holder.desc.setTextColor(Color.parseColor("#EB5757"))
                holder.edit.visibility=View.GONE
                holder.select.visibility=View.VISIBLE
                holder.desc.visibility=View.VISIBLE
                holder.notify.visibility=View.VISIBLE
                holder.desc_image.visibility=View.VISIBLE
            }
//            holder.name.setTypeface(ResourcesCompat.getFont(context, R.font.bold))
//            holder.notify.visibility = View.GONE
            val prefs = SharedPrefs(activity)
            holder.select.setOnClickListener {
                listener.editSelectedDomain(bundleItem)
            }
            if(prefs.getDomainOrderType() == 0 && !prefs.getSelectedDomainName().isNullOrEmpty()){
                holder.edit.visibility = View.VISIBLE
                holder.edit.setOnClickListener {
                    listener.editSelectedDomain(bundleItem)
                }
            }
            else{
                holder.edit.visibility = View.GONE
            }
        }else if((upgradeList.get(position).feature_code.equals("CALLTRACKER"))
           // || upgradeList.get(position).feature_code.equals("IVR"))
           // && upgradeList.get(position).name!!.contains("[0-9]".toRegex())
        ){
//            holder.name.setText(upgradeList.get(position).name)
//            holder.name.setTypeface(ResourcesCompat.getFont(context, R.font.bold))
            if(!prefs.getSelectedVMNName().isNullOrEmpty()) {
                holder.name.text = upgradeList.get(position).name
                holder.desc.text = prefs.getSelectedVMNName()
                holder.desc.visibility=View.VISIBLE
                holder.desc_image.visibility=View.VISIBLE
                holder.edit.visibility=View.VISIBLE
                holder.select.visibility=View.GONE
                holder.notify.visibility=View.GONE
                holder.desc_image.setImageResource(R.drawable.ic_green_tickss)
            }else{
                holder.name.text = upgradeList.get(position).name
                holder.desc.text = "VMN not selected yet"
                holder.desc.setTextColor(Color.parseColor("#EB5757"))
                holder.desc.visibility=View.VISIBLE
                holder.edit.visibility=View.GONE
                holder.select.visibility=View.VISIBLE
                holder.notify.visibility=View.VISIBLE
                holder.desc_image.visibility=View.VISIBLE
            }
         //   holder.notify.visibility = View.GONE
            val prefs = SharedPrefs(activity)
            holder.select.setOnClickListener {
                listener.editSelectedVmn(bundleItem)
            }
            if(prefs.getVmnOrderType() == 0 && !prefs.getSelectedVMNName().isNullOrEmpty()){
                holder.edit.visibility = View.VISIBLE
                holder.edit.setOnClickListener {
                    listener.editSelectedVmn(bundleItem)
                }
            }
            else
                holder.edit.visibility = View.GONE
        } else if((upgradeList.get(position).feature_code.equals("DICTATE"))
            // || upgradeList.get(position).feature_code.equals("IVR"))
           // && (!prefs.getSelectedDictatePrefs().isNullOrEmpty() )
        ){
//            holder.name.setText(upgradeList.get(position).name)
//            holder.name.setTypeface(ResourcesCompat.getFont(context, R.font.regular))
//            holder.notify.visibility = View.GONE
            if(!prefs.getSelectedDictatePrefs().isNullOrEmpty()) {
                holder.name.text = upgradeList.get(position).name
                holder.desc.text = "Preferences added"
                holder.desc.visibility=View.VISIBLE
                holder.desc_image.visibility=View.VISIBLE
                holder.edit.visibility=View.VISIBLE
                holder.select.visibility=View.GONE
                holder.notify.visibility=View.GONE
                holder.desc_image.setImageResource(R.drawable.ic_green_tickss)
            }else{
                holder.name.text = upgradeList.get(position).name
                holder.desc.visibility=View.VISIBLE
                holder.desc.text = "Preferences pending"
                holder.desc.setTextColor(Color.parseColor("#EB5757"))
                holder.edit.visibility=View.GONE
                holder.select.visibility=View.VISIBLE
                holder.notify.visibility=View.VISIBLE
                holder.desc_image.visibility=View.VISIBLE
            }
            val prefs = SharedPrefs(activity)
            holder.select.setOnClickListener {
                listener.editSelectedPreferences(bundleItem)
            }
            if(prefs.getSelectedDictatePrefs().equals("Mandatory")){
                holder.edit.visibility = View.VISIBLE
                holder.edit.setOnClickListener {
                    listener.editSelectedPreferences(bundleItem)
                }
            }
            else
                holder.edit.visibility = View.GONE
        }

        else {
            holder.edit.visibility = View.GONE
            holder.select.visibility=View.GONE
            holder.name.setText(upgradeList.get(position).name)
            holder.name.setTypeface(ResourcesCompat.getFont(context, R.font.regular))
            if(upgradeList.get(position).feature_code.equals("DOMAINPURCHASE")){
                holder.notify.visibility = View.VISIBLE
            }
            if(upgradeList.get(position).feature_code.equals("CALLTRACKER")
            ){
                holder.notify.visibility = View.VISIBLE
            }
            if(upgradeList.get(position).feature_code.equals("DICTATE")
            ){
                holder.notify.visibility = View.VISIBLE
            }
        }

        Glide.with(context).load(upgradeList.get(position).primary_image).into(holder.image)

//        holder.edit.setOnClickListener {
//            listener.editSelectedDomain(bundleItem)
//        }


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
        val desc_image = itemView.findViewById<ImageView>(R.id.imageView3)!!
        val name = itemView.findViewById<TextView>(R.id.title)!!
        val desc = itemView.findViewById<TextView>(R.id.title1)!!
        val edit = itemView.findViewById<TextView>(R.id.edit)!!
        val select = itemView.findViewById<TextView>(R.id.select)!!
        val notify = itemView.findViewById<ImageView>(R.id.notify)!!

    }


}