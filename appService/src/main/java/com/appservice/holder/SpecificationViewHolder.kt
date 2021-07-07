package com.appservice.holder

import android.view.View
import com.appservice.constant.RecyclerViewActionType
import com.appservice.databinding.RowLayoutAddedSpecsBinding
import com.appservice.model.KeySpecification
import com.appservice.recyclerView.AppBaseRecyclerViewHolder
import com.appservice.recyclerView.BaseRecyclerViewItem
import com.framework.extensions.onTextChanged

class SpecificationViewHolder(binding: RowLayoutAddedSpecsBinding) :
  AppBaseRecyclerViewHolder<RowLayoutAddedSpecsBinding>(binding) {

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    val keySpecification = item as? KeySpecification
    binding.btnRemoveView.visibility = if (position == 0) View.INVISIBLE else View.VISIBLE
    binding.type.setText(keySpecification?.key ?: "")
    binding.value.setText(keySpecification?.value ?: "")
    binding.type.onTextChanged {
      keySpecification?.key = it
      listener?.onItemClick(
        position,
        keySpecification,
        RecyclerViewActionType.UPDATE_SPECIFICATION_VALUE.ordinal
      )
    }
    binding.value.onTextChanged {
      keySpecification?.value = it
      listener?.onItemClick(
        position,
        keySpecification,
        RecyclerViewActionType.UPDATE_SPECIFICATION_VALUE.ordinal
      )
    }
    binding.btnRemoveView.setOnClickListener {
      activity?.currentFocus?.clearFocus()
      listener?.onItemClick(
        position,
        keySpecification,
        RecyclerViewActionType.CLEAR_SPECIFICATION_CLICK.ordinal
      )
    }
  }
}
