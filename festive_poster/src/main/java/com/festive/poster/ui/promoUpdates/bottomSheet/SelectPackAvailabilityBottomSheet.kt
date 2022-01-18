package com.festive.poster.ui.promoUpdates.bottomSheet

import android.os.Bundle
import com.festive.poster.R
import com.festive.poster.databinding.BsheetDeleteDraftBinding
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel

class SelectPackAvailabilityBottomSheet :
    BaseBottomSheetDialog<BsheetDeleteDraftBinding, BaseViewModel>() {

    companion object {
        @JvmStatic
        fun newInstance(): SelectPackAvailabilityBottomSheet {
            val bundle = Bundle().apply {}
            val fragment = SelectPackAvailabilityBottomSheet()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getLayout(): Int {
        return R.layout.bsheet_select_pack_validity
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {
        binding?.btnYesDelete?.setOnClickListener {
            dismiss()
        }

        binding?.btnNoStayHere?.setOnClickListener {
            dismiss()
        }
    }
}