package com.dashboard.controller.ui.websiteTheme.bottomsheet

import android.os.Bundle
import com.dashboard.R
import com.dashboard.controller.ui.dashboard.getLocalSession
import com.dashboard.databinding.BsheetWebsiteRepublishingProgressBinding
import com.dashboard.viewmodel.RepublishWebsiteViewModel
import com.framework.base.BaseBottomSheetDialog
import com.framework.extensions.observeOnce
import com.framework.models.BaseViewModel
import com.framework.pref.UserSessionManager
import java.util.*
import kotlin.concurrent.schedule

enum class SuccessType {
    ON_PROGRESS_COMPLETE
}

class RepublishProgressBottomSheet : BaseBottomSheetDialog<BsheetWebsiteRepublishingProgressBinding, RepublishWebsiteViewModel>() {

    var onRepublishSuccess: (value: String) -> Unit = { }
    companion object {
        @JvmStatic
        fun newInstance(): RepublishProgressBottomSheet {
            val bundle = Bundle().apply {}
            val fragment = RepublishProgressBottomSheet()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getLayout(): Int {
        return R.layout.bsheet_website_republishing_progress
    }

    override fun getViewModelClass(): Class<RepublishWebsiteViewModel> {
        return RepublishWebsiteViewModel::class.java
    }

    override fun onCreateView() {
        this.isCancelable = false
        republishWebsiteApiCall()
    }

    private fun republishWebsiteApiCall() {
        viewModel?.republishWebsite(fpTag = sessionManager?.fpTag)?.observeOnce(this, {
            if (it.isSuccess()){
                onRepublishSuccess(SuccessType.ON_PROGRESS_COMPLETE.name)
            }else{
                showShortToast(getString(R.string.please_try_again_later))
            }
            dismiss()
        })
    }
}