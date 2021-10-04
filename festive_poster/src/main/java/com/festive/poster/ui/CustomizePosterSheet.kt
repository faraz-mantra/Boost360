package com.festive.poster.ui

import android.view.View
import com.festive.poster.R
import com.festive.poster.databinding.BsheetCustomizePosterBinding
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel

class CustomizePosterSheet: BaseBottomSheetDialog<BsheetCustomizePosterBinding, BaseViewModel>() {
    override fun getLayout(): Int {
        return R.layout.bsheet_customize_poster
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {
        setOnClickListener(binding?.ivCancel)
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when(v){
            binding?.ivCancel->{
                dismiss()
            }
        }
    }
}