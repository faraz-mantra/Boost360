package com.nowfloats.education.toppers.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nowfloats.education.helper.ItemClickEventListener
import com.nowfloats.education.toppers.model.Data
import com.thinksity.databinding.ToppersRowBinding

class TopperAdapter(private val eventListener: ItemClickEventListener) :
  RecyclerView.Adapter<TopperAdapter.ViewHolder>() {
  var items: List<Data> = emptyList()
  private var menuPosition = -1
  private var menuStatus = false
  private lateinit var binding: ToppersRowBinding

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val inflater = LayoutInflater.from(parent.context)
    binding = ToppersRowBinding.inflate(inflater, parent, false)
    return ViewHolder(binding)
  }

  fun menuOption(pos: Int, status: Boolean) {
    menuPosition = pos
    menuStatus = status
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    holder.bindView(items[position], position)
  }

  override fun getItemCount(): Int {
    return items.size
  }

  inner class ViewHolder(private val binding: ToppersRowBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bindView(ourTopperResponseData: Data, position: Int) {
      binding.ourTopperResponseData = ourTopperResponseData

      binding.menuOptions.visibility = View.GONE
      if (menuPosition == position) {
        if (menuStatus) {
          binding.menuOptions.visibility = View.VISIBLE
        } else {
          binding.menuOptions.visibility = View.GONE
        }
      }
      binding.mainLayout.setOnClickListener { eventListener.itemMenuOptionStatus(position, false) }
      binding.singleItemMenuButton.setOnClickListener {
        if (binding.menuOptions.visibility == View.GONE) {
          eventListener.itemMenuOptionStatus(position, true)
        } else {
          eventListener.itemMenuOptionStatus(position, false)
        }
      }

      binding.editToppers.setOnClickListener {
        eventListener.onEditClick(ourTopperResponseData, position)
      }

      binding.deleteToppers.setOnClickListener {
        eventListener.onDeleteClick(ourTopperResponseData, position)
      }
    }
  }
}