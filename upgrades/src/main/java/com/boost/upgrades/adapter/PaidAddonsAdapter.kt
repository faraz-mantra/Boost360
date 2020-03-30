package com.boost.upgrades.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.boost.upgrades.R
import com.boost.upgrades.UpgradeActivity
import com.boost.upgrades.data.model.WidgetModel
import com.boost.upgrades.interfaces.MyAddonsListener
import com.bumptech.glide.Glide


class PaidAddonsAdapter(
        val activity: UpgradeActivity,
        itemList: List<WidgetModel>?, var myAddonsListener: MyAddonsListener
) : RecyclerView.Adapter<PaidAddonsAdapter.upgradeViewHolder>(), View.OnClickListener {

    private var list = ArrayList<WidgetModel>()
    private lateinit var context: Context

    init {
        this.list = itemList as ArrayList<WidgetModel>
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): upgradeViewHolder {
        val itemView = LayoutInflater.from(parent?.context).inflate(
            R.layout.paid_addons_list_item, parent, false
        )
        context = itemView.context

        itemView.setOnClickListener(this)

        return upgradeViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return list.size //5
    }

    override fun onBindViewHolder(holder: upgradeViewHolder, position: Int) {
        val cryptocurrencyItem = list[position]
        holder.upgradeListItem(cryptocurrencyItem)
//
//        holder.itemView.setOnClickListener{
//            val details = DetailsFragment.newInstance()
//            val args = Bundle()
//            args.putInt("pos", position)
//            details.arguments = args
//            activity.addFragment(details , Constants.DETAILS_FRAGMENT)
////            val intent = Intent(this.context, Details::class.java)
////            intent.putExtra("position",position)
////            startActivity(this.context, intent, null)
//        }
        if (list.size - 1 == position) {
            holder.view.visibility = View.GONE
        }
    }

    fun addupdates(upgradeModel: List<WidgetModel>) {
        val initPosition = list.size
        list.clear()
        list.addAll(upgradeModel)
        notifyItemRangeInserted(initPosition, list.size)
    }


    override fun onClick(v: View?) {
        myAddonsListener.onPaidAddonsClicked(v)
    }

    class upgradeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var view = itemView.findViewById<View>(R.id.paid_single_dummy_view)!!
//        private var upgradeDetails = itemView.findViewById<TextView>(R.id.details)!!
//        private var upgradePrice = itemView.findViewById<TextView>(R.id.price)!!
        private var image = itemView.findViewById<ImageView>(R.id.single_paidaddon_image)!!
//
        private var context:Context  = itemView.context


        fun upgradeListItem(updateModel: WidgetModel) {
//            upgradeTitle.text = updateModel.title
//            upgradeDetails.text = updateModel.name
//            upgradePrice.text=updateModel.price
            Glide.with(context).load(updateModel.image).into(image)

    }
    }

}