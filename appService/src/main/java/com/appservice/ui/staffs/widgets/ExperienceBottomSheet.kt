package com.appservice.ui.staffs.widgets

import android.view.View
import com.appservice.R
import com.appservice.databinding.BottomSheetExperienceBinding
import com.appservice.recyclerView.AppBaseRecyclerViewAdapter
import com.appservice.recyclerView.BaseRecyclerViewItem
import com.appservice.recyclerView.RecyclerItemClickListener
import com.appservice.model.staffModel.ExperienceModel
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel
import java.util.*

const val EXPERIENCE_VALUE = "EXPERIENCE_VALUE"

class ExperienceBottomSheet : BaseBottomSheetDialog<BottomSheetExperienceBinding, BaseViewModel>(), RecyclerItemClickListener {

  private var value: Int? = null
  private lateinit var experienceData: ArrayList<ExperienceModel>
  private lateinit var adapterN: AppBaseRecyclerViewAdapter<ExperienceModel>
  var onClicked: (value: Int?) -> Unit = { }
  override fun getLayout(): Int {
    return R.layout.bottom_sheet_experience
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun onCreateView() {
    value = arguments?.getString(EXPERIENCE_VALUE)?.toIntOrNull()
    setOnClickListener(binding?.btnCancel, binding?.btnDone)
    this.experienceData = ExperienceModel().experienceData()
    this.experienceData.map { it.isSelected = (value != null && it.value == value) }
    binding?.rvExperience?.apply {
      adapterN = AppBaseRecyclerViewAdapter(baseActivity, experienceData, this@ExperienceBottomSheet)
      adapter = adapterN
    }
  }

  override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
    this.value = (item as ExperienceModel).value
    experienceData.forEach { if (it != item) it.isSelected = false }
    adapterN.notifyDataSetChanged()
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.btnDone -> {
        if (value == null) {
          showShortToast("Please select years of experience!")
          return
        }
        dismiss()
        onClicked(value)
      }
      binding?.btnCancel -> dismiss()
    }
  }
}