package com.onboarding.nowfloats.ui

import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import com.framework.base.BaseDialogFragment
import com.framework.models.BaseViewModel
import com.framework.utils.NetworkUtils
import com.framework.views.blur.RenderScriptBlur
import com.onboarding.nowfloats.R
import com.onboarding.nowfloats.databinding.DialogInternetErrorBinding

class InternetErrorDialog : BaseDialogFragment<DialogInternetErrorBinding, BaseViewModel>() {

    var onRetryTapped = {}

    override fun getLayout(): Int {
        return R.layout.dialog_internet_error
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun getTheme(): Int {
        return R.style.MaterialDialogThemeFull
    }

    override fun onViewCreated() {
        setBlur()
        isCancelable = false
        binding?.retryBtn?.setOnClickListener {
            if (NetworkUtils.isNetworkConnected()) {
                onRetryTapped()
                dismiss()
            }
        }
    }

    private fun setBlur() {
        val decorView: View? = activity?.window?.decorView
        val rootView: ViewGroup = decorView?.findViewById(android.R.id.content) as ViewGroup
        val windowBackground: Drawable = decorView.background
        binding?.blurView?.setupWith(rootView)?.setFrameClearDrawable(windowBackground)
                ?.setBlurAlgorithm(RenderScriptBlur(activity))?.setBlurRadius(4f)?.setHasFixedTransformationMatrix(true)
    }

}