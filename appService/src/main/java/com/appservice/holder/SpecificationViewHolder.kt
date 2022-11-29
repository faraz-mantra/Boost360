package com.appservice.holder

import android.view.View
import com.appservice.R
import com.appservice.constant.RecyclerViewActionType
import com.appservice.databinding.RowLayoutAddedSpecsBinding
import com.appservice.model.KeySpecification
import com.appservice.recyclerView.AppBaseRecyclerViewHolder
import com.appservice.recyclerView.BaseRecyclerViewItem
import com.framework.extensions.afterTextChanged

class SpecificationViewHolder(binding: RowLayoutAddedSpecsBinding) :
  AppBaseRecyclerViewHolder<RowLayoutAddedSpecsBinding>(binding) {

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    val keySpecification = item as? KeySpecification

    binding.btnRemoveView.visibility = if (position == 0) View.INVISIBLE else View.VISIBLE

    binding.type.setText(keySpecification?.key ?: "")
    binding.value.setText(keySpecification?.value ?: "")

    if (keySpecification?.key.isNullOrBlank().not() && keySpecification?.value.isNullOrBlank().not()){
      binding.type.setBackgroundResource(android.R.color.transparent)
      binding.value.setBackgroundResource(android.R.color.transparent)
    }
    if (keySpecification?.key.isNullOrBlank() && keySpecification?.value.isNullOrBlank().not()){
      binding.type.setBackgroundResource(R.drawable.error_red_filled_pink_left_rounded)
      binding.value.setBackgroundResource(android.R.color.transparent)
    }
    if (keySpecification?.value.isNullOrBlank() && keySpecification?.key.isNullOrBlank().not()){
      binding.type.setBackgroundResource(android.R.color.transparent)
      binding.value.setBackgroundResource(R.drawable.error_red_filled_pink_righ_rounded)
    }

    binding.type.afterTextChanged {
        keySpecification?.key = it
        listener?.onItemClick(position, keySpecification, RecyclerViewActionType.UPDATE_SPECIFICATION_VALUE.ordinal)
    }
    binding.value.afterTextChanged {
        keySpecification?.value = it
        listener?.onItemClick(position, keySpecification, RecyclerViewActionType.UPDATE_SPECIFICATION_VALUE.ordinal)
    }
    binding.btnRemoveView.setOnClickListener {
      activity?.currentFocus?.clearFocus()
      listener?.onItemClick(position, keySpecification, RecyclerViewActionType.CLEAR_SPECIFICATION_CLICK.ordinal)
    }
  }
}
