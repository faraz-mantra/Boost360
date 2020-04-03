package com.onboarding.nowfloats.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.framework.base.BaseFragment
import com.framework.models.BaseViewModel


abstract class AppBaseFragment<Binding : ViewDataBinding, ViewModel : BaseViewModel> : BaseFragment<Binding, ViewModel>() {


    protected var appBaseActivity: AppBaseActivity<*, *>? = null

    private var progressView: ProgressDialog? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        appBaseActivity = activity as? AppBaseActivity<*, *>
        progressView = ProgressDialog.newInstance()
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onCreateView() {

    }

    protected fun getToolbarTitle(): String? {
        return appBaseActivity?.getToolbar()?.getTitleTextView()?.text?.toString()
    }

    protected open fun hideProgress() {
        progressView?.let { if (it.dialog != null && it.dialog?.isShowing!!) progressView?.hideProgress() }
    }


    protected open fun showProgress() {
        progressView?.let { if (it.isVisible.not()) progressView?.showProgress(activity?.supportFragmentManager) }
    }

    protected open fun showProgress(title: String?, cancelable: Boolean) {
        progressView?.setTitle(title!!)
        progressView?.isCancelable = cancelable
        showProgress()
    }

    protected open fun showProgress(title: String?) {
        progressView?.setTitle(title!!)
        showProgress()
    }
}
