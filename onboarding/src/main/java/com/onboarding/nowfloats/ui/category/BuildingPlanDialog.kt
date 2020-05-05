package com.onboarding.nowfloats.ui.category

import android.os.CountDownTimer
import com.framework.base.BaseDialogFragment
import com.framework.models.BaseViewModel
import com.onboarding.nowfloats.R
import com.onboarding.nowfloats.databinding.DialogBuildingPlanBinding

class BuildingPlanDialog : BaseDialogFragment<DialogBuildingPlanBinding, BaseViewModel>() {

  var onClicked: () -> Unit = { }
  var progress = 0F
  override fun getLayout(): Int {
    return R.layout.dialog_building_plan
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun getTheme(): Int {
    return R.style.MaterialDialogThemeFull
  }

  override fun onViewCreated() {
    binding?.blurView?.setBlur(4F)
    isCancelable = false
    binding?.progressBar?.progress = 0F
    binding?.progressBar?.progressMax = 100F

    object : CountDownTimer(4000, 1000) {
      override fun onTick(millisUntilFinished: Long) {
        binding?.progressBar?.post {
          progress = (progress + 25F)
          binding?.progressBar?.progress = progress
        }
      }

      override fun onFinish() {
        onClicked()
        dismiss()
      }
    }.start()
  }

}