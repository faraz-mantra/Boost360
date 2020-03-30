package com.onboarding.nowfloats.base

import android.graphics.Typeface
import android.view.Gravity
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.ViewDataBinding
import com.framework.base.BaseActivity
import com.framework.models.BaseViewModel
import com.onboarding.nowfloats.R


abstract class AppBaseActivity<Binding : ViewDataBinding, ViewModel : BaseViewModel> : BaseActivity<Binding, ViewModel>() {

  override fun onCreateView() {
  }

  override fun setTheme() {

  }

  override fun getToolbarTitle(): String? {
    return super.getToolbarTitle()
  }

  override fun getToolbarTitleTypeface(): Typeface? {
    return ResourcesCompat.getFont(this, R.font.semi_bold)
  }

  override fun getToolbarTitleSize(): Float? {
    return resources.getDimension(R.dimen.body_1)
  }

  override fun getNavIconScale(): Float {
    return 0.75f
  }

  override fun getToolbarTitleGravity(): Int {
    return Gravity.CENTER_HORIZONTAL
  }
}
