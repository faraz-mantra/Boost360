package com.inventoryorder.ui.bottomsheet

import androidx.recyclerview.widget.LinearLayoutManager
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.inventoryorder.R
import com.inventoryorder.databinding.BottomSheetFilterBinding
import com.inventoryorder.databinding.BottomSheetTimeSlotBinding
import com.inventoryorder.model.bottomsheet.FilterModel
import com.inventoryorder.model.timeSlot.TimeSlotData
import com.inventoryorder.recyclerView.AppBaseRecyclerViewAdapter
import com.inventoryorder.recyclerView.BaseRecyclerViewItem
import com.inventoryorder.recyclerView.RecyclerItemClickListener

class TimeSlotBottomSheetDialog :
  BaseBottomSheetDialog<BottomSheetTimeSlotBinding, BaseViewModel>(), RecyclerItemClickListener {

  private var list = ArrayList<TimeSlotData>()
  private var adapter: AppBaseRecyclerViewAdapter<TimeSlotData>? = null
  var onDoneClicked: (location: TimeSlotData?) -> Unit = { }


  override fun getLayout(): Int {
    return R.layout.bottom_sheet_time_slot
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }


  fun setList(list: ArrayList<TimeSlotData>) {
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

  override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
    val filterItem = item as? TimeSlotData
    list.forEach { it.isSelected = (it.getTimeSlotText() == filterItem?.getTimeSlotText()) }
    adapter?.notifyDataSetChanged()
    onDoneClicked(filterItem)
    dismiss()
  }
}