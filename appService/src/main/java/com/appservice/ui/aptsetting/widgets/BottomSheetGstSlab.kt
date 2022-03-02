package com.appservice.ui.aptsetting.widgets

import com.appservice.R
import com.appservice.constant.IntentConstant
import com.appservice.databinding.BottomSheetBankAccountVerifiedBinding
import com.appservice.databinding.BottomSheetGstSlabBinding
import com.appservice.model.GstDetailModel
import com.appservice.model.aptsetting.UserFpDetailsResponse
import com.appservice.recyclerView.AppBaseRecyclerViewAdapter
import com.appservice.recyclerView.BaseRecyclerViewItem
import com.appservice.recyclerView.RecyclerItemClickListener
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel

class BottomSheetGstSlab : BaseBottomSheetDialog<BottomSheetGstSlabBinding, BaseViewModel>(), RecyclerItemClickListener {

  var onClicked: (gstSlab: Int) -> Unit = { _: Int -> }
  private var gstSlab: Int = 0
  private var adapterN: AppBaseRecyclerViewAdapter<GstDetailModel>? = null
  private var listGst: ArrayList<GstDetailModel> = arrayListOf()

  override fun getLayout(): Int {
    return R.layout.bottom_sheet_gst_slab
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun onCreateView() {
    this.gstSlab = arguments?.getInt(IntentConstant.GST_DATA.name) ?: 0
    binding?.btnContinue?.setOnClickListener {
      dismiss()
      onClicked(gstSlab)
    }
    binding?.ivClose?.setOnClickListener { dismiss() }
    listGst = GstDetailModel().gstDataNew(gstSlab)
    binding?.rvGst?.apply {
      adapterN = AppBaseRecyclerViewAdapter(baseActivity, listGst, this@BottomSheetGstSlab)
      adapter = adapterN
    }
  }

  override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
    this.gstSlab = (item as? GstDetailModel)?.value?.toIntOrNull() ?: 0
    listGst = ArrayList(listGst.map { it.isSelected = (it.value?.toIntOrNull() ?: 0 == gstSlab);it })
    adapterN?.notify(listGst)
  }
}