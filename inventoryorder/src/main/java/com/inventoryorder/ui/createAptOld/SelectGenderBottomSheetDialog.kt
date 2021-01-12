package com.inventoryorder.ui.createAptOld

import androidx.recyclerview.widget.LinearLayoutManager
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel
import com.inventoryorder.R
import com.inventoryorder.databinding.BottomSheetSelectGenderBinding
import com.inventoryorder.model.bottomsheet.GenderSelectionModel
import com.inventoryorder.recyclerView.AppBaseRecyclerViewAdapter
import com.inventoryorder.recyclerView.BaseRecyclerViewItem
import com.inventoryorder.recyclerView.RecyclerItemClickListener

class SelectGenderBottomSheetDialog : BaseBottomSheetDialog<BottomSheetSelectGenderBinding, BaseViewModel>(), RecyclerItemClickListener {

  private var list = ArrayList<GenderSelectionModel>()
  private var adapter: AppBaseRecyclerViewAdapter<GenderSelectionModel>? = null
  var onDoneClicked: (genderSelectionModel: GenderSelectionModel?) -> Unit = {}

  override fun getLayout(): Int {
    return R.layout.bottom_sheet_select_gender
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  fun setList(list: ArrayList<GenderSelectionModel>) {
    this.list.clear()
    this.list.addAll(list)
  }

  override fun onCreateView() {
    binding?.recyclerViewBottomSheetSelectGender?.post {
      adapter = AppBaseRecyclerViewAdapter(baseActivity, list, this)
      binding?.recyclerViewBottomSheetSelectGender?.layoutManager = LinearLayoutManager(baseActivity)
      binding?.recyclerViewBottomSheetSelectGender?.adapter = adapter
      binding?.recyclerViewBottomSheetSelectGender?.let { adapter?.runLayoutAnimation(it) }
    }
  }

  override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
    val selectGenderModel = item as GenderSelectionModel
    list.forEach { it.isSelected = (it.genderType == selectGenderModel.genderType) }
    adapter?.notifyDataSetChanged()
    onDoneClicked(selectGenderModel)
    dismiss()
  }

}