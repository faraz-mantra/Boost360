package com.boost.marketplace.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.boost.dbcenterapi.upgradeDB.model.FeaturesModel
import com.boost.dbcenterapi.utils.SharedPrefs
import com.boost.marketplace.R
import com.boost.marketplace.interfaces.AddonsListener
import com.boost.marketplace.ui.Compare_Plans.ComparePacksActivity
import com.boost.marketplace.ui.browse.SearchActivity
import com.bumptech.glide.Glide
import java.text.NumberFormat
import java.util.*


class CompareItemAdapter(
    cryptoCurrencies: List<FeaturesModel>?,
    val addonsListener: AddonsListener,
    val activity: ComparePacksActivity?,
    val activity2: SearchActivity?,
    val activity3: FragmentActivity?
) :
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
        if (activity != null) {
            holder.upgradeListItem(holder, cryptocurrencyItem, activity, null, null)

        } else if (activity2 != null) {
            holder.upgradeListItem(holder, cryptocurrencyItem, null, activity2, null)

        } else {
            holder.upgradeListItem(holder, cryptocurrencyItem, null, null, activity3)

        }
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
//            val intent = Intent(context, FeatureDetailsActivity::class.java)
//            intent.putExtra("itemId", upgradeList.get(position).boost_widget_key)
//            context.startActivity(intent)
            addonsListener.onAddonsClicked(upgradeList.get(position))
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

        fun upgradeListItem(
            holder: upgradeViewHolder,
            updateModel: FeaturesModel,
            activity: ComparePacksActivity?,
            activity2: SearchActivity?,
            activity3: FragmentActivity?
        ) {
            var prefs: SharedPrefs? = null

            if (activity != null) {
                prefs = SharedPrefs(activity)
            } else if (activity2 != null) {
                prefs = SharedPrefs(activity2)
            } else {
                prefs = SharedPrefs(activity3!!)

            }
            val discount = 100 - updateModel.discount_percent
            var price = (discount * updateModel.price) / 100
            if (prefs.getYearPricing()) {
                price = price * 12
                holder.price.text =
                    "₹" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(price) + "/year"
            } else {
                holder.price.text =
                    "₹" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(price) + "/month"
            }

        }


    }

}