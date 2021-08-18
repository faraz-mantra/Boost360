package com.dashboard.controller.ui.dialog

import android.util.Log
import androidx.fragment.app.FragmentManager
import com.dashboard.R
import com.dashboard.base.ProgressDialog
import com.dashboard.databinding.DialogWelcomeHomeBinding
import com.dashboard.model.live.welcomeData.WelcomeData
import com.dashboard.model.live.welcomeData.saveWelcomeData
import com.framework.base.BaseDialogFragment
import com.framework.models.BaseViewModel

class WelcomeHomeDialog : BaseDialogFragment<DialogWelcomeHomeBinding, BaseViewModel>() {

  private var welcomeData: WelcomeData? = null
  var onClicked: (type: String?) -> Unit = { }

  companion object {
    @JvmStatic
    fun newInstance(): WelcomeHomeDialog {
      return WelcomeHomeDialog()
    }
  }

  fun setData(welcomeData: WelcomeData) {
    this.welcomeData = welcomeData
  }

  override fun getTheme(): Int {
    return R.style.MaterialThemeSignInFull
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun getLayout(): Int {
    return R.layout.dialog_welcome_home
  }

  override fun onCreateView() {
    isCancelable = false
    if (welcomeData != null) {
      binding?.title?.text = welcomeData!!.title
      binding?.desc?.text = welcomeData!!.desc
      binding?.btnManage?.text = welcomeData!!.btnTitle
      val type = WelcomeData.WelcomeType.fromName(welcomeData!!.welcomeType)
      type?.icon?.let { binding?.image?.setImageResource(it) }
      binding?.btnManage?.setOnClickListener {
        onClicked(type?.name)
        hideProgress()
      }
      welcomeData!!.welcomeType?.let { saveWelcomeData(it, true) }
    } else hideProgress()
    binding?.closeBtn?.setOnClickListener { dismiss() }
  }

  fun showProgress(manager: FragmentManager) {
    try {
      if (this.isVisible.not()) show(manager, ProgressDialog::class.java.simpleName)
    } catch (e: Exception) {
      Log.e(ProgressDialog::class.java.name, e.localizedMessage ?: "")
    }
  }

  fun hideProgress() {
    try {
      if (isRemoving.not()) dismiss()
    } catch (e: Exception) {
      Log.e(ProgressDialog::class.java.name, e.localizedMessage ?: "")
    }
  }
}