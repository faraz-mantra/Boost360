package com.inventoryorder.ui.bottomsheet

import androidx.recyclerview.widget.LinearLayoutManager
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel
import com.inventoryorder.R
import com.inventoryorder.databinding.BottomSheetFilterBinding
import com.inventoryorder.model.bottomsheet.FilterModel
import com.inventoryorder.recyclerView.AppBaseRecyclerViewAdapter
import com.inventoryorder.recyclerView.RecyclerItemClickListener

class FilterBottomSheetDialog : BaseBottomSheetDialog<BottomSheetFilterBinding, BaseViewModel>(),
  RecyclerItemClickListener {

  private var list = ArrayList<FilterModel>()
  private var adapter: AppBaseRecyclerViewAdapter<FilterModel>? = null
  var onDoneClicked: (location: FilterModel?) -> Unit = { }


  override fun getLayout(): Int {
    return R.layout.bottom_sheet_filter
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }


  fun setList(list: ArrayList<FilterModel>) {
    this.list.clear()
    this.list.addAll(list)
  }

  override fun onCreateView() {
    binding?.recyclerView?.post {
      adapter = AppBaseRecyclerViewAdapter(baseActivity, list, this)
      binding?.recyclerView?.layoutManager = LinearLayoutManager(baseActivity)
      binding?.recyclerView?.adapter = adapter
      binding?.recyclerView?.let { adapter?.runLayoutAnimation(it) }
    }
  }

  override fun onItemClick(
    position: Int,
    item: com.inventoryorder.recyclerView.BaseRecyclerViewItem?,
    actionType: Int
  ) {
    val filterItem = item as? FilterModel
    list.forEach { it.isSelected = (it.type == filterItem?.type) }
    adapter?.notifyDataSetChanged()
    onDoneClicked(filterItem)
    dismiss()
  }
}