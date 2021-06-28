package com.dashboard.controller.ui.dialog

import androidx.fragment.app.FragmentManager
import com.dashboard.R
import com.dashboard.databinding.DialogDrWelcomeBinding
import com.framework.base.BaseDialogFragment
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.framework.models.BaseViewModel
import com.framework.utils.ConversionUtils
import com.framework.utils.ScreenUtils
import com.onboarding.nowfloats.extensions.fadeIn
import com.onboarding.nowfloats.extensions.tick

class DrScoreWelcomeDialog : BaseDialogFragment<DialogDrWelcomeBinding, BaseViewModel>() {

  var onClicked: () -> Unit = { }

  companion object {
    @JvmStatic
    fun newInstance(): DrScoreWelcomeDialog {
      return DrScoreWelcomeDialog()
    }
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun getLayout(): Int {
    return R.layout.dialog_dr_welcome
  }

  override fun getTheme(): Int {
    return R.style.MaterialDialogTheme
  }

  override fun onCreateView() {
    isCancelable = false
    binding?.root?.post {
      binding?.title?.fadeIn(0)?.mergeWith(binding?.lottyProgress?.fadeIn(10))
          ?.andThen(tick(2))?.doOnComplete {
            binding?.lottyProgress?.post {
              binding?.lottyProgress?.gone()
              binding?.lottyProgress?.cancelAnimation()
              binding?.drScoreWelcomeAnim?.visible()
              binding?.drScoreWelcomeAnim?.playAnimation()
            }
          }?.andThen(binding?.drScoreWelcomeAnim?.fadeIn(50))
          ?.andThen(tick(4))?.doOnComplete {
            binding?.drScoreWelcomeAnim?.post {
              binding?.title?.text = getString(R.string.ready_welcome_new_customers)
              binding?.drScoreWelcomeAnim?.gone()
              binding?.drScoreWelcomeAnim?.cancelAnimation()
              binding?.imageIcon?.visible()
            }
          }?.andThen(binding?.imageIcon?.fadeIn(50))?.doOnComplete {
            binding?.desc?.visible()
          }
          ?.andThen(binding?.desc?.fadeIn(50))?.doOnComplete {
            binding?.btnDone?.visible()
          }
          ?.andThen(binding?.btnDone?.fadeIn(50))?.subscribe()
    }
    binding?.btnDone?.setOnClickListener {
      hideDialog()
      onClicked()
    }
  }

  override fun getWidth(): Int {
    return ScreenUtils.instance.getWidth(activity) - ConversionUtils.dp2px(42f)
  }

  fun showDialog(manager: FragmentManager) {
    try {
      if (this.isVisible.not()) show(manager, DrScoreWelcomeDialog::class.java.name)
    } catch (e: IllegalStateException) {
      e.printStackTrace()
    }
  }

  fun hideDialog() {
    try {
      if (isRemoving.not()) dismiss()
    } catch (e: IllegalStateException) {
      e.printStackTrace()
    }
  }
}