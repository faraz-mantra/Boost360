package com.inventoryorder.holders

import androidx.core.content.ContextCompat
import com.inventoryorder.constant.RecyclerViewActionType
import com.inventoryorder.databinding.ItemBottomSheetServiceLocationsBinding
import com.inventoryorder.model.bottomsheet.LocationsModel
import com.inventoryorder.recyclerView.AppBaseRecyclerViewHolder
import com.inventoryorder.recyclerView.BaseRecyclerViewItem

class LocationsViewHolder(binding: ItemBottomSheetServiceLocationsBinding) :
  AppBaseRecyclerViewHolder<ItemBottomSheetServiceLocationsBinding>(binding) {


  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    val data = item as LocationsModel
    setDataForServiceLocations(data)
  }

  private fun setDataForServiceLocations(model: LocationsModel) {
    model.getIcon()?.let { binding.ivOptionSelected.setImageResource(it) }
    binding.tvOptionSelected.text = model.serviceOptionSelectedName
    binding.mainView.background = activity?.let { ContextCompat.getDrawable(it, model.getColor()) }
    binding.mainView.setOnClickListener {
      listener?.onItemClick(
        adapterPosition,
        model,
        RecyclerViewActionType.BOOKING_ITEM_CLICKED.ordinal
      )
    }

  }

}