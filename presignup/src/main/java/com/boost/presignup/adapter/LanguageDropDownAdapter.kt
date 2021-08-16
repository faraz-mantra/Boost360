package com.boost.presignup.adapter

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.RecyclerView
import com.boost.presignup.R

class LanguageDropDownAdapter(
  var context: Context,
  var list: List<String>,
  val listener: RecyclerViewClickListener
) :
  RecyclerView.Adapter<LanguageDropDownAdapter.ViewHolder>() {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val inflater = (context as Activity).layoutInflater
    val view = inflater.inflate(R.layout.language_single_item, parent, false)
    return ViewHolder(view)
  }

//    fun submitList(filterItemList: List<AllManufacturerResponseDataClass.Manufacturer>) {
//        list.addAll(filterItemList)
//        notifyDataSetChanged()
//    }

  override fun getItemCount(): Int {
    return if (list.size >= 0) {
      list.size
    } else {
      0
    }
  }

  override fun onBindViewHolder(holder: LanguageDropDownAdapter.ViewHolder, position: Int) {
    holder.textView.text = list[position]
    holder.languageLayout.setOnClickListener {
      listener.onClick(holder, position)
    }
  }


  inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val textView: AppCompatTextView = itemView.findViewById(R.id.language_type)
    val languageLayout: LinearLayoutCompat = itemView.findViewById(R.id.language_layout)
  }

  interface RecyclerViewClickListener {
    fun onClick(viewHolder: ViewHolder, itemPos: Int)
  }
}