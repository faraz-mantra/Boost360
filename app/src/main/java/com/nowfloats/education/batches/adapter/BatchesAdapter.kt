package com.nowfloats.education.batches.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nowfloats.education.batches.model.Data
import com.nowfloats.education.helper.ItemClickEventListener
import com.thinksity.databinding.BatchesItemBinding

class BatchesAdapter(private val eventListener: ItemClickEventListener) :
  RecyclerView.Adapter<BatchesAdapter.ViewHolder>() {
  var items: List<Data> = emptyList()
  private var menuPosition = -1
  private var menuStatus = false
  private lateinit var binding: BatchesItemBinding

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val inflater = LayoutInflater.from(parent.context)
    binding = BatchesItemBinding.inflate(inflater, parent, false)
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

  inner class ViewHolder(private val binding: BatchesItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bindView(batchesResponseData: Data, position: Int) {
      binding.batchesResponseData = batchesResponseData

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

      binding.editBatches.setOnClickListener {
        eventListener.onEditClick(batchesResponseData, position)
      }

      binding.deleteBatches.setOnClickListener {
        eventListener.onDeleteClick(batchesResponseData, position)
      }
    }
  }
}