package com.boost.marketplace.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.boost.marketplace.R
import com.boost.marketplace.interfaces.DetailsFragmentListener
import com.bumptech.glide.Glide

class SecondaryImagesAdapter(imageList: List<String>, listen: DetailsFragmentListener) :
  RecyclerView.Adapter<SecondaryImagesAdapter.ViewHolder>() {

  private lateinit var context: Context
  private var list = ArrayList<String>()
  private lateinit var listener: DetailsFragmentListener

  init {
    list = imageList as ArrayList<String>
    listener = listen
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val itemView = LayoutInflater.from(parent?.context).inflate(
      R.layout.secondary_image_item, parent, false
    )
    val lp = ViewGroup.LayoutParams(
      parent.width / 4,
      parent.width / 4
    )
    context = itemView.context

    itemView.layoutParams = lp
    return ViewHolder(itemView)
  }

  override fun getItemCount(): Int {
    return if (list.size > 4) 4 else list.size //list.size
  }

  fun addUpdates(imageList: List<String>) {
    val initPosition = list.size
    list.clear()
    list.addAll(imageList)
    notifyItemRangeInserted(initPosition, list.size)
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {

    Glide.with(context).load(list.get(position)).into(holder.image)
    if (position == 3) {
      holder.countLayout.visibility = View.VISIBLE
      holder.countValue.setText("+" + (list.size - 4))
    } else {
      holder.countLayout.visibility = View.GONE
    }
    holder.itemView.setOnClickListener {
      listener.imagePreviewPosition(list, position)
    }
  }

  class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val image = itemView.findViewById<ImageView>(R.id.secondary_single_image)
    val countLayout = itemView.findViewById<LinearLayout>(R.id.count_layout)
    val countValue = itemView.findViewById<TextView>(R.id.count_value)

  }

}