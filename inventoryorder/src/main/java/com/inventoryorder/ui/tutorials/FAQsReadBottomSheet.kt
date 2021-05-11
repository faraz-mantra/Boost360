package com.inventoryorder.ui.tutorials

import android.view.View
import com.framework.base.BaseBottomSheetDialog
import com.framework.extensions.observeOnce
import com.inventoryorder.R
import com.inventoryorder.constant.IntentConstant
import com.inventoryorder.databinding.BottomSheetFaqAppointmentBinding
import com.inventoryorder.recyclerView.AppBaseRecyclerViewAdapter
import com.inventoryorder.ui.tutorials.model.product.Faq
import com.inventoryorder.ui.tutorials.model.product.TutorialsDataResponse
import com.inventoryorder.ui.tutorials.viewmodel.TutorialViewModel

class FAQsReadBottomSheet : BaseBottomSheetDialog<BottomSheetFaqAppointmentBinding, TutorialViewModel>() {

    private var faqs: java.util.ArrayList<Faq>? = null

    override fun getLayout(): Int {
        return R.layout.bottom_sheet_faq_appointment
    }

    override fun getViewModelClass(): Class<TutorialViewModel> {
        return TutorialViewModel::class.java
    }

    override fun onCreateView() {
        setOnClickListener(binding?.civClose, binding?.civBack)
        if (getBundle()) {
            getFaqsStaff()
        } else {
            getFaqs()
        }
    }

    private fun getFaqs() {
        val adapter = AppBaseRecyclerViewAdapter(baseActivity, faqs!!)
        binding?.rvFaq?.adapter = adapter
    }

    private fun getFaqsStaff() {
        viewModel?.getFaqStaffResponse()?.observeOnce(viewLifecycleOwner, {
            val adapter = AppBaseRecyclerViewAdapter(baseActivity, it.contents!!)
            binding?.rvFaq?.adapter = adapter

        })
    }

    private fun getBundle(): Boolean {
        this.faqs = arguments?.getSerializable(IntentConstant.DATA_FAQS.name) as? ArrayList<Faq>
        return !faqs.isNullOrEmpty()
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when (v) {
            binding?.civClose -> dismiss()
            binding?.civBack -> {
                dismiss()
                val learnHowItWorkBottomSheet = LearnHowItWorkBottomSheet()
                learnHowItWorkBottomSheet.show(parentFragmentManager, LearnHowItWorkBottomSheet::class.java.name)
            }
        }
    }
}


