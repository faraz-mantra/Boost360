package com.appservice.appointment.widgets

import android.text.Html
import android.view.View
import com.appservice.R
import com.appservice.constant.IntentConstant
import com.appservice.databinding.BottomSheetConfirmGstBinding
import com.appservice.viewmodel.AppointmentSettingsViewModel
import com.framework.base.BaseBottomSheetDialog

class BottomSheetConfirmGST : BaseBottomSheetDialog<BottomSheetConfirmGstBinding, AppointmentSettingsViewModel>() {
    override fun getLayout(): Int {
        return R.layout.bottom_sheet_confirm_gst
    }

    override fun getViewModelClass(): Class<AppointmentSettingsViewModel> {
        return AppointmentSettingsViewModel::class.java
    }

    var clickType: (name: ClickType?) -> Unit = { }

    enum class ClickType {
        SAVECHANGES, CANCEL
    }

    override fun onCreateView() {
        setOnClickListener(binding?.btnCancel, binding?.yesItsCorrect)
        val gstint = arguments?.getString(IntentConstant.GSTIN.name)
        val businessName = arguments?.getString(IntentConstant.BUSINESSNAME.name)
        val gstConfirm = "Your GSTIN no. <b>$gstint</b> is registered with the company name <b>$businessName.</b>"
        binding?.ctvConfirmGst?.text = Html.fromHtml(gstConfirm)

    }

    override fun onClick(v: View) {
        super.onClick(v)
        when (v) {
            binding?.btnCancel -> {
                dismiss()
                clickType(ClickType.CANCEL)

            }
            binding?.yesItsCorrect -> {
                dismiss()
                clickType(ClickType.SAVECHANGES)
            }
        }
    }
}