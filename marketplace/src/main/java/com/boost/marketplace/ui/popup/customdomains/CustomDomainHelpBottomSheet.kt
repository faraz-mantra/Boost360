package com.boost.marketplace.ui.popup.customdomains

import android.content.Intent
import android.net.Uri
import com.boost.dbcenterapi.utils.SharedPrefs
import com.boost.marketplace.R
import com.boost.marketplace.databinding.PopupCallExpertCustomDomainBinding
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel

class CustomDomainHelpBottomSheet : BaseBottomSheetDialog<PopupCallExpertCustomDomainBinding, BaseViewModel>() {

    lateinit var prefs: SharedPrefs

    override fun getLayout(): Int {
        return R.layout.popup_call_expert_custom_domain
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {

        prefs = SharedPrefs(requireActivity())

        binding?.backBtn?.setOnClickListener {
            dismiss()
        }

        binding?.tvCallExpert?.setOnClickListener {
            val callIntent = Intent(Intent.ACTION_DIAL)
            callIntent.data = Uri.parse("tel:" + prefs.getExpertContact())
            startActivity(Intent.createChooser(callIntent, "Call by:"))
        }
    }

}