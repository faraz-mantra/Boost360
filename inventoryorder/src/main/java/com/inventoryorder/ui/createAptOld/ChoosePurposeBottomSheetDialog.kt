package com.inventoryorder.ui.createAptOld

import androidx.recyclerview.widget.LinearLayoutManager
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel
import com.inventoryorder.R
import com.inventoryorder.databinding.BottomSheetChoosePurposeBinding
import com.inventoryorder.model.bottomsheet.ChoosePurposeModel
import com.inventoryorder.recyclerView.AppBaseRecyclerViewAdapter
import com.inventoryorder.recyclerView.BaseRecyclerViewItem
import com.inventoryorder.recyclerView.RecyclerItemClickListener

class ChoosePurposeBottomSheetDialog :
  BaseBottomSheetDialog<BottomSheetChoosePurposeBinding, BaseViewModel>(),
  RecyclerItemClickListener {

  private var list = ArrayList<ChoosePurposeModel>()
  private var adapter: AppBaseRecyclerViewAdapter<ChoosePurposeModel>? = null
  var onDoneClicked: (choosePurposeModel: ChoosePurposeModel?) -> Unit = {}

  override fun getLayout(): Int {
    return R.layout.bottom_sheet_choose_purpose
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  fun setList(list: ArrayList<ChoosePurposeModel>) {
    this.list.clear()
    this.list.addAll(list)
  }

  override fun onCreateView() {
    binding?.recyclerViewBottomSheetChoosePurpose?.post {
      adapter = AppBaseRecyclerViewAdapter(baseActivity, list, this)
      binding?.recyclerViewBottomSheetChoosePurpose?.layoutManager =
        LinearLayoutManager(baseActivity)
      binding?.recyclerViewBottomSheetChoosePurpose?.adapter = adapter
      binding?.recyclerViewBottomSheetChoosePurpose?.let { adapter?.runLayoutAnimation(it) }
    }
  }

  override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
    var choosePurposeModel = item as? ChoosePurposeModel
    list.forEach {
      it.isSelected =
        (it.choosePurposeSelectedName == choosePurposeModel?.choosePurposeSelectedName)
    }
    adapter?.notifyDataSetChanged()
    onDoneClicked((item as? @kotlin.ParameterName(name = "choosePurposeModel") ChoosePurposeModel))
    dismiss()
  }


}