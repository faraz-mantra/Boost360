package com.boost.upgrades.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.boost.upgrades.R
import com.boost.upgrades.UpgradeActivity
import com.boost.dbcenterapi.upgradeDB.model.*
import com.boost.upgrades.interfaces.RemoveItemsListener
import com.bumptech.glide.Glide


class RemoveAddonsAdapter(
  val activity: UpgradeActivity,
  itemList: List<CartModel>?, val listener: RemoveItemsListener
) : RecyclerView.Adapter<RemoveAddonsAdapter.upgradeViewHolder>() {

  private var list = ArrayList<CartModel>()
  private lateinit var context: Context

  init {
    this.list = itemList as ArrayList<CartModel>
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): upgradeViewHolder {
    val itemView = LayoutInflater.from(parent?.context).inflate(
      R.layout.remove_list_item, parent, false
    )
    context = itemView.context


    return upgradeViewHolder(itemView)
  }

  override fun getItemCount(): Int {
    return list.size //5
  }

  override fun onBindViewHolder(holder: upgradeViewHolder, position: Int) {
    val cryptocurrencyItem = list[position]
    holder.upgradeListItem(cryptocurrencyItem)
    holder.selectStatus.setOnCheckedChangeListener { buttonView, isChecked ->
      if (isChecked) {
        listener.removeItemFromCart(list.get(position))
      } else {
        listener.addItemToCart(list.get(position))
      }
    }

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

  fun addupdates(upgradeModel: List<CartModel>) {
    val initPosition = list.size
    list.clear()
    list.addAll(upgradeModel)
    notifyItemRangeInserted(initPosition, list.size)
  }


  class upgradeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var view = itemView.findViewById<View>(R.id.remove_addons_view)!!
    var selectStatus = itemView.findViewById<CheckBox>(R.id.remove_addons_check)!!

    //        private var upgradePrice = itemView.findViewById<TextView>(R.id.price)!!
    private var image = itemView.findViewById<ImageView>(R.id.remove_addons_image)!!

    //
    private var context: Context = itemView.context


    fun upgradeListItem(item: CartModel) {
//            upgradeTitle.text = updateModel.title
//            upgradeDetails.text = updateModel.name
//            upgradePrice.text=updateModel.price
      Glide.with(context).load(item.link).into(image)

    }
  }

}