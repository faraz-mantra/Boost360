package com.appservice.ui.catalog.widgets

import com.appservice.R
import com.appservice.databinding.BottomSheetGstDetailBinding
import com.appservice.model.GstDetailModel
import com.appservice.recyclerView.AppBaseRecyclerViewAdapter
import com.appservice.recyclerView.BaseRecyclerViewItem
import com.appservice.recyclerView.RecyclerItemClickListener
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel

class GstDetailsBottomSheet : BaseBottomSheetDialog<BottomSheetGstDetailBinding, BaseViewModel>(),
  RecyclerItemClickListener {

  var onClicked: (value: String) -> Unit = { }
  override fun getLayout(): Int {
    return R.layout.bottom_sheet_gst_detail
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun onCreateView() {
    binding?.rvGstDetail?.apply {
      val adapterN = AppBaseRecyclerViewAdapter(
        baseActivity,
        GstDetailModel().gstData(),
        this@GstDetailsBottomSheet
      )
      adapter = adapterN
    }
  }

  override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
    dismiss()
    (item as GstDetailModel).value?.let { onClicked(it) }
  }
}