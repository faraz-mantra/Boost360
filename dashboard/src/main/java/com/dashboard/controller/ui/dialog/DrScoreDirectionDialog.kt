package com.dashboard.controller.ui.dialog

import android.util.Log
import androidx.fragment.app.FragmentManager
import com.dashboard.R
import com.dashboard.base.ProgressDialog
import com.dashboard.databinding.DialogDrScoreDirectionBinding
import com.dashboard.databinding.DialogWelcomeHomeBinding
import com.dashboard.model.live.welcomeData.WelcomeData
import com.dashboard.model.live.welcomeData.saveWelcomeData
import com.framework.base.BaseDialogFragment
import com.framework.models.BaseViewModel

class DrScoreDirectionDialog : BaseDialogFragment<DialogDrScoreDirectionBinding, BaseViewModel>() {

  var onClicked: () -> Unit = { }

  companion object {
    @JvmStatic
    fun newInstance(): DrScoreDirectionDialog {
      return DrScoreDirectionDialog()
    }
  }

  override fun getTheme(): Int {
    return R.style.MaterialDialogThemeFullN
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun getLayout(): Int {
    return R.layout.dialog_dr_score_direction
  }

  override fun onCreateView() {
    isCancelable = false
    binding?.btnNext?.setOnClickListener {
      hideDialog()
      onClicked()
    }
  }

  fun showDialog(manager: FragmentManager) {
    try {
      if (this.isVisible.not()) show(manager, DrScoreDirectionDialog::class.java.simpleName)
    } catch (e: Exception) {
      Log.e(DrScoreDirectionDialog::class.java.name, e.localizedMessage ?: "")
    }
  }

  fun hideDialog() {
    try {
      if (isRemoving.not()) dismiss()
    } catch (e: Exception) {
      Log.e(DrScoreDirectionDialog::class.java.name, e.localizedMessage ?: "")
    }
  }
}