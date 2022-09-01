package com.boost.marketplace.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.boost.dbcenterapi.upgradeDB.local.AppDatabase
import com.boost.dbcenterapi.upgradeDB.model.FeaturesModel
import com.boost.dbcenterapi.utils.Utils
import com.boost.marketplace.R
import com.boost.marketplace.interfaces.AddonsListener
import com.boost.marketplace.ui.browse.BrowseFeaturesActivity
import com.framework.analytics.SentryController
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class BrowseParentFeaturesAdapter(
    var upgradeList: ArrayList<String>,
    val activity: BrowseFeaturesActivity,
    val addonsListener: AddonsListener
) : RecyclerView.Adapter<BrowseParentFeaturesAdapter.ViewHolder>() {

    lateinit var context: Context
    var accountType = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val item = View.inflate(parent.context, R.layout.item_browse_features, null)
        context = item.context
        return ViewHolder(item)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.title.setText(upgradeList.get(position))
        if(accountType.isNotEmpty()) {
            holder.desc.setText("Features that $accountType are liking most.")
            if(position != 0)
                holder.desc.visibility = View.VISIBLE
        }else{
            holder.desc.visibility = View.GONE
        }
        getFeaturesByType(holder, position)
        if(position == 0){
            holder.titleLayout.visibility = View.GONE
        }else{
            holder.titleLayout.visibility = View.VISIBLE
        }

    }

    override fun getItemCount(): Int {
        return upgradeList.size
    }

    fun addupdates(upgradeModel: List<String>) {
        val initPosition = upgradeList.size
        upgradeList.clear()
        upgradeList.addAll(upgradeModel)
        notifyItemRangeInserted(initPosition, upgradeList.size)
    }

    fun updateAccountType(accountType: String){
        this.accountType = accountType
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title = itemView.findViewById<TextView>(R.id.title)
        val desc = itemView.findViewById<TextView>(R.id.desc)
        val titleLayout = itemView.findViewById<LinearLayout>(R.id.feature_name_ll)
        val recyclerview = itemView.findViewById<RecyclerView>(R.id.recyclerView)
    }


    fun getFeaturesByType(holder: ViewHolder, position: Int) {
        if (position == 0 && upgradeList.size!=1) {
            CompositeDisposable().add(
                AppDatabase.getInstance(activity.application)!!
                    .featuresDao()
                    .getFeaturesItems(true)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        val tempList = ArrayList(it.subList(0,4))
                        val list = ArrayList<FeaturesModel>()
                        for(singleItem in tempList){
                            if(!singleItem.feature_code.equals("LIMITED_CONTENT")){
                                list.add(singleItem)
                            }
                        }
                        val adapter = BrowseChildFeaturesAdapter(list, accountType , addonsListener, activity)
                        holder.recyclerview.adapter = adapter
                        adapter.notifyDataSetChanged()
                    }, {
                        it.printStackTrace()
                    })
            )
        } else {
            CompositeDisposable().add(
                AppDatabase.getInstance(activity.application)!!
                    .featuresDao()
                    .getFeaturesItemsByTypePremium(upgradeList.get(position), "MERCHANT_TRAINING", true)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        val list = ArrayList<FeaturesModel>()
                        for(singleItem in it){
                            if(!singleItem.feature_code.equals("LIMITED_CONTENT")){
                                list.add(singleItem)
                            }
                        }
                        val adapter = BrowseChildFeaturesAdapter(list, accountType, addonsListener, activity)
                        holder.recyclerview.adapter = adapter
                        adapter.notifyDataSetChanged()
                    }, {
                        it.printStackTrace()
                    })
            )
        }
    }


}