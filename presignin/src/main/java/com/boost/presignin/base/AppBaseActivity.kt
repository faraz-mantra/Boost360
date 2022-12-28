package com.boost.presignin.base

import android.content.DialogInterface
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.util.Linkify
import android.view.MenuItem
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.content.ContextCompat
import androidx.databinding.ViewDataBinding
import com.boost.presignin.R
import com.framework.base.BaseActivity
import com.framework.models.BaseViewModel

abstract class AppBaseActivity<Binding : ViewDataBinding, ViewModel : BaseViewModel> : BaseActivity<Binding, ViewModel>() {

  private var progressView: ProgressDialog? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    progressView = ProgressDialog.newInstance()
    super.onCreate(savedInstanceState)
  }

  override fun onCreateView() {
  }


  override fun getToolbarBackgroundColor(): Int? {
    return Color.parseColor("#747474")
  }

  override fun getToolbarTitleColor(): Int? {
    return Color.parseColor("#FFFFFF")
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
      android.R.id.home -> {
        onBackPressed()
        return true
      }
    }
    return super.onOptionsItemSelected(item)
  }

  protected open fun hideProgress() {
    progressView?.hideProgress()
  }

  protected open fun showProgress(title: String? = "Please wait...", cancelable: Boolean? = false) {
    title?.let { progressView?.setTitle(it) }
    cancelable?.let { progressView?.isCancelable = it }
    progressView?.showProgress(supportFragmentManager)
  }

  fun changeTheme(color: Int, taskBarColor: Int) {
    getToolbar()?.setBackgroundColor(ContextCompat.getColor(this, color))
    window?.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      window?.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
      window?.statusBarColor = ContextCompat.getColor(this, taskBarColor)
    }
  }

  protected fun needHelp() {
    val s = SpannableString(resources.getString(R.string.need_help_desc))
    Linkify.addLinks(s, Linkify.ALL)
    val alertDialog = AlertDialog.Builder(ContextThemeWrapper(this, R.style.CustomAlertDialogTheme))
    alertDialog.setTitle(getString(R.string.need_help_title)).setMessage(s)
      .setPositiveButton(resources.getString(R.string.okay), null)
    val alert = alertDialog.create()
    alert.show()
    alert.findViewById<TextView>(android.R.id.message)?.movementMethod =
      LinkMovementMethod.getInstance()
    alert.getButton(DialogInterface.BUTTON_POSITIVE)
      .setTextColor(ContextCompat.getColor(this, R.color.colorAccent))
  }
}
