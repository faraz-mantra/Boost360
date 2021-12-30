package com.boost.marketplace.adapter

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.boost.marketplace.R
import com.bumptech.glide.Glide

class ImagePreviewAdapter(imageList: List<String>) :
  RecyclerView.Adapter<ImagePreviewAdapter.ViewHolder>() {

  private lateinit var context: Context
  private var list = ArrayList<String>()

  init {
    list = imageList as ArrayList<String>
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val item = View.inflate(parent.context, R.layout.image_preview_item, null)
    val lp = ViewGroup.LayoutParams(
      ViewGroup.LayoutParams.MATCH_PARENT,
      ViewGroup.LayoutParams.MATCH_PARENT
    )
    item.layoutParams = lp
    context = item.context

    return ViewHolder(item)
  }

  override fun getItemCount(): Int {
    return list.size
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {

    Glide.with(context).load(list.get(position)).into(holder.image)
  }

  class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val image = itemView.findViewById<ImageView>(R.id.preview_image)

  }

}