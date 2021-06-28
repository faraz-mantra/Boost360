package com.inventoryorder.ui.tutorials

import android.view.View
import com.framework.base.BaseBottomSheetDialog
import com.framework.extensions.observeOnce
import com.inventoryorder.R
import com.inventoryorder.databinding.BottomSheetFaqAppointmentBinding
import com.inventoryorder.recyclerView.AppBaseRecyclerViewAdapter
import com.inventoryorder.ui.tutorials.viewmodel.TutorialViewModel

class FAQsReadBottomSheet : BaseBottomSheetDialog<BottomSheetFaqAppointmentBinding, TutorialViewModel>() {

  override fun getLayout(): Int {
    return R.layout.bottom_sheet_faq_appointment
  }

  override fun getViewModelClass(): Class<TutorialViewModel> {
    return TutorialViewModel::class.java
  }

  override fun onCreateView() {
    setOnClickListener(binding?.civClose, binding?.civBack)
    viewModel?.getFaqStaffResponse()?.observeOnce(viewLifecycleOwner, {
      val adapter = AppBaseRecyclerViewAdapter(baseActivity, it.contents!!)
      binding?.rvFaq?.adapter = adapter

    })
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.civClose -> dismiss()
      binding?.civBack -> {
        dismiss()
        val learnAboutAppointmentMgmtBottomSheet = LearnHowItWorkBottomSheet()
        learnAboutAppointmentMgmtBottomSheet.show(parentFragmentManager, LearnHowItWorkBottomSheet::class.java.name)
      }
    }
  }
}


