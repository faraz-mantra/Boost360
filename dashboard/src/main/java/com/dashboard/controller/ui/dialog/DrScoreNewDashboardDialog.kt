package com.dashboard.controller.ui.dialog

import android.util.Log
import androidx.fragment.app.FragmentManager
import com.dashboard.R
import com.dashboard.base.ProgressDialog
import com.dashboard.databinding.DialogDrScoreDashboardBinding
import com.dashboard.databinding.DialogDrScoreDirectionBinding
import com.framework.base.BaseDialogFragment
import com.framework.models.BaseViewModel

class DrScoreNewDashboardDialog :
  BaseDialogFragment<DialogDrScoreDashboardBinding, BaseViewModel>() {

  var onClicked: () -> Unit = { }

  companion object {
    @JvmStatic
    fun newInstance(): DrScoreNewDashboardDialog {
      return DrScoreNewDashboardDialog()
    }
  }


  override fun getTheme(): Int {
    return R.style.MaterialDialogThemeFullN
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun getLayout(): Int {
    return R.layout.dialog_dr_score_dashboard
  }

  override fun onCreateView() {
    isCancelable = false
    binding?.btnNewDashboard?.setOnClickListener {
      hideDialog()
      onClicked()
    }
  }

  fun showDialog(manager: FragmentManager) {
    try {
      if (this.isVisible.not()) show(manager, DrScoreNewDashboardDialog::class.java.simpleName)
    } catch (e: Exception) {
      Log.e(DrScoreNewDashboardDialog::class.java.name, e.localizedMessage ?: "")
    }
  }

  fun hideDialog() {
    try {
      if (isRemoving.not()) dismiss()
    } catch (e: Exception) {
      Log.e(DrScoreNewDashboardDialog::class.java.name, e.localizedMessage ?: "")
    }
  }
}