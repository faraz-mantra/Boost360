package com.dashboard.base

import android.content.DialogInterface
import android.os.Bundle
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.util.Linkify
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import androidx.databinding.ViewDataBinding
import com.boost.presignin.R
import com.framework.base.BaseFragment
import com.framework.models.BaseViewModel

abstract class AppBaseFragment<Binding : ViewDataBinding, ViewModel : BaseViewModel> : BaseFragment<Binding, ViewModel>() {

  private var progressView: ProgressDialog? = null

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    progressView = ProgressDialog.newInstance()
    return super.onCreateView(inflater, container, savedInstanceState)
  }

  override fun onCreateView() {
  }

  protected open fun hideProgress() {
    progressView?.hideProgress()
  }

  protected open fun showProgress(title: String? = "Please wait...", cancelable: Boolean? = false) {
    title?.let { progressView?.setTitle(it) }
    cancelable?.let { progressView?.isCancelable = it }
    activity?.let { progressView?.showProgress(it.supportFragmentManager) }
  }
  protected fun needHelp() {
    val s = SpannableString(resources.getString(R.string.need_help_desc))
    Linkify.addLinks(s, Linkify.ALL)
    val alertDialog = AlertDialog.Builder(ContextThemeWrapper(baseActivity, R.style.CustomAlertDialogTheme))
    alertDialog.setTitle(getString(R.string.need_help_title)).setMessage(s).setPositiveButton(resources.getString(R.string.okay), null)
    val alert = alertDialog.create()
    alert.show()
    alert.findViewById<TextView>(android.R.id.message)?.movementMethod = LinkMovementMethod.getInstance()
    alert.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getColor(R.color.colorAccentLight))
  }
}
