package com.inventoryorder.ui.appointmentSpa.create

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel
import com.inventoryorder.R
import com.inventoryorder.constant.RecyclerViewActionType
import com.inventoryorder.databinding.*
import com.inventoryorder.model.order.orderbottomsheet.BottomSheetOptionsItem
import com.inventoryorder.model.order.orderbottomsheet.OrderBottomSheet
import com.inventoryorder.recyclerView.AppBaseRecyclerViewAdapter
import com.inventoryorder.recyclerView.BaseRecyclerViewItem
import com.inventoryorder.recyclerView.RecyclerItemClickListener

class CreateOrderBottomSheetDialog(val orderBottomSheet: OrderBottomSheet) : BaseBottomSheetDialog<BottomSheetOrderBinding, BaseViewModel>(), RecyclerItemClickListener {

  var onClicked: (bottomSheetOptionItem: BottomSheetOptionsItem, orderBottomSheet: OrderBottomSheet) -> Unit = { bottomsheetOptionItem: BottomSheetOptionsItem, orderBottomSheet: OrderBottomSheet -> }
  private var optionsAdapter: AppBaseRecyclerViewAdapter<BottomSheetOptionsItem>? = null
  private var layoutManagerN: LinearLayoutManager? = null
  private var currentOptionPosition = 0
  private var currentSelectedItem: BottomSheetOptionsItem? = null

  override fun getLayout(): Int {
    return R.layout.bottom_sheet_order
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun onCreateView() {
    layoutManagerN = LinearLayoutManager(baseActivity)
    binding?.tvTitle?.text = orderBottomSheet.title
    if (orderBottomSheet.decription?.isNotEmpty() == true) {
      binding?.tvSubTitle?.text = orderBottomSheet.decription
    } else binding?.tvSubTitle?.visibility = View.GONE
    currentSelectedItem = orderBottomSheet.items?.get(0)
    setOnClickListener(binding?.buttonDone, binding?.tvCancel)
    setUpRecyclerView()
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.buttonDone -> {
        onClicked(currentSelectedItem!!, orderBottomSheet)
        dismiss()
      }
      binding?.tvCancel -> dismiss()
    }
  }

  private fun setUpRecyclerView() {
    binding?.recyclerOrderOptions?.apply {
      optionsAdapter = AppBaseRecyclerViewAdapter(baseActivity, orderBottomSheet.items!!, this@CreateOrderBottomSheetDialog)
      layoutManager = layoutManagerN
      adapter = optionsAdapter
    }
  }

  override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
    if (actionType == RecyclerViewActionType.ORDER_OPTION_SELECTED.ordinal) {
      currentOptionPosition = position
      currentSelectedItem = orderBottomSheet.items?.get(currentOptionPosition)
      orderBottomSheet.items?.forEach { it.isChecked = (it.serverValue == currentSelectedItem?.serverValue) }
      optionsAdapter?.notifyDataSetChanged()
    }
  }
}