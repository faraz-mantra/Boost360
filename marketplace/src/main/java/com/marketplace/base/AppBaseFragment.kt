package com.marketplace.base

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.framework.base.BaseFragment
import com.framework.base.BaseResponse
import com.framework.exceptions.NoNetworkException
import com.framework.extensions.gone
import com.framework.extensions.observeOnce
import com.framework.models.BaseViewModel
import com.framework.pref.UserSessionManager
import com.marketplace.R

abstract class AppBaseFragment<Binding : ViewDataBinding, ViewModel : BaseViewModel> :
    BaseFragment<Binding, ViewModel>() {

    protected var session:UserSessionManager? = null

    protected var appBaseActivity: AppBaseActivity<*, *>? = null
    private var progressView: ProgressDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        appBaseActivity = activity as? AppBaseActivity<*, *>
        progressView = ProgressDialog.newInstance()
        session = appBaseActivity?.let { UserSessionManager(it) }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onCreateView() {

    }

    protected fun getToolbarTitle(): String? {
        return appBaseActivity?.getToolbar()?.getTitleTextView()?.text?.toString()
    }

    protected fun hideToolBar() {
        appBaseActivity?.getToolbar()?.gone()
    }

    protected open fun hideProgress() {
        progressView?.hideProgress()
    }

    protected open fun showProgress(
        title: String? = "Please wait...",
        cancelable: Boolean? = false
    ) {
        title?.let { progressView?.setTitle(it) }
        cancelable?.let { progressView?.isCancelable = it }
        activity?.let { progressView?.showProgress(it.supportFragmentManager) }
    }

    protected fun isResponseSuccessful(it: BaseResponse, errorMessage: String?): Boolean {
        if ((it.error is NoNetworkException).not()) {
            if ((it.isSuccess())) {
                return true;
            } else {
                Log.d("API_ERROR", it.message())
                Log.d("BaseResponseData", it.message())
                showErrorMessage(errorMessage)
            }
        } else {
            showErrorMessage("No Internet Connection")
        }
        return false;
    }

    protected fun hitApi(liveData: LiveData<BaseResponse>?, errorStringId: Int) {
        liveData?.observeOnce(viewLifecycleOwner, Observer {
            if (isResponseSuccessful(it, resources.getString(errorStringId))) {
                onSuccess(it)
            } else {
                onFailure(it)
            }
        })
    }

    open fun onSuccess(it: BaseResponse) {
        Log.d("TAG", "onSuccess");
    }

    open fun onFailure(it: BaseResponse) {

    }

    private fun showErrorMessage(string: String?) {
        hideProgress()
        showLongToast(string)
    }
}
