package com.appservice.appointment

import android.view.View
import com.appservice.R
import com.appservice.databinding.BottomSheetConfirmingChangesBinding
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel

class BottomSheetConfirmingChange: BaseBottomSheetDialog<BottomSheetConfirmingChangesBinding, BaseViewModel>() {
    override fun getLayout(): Int {
        return R.layout.bottom_sheet_confirming_changes
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {
        setOnClickListener(binding?.btnGoBack, binding?.btnYesChange)
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when (v) {
            binding?.btnGoBack -> {
                dismiss()
            }
            binding?.btnYesChange -> {
                dismiss()
                showSuccessfullyUpdated()
            }
        }
    }

    private fun showSuccessfullyUpdated() {
        val bottomSheetCatalogDisplayName = BottomSheetSuccessfullyUpdated()
        bottomSheetCatalogDisplayName.show(this.parentFragmentManager, BottomSheetSuccessfullyUpdated::class.java.name)
    }
}