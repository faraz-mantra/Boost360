package com.nowfloats.Restaurants.BookATable.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nowfloats.Restaurants.API.model.GetBookTable.Data
import com.nowfloats.Restaurants.Interfaces.BookTableFragmentListener
import com.thinksity.R


class BookTableAdapter(var itemList: List<Data>, val listener: BookTableFragmentListener) :
  RecyclerView.Adapter<BookTableAdapter.ViewHolder>() {

  lateinit var context: Context
  private var menuPosition = -1
  private var menuStatus = false

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val itemView = LayoutInflater.from(parent?.context).inflate(
      R.layout.item_book_table, parent, false
    )
    context = itemView.context
    return ViewHolder(itemView)
  }

  override fun getItemCount(): Int {
    return itemList.size
  }

  fun menuOption(pos: Int, status: Boolean) {
    menuPosition = pos
    menuStatus = status
  }

  fun updateList(list: List<Data>) {
    itemList = list
    notifyDataSetChanged()
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    holder.menuOptionLayout.setVisibility(View.GONE)
    if (menuPosition == position) {
      if (menuStatus) {
        holder.menuOptionLayout.setVisibility(View.VISIBLE)
      } else {
        holder.menuOptionLayout.setVisibility(View.GONE)
      }
    }
    holder.itemView.setOnClickListener { listener.itemMenuOptionStatus(-1, false) }

    holder.menuButton.setOnClickListener {
      if (holder.menuOptionLayout.getVisibility() == View.GONE) {
        listener.itemMenuOptionStatus(position, true)
      } else {
        listener.itemMenuOptionStatus(position, false)
      }
    }

    holder.editButton.setOnClickListener {
      listener.editOptionClicked(itemList.get(position))
    }

    holder.deleteButton.setOnClickListener {
      listener.deleteOptionClicked(itemList.get(position))
    }

    holder.userName.setText(itemList.get(position).name)
    holder.dateValue.setText(itemList.get(position).date)
    holder.timeValue.setText(itemList.get(position).time)
    holder.messageValue.setText(itemList.get(position).message)

    if (itemList.get(position).totalPeople.equals("0")) {
      holder.tableCount.visibility = View.GONE
    } else {
      holder.tableCount.visibility = View.VISIBLE
      holder.tableCount.setText("+" + itemList.get(position).totalPeople)
    }


    val origCost = SpannableString(itemList.get(position).number)

    origCost.setSpan(
      UnderlineSpan(),
      0,
      origCost.length,
      0
    )
    holder.contactNumber.text = origCost

    holder.contactNumber.setOnClickListener {
      val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + itemList.get(position).number))
      context.startActivity(intent)
    }

  }

  class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var menuButton = itemView.findViewById<ImageView>(R.id.single_item_menu_button);
    var menuOptionLayout = itemView.findViewById<LinearLayout>(R.id.menu_options)!!

    var editButton = itemView.findViewById<TextView>(R.id.edit_button)
    var deleteButton = itemView.findViewById<TextView>(R.id.delete_button)

    var userName = itemView.findViewById<TextView>(R.id.user_name)!!
    var tableCount = itemView.findViewById<TextView>(R.id.table_count)!!
    var contactNumber = itemView.findViewById<TextView>(R.id.contact_number)!!
    var dateValue = itemView.findViewById<TextView>(R.id.date_value)!!
    var timeValue = itemView.findViewById<TextView>(R.id.time_value)!!
    var messageValue = itemView.findViewById<TextView>(R.id.message_value)!!

  }
}