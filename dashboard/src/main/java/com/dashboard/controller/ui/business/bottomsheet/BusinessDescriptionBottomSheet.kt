package com.dashboard.controller.ui.business.bottomsheet

import android.view.View
import com.dashboard.R
import com.dashboard.constant.IntentConstant
import com.dashboard.controller.ui.business.model.BusinessProfileModel
import com.dashboard.databinding.BottomSheetBusinessDescBinding
import com.framework.base.BaseBottomSheetDialog
import com.framework.extensions.afterTextChanged
import com.framework.models.BaseViewModel
import com.framework.utils.showKeyBoard

class BusinessDescriptionBottomSheet : BaseBottomSheetDialog<BottomSheetBusinessDescBinding, BaseViewModel>() {

    private var businessProfileModel: BusinessProfileModel? = null

    override fun getLayout(): Int {
        return R.layout.bottom_sheet_business_desc
    }
    var onClicked: (businessModel: BusinessProfileModel) -> Unit = { }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {
        dialog.behavior.isDraggable = false
        binding?.btnSaveDesc?.isEnabled = false
        setOnClickListener(binding?.rivCloseBottomSheet, binding?.btnSaveDesc)
        this.businessProfileModel = arguments?.get(IntentConstant.BUSINESS_DETAILS.name) as? BusinessProfileModel
        binding?.cetBusinessDesc?.setText(businessProfileModel?.businessDesc)
        binding?.cetBusinessDesc?.setSelection(businessProfileModel?.businessDesc?.length?:0)
        binding?.cetBusinessDesc?.afterTextChanged { binding?.btnSaveDesc?.isEnabled = binding?.cetBusinessDesc?.text?.trim()?.length ?: 0 > 0 && businessProfileModel?.businessDesc?.trim()!=it.trim()}
        baseActivity.showKeyBoard(binding?.cetBusinessDesc)
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when (v) {
            binding?.btnSaveDesc -> {
                val desc = binding?.cetBusinessDesc?.text.toString()
                businessProfileModel?.businessDesc = desc
                onClicked(businessProfileModel!!)
                dismiss()
                //send data to the fragment
            }
            binding?.rivCloseBottomSheet -> {
                dismiss()
            }
        }
    }
}