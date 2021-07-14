package com.appservice.base

import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.ViewDataBinding
import com.appservice.R
import com.framework.base.BaseActivity
import com.framework.models.BaseViewModel

abstract class AppBaseActivity<Binding : ViewDataBinding, ViewModel : BaseViewModel> : BaseActivity<Binding, ViewModel>() {

  override fun onCreate(savedInstanceState: Bundle?) {
    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    super.onCreate(savedInstanceState)
  }

  override fun onCreateView() {

  }

  override fun getToolbarTitleTypeface(): Typeface? {
    return ResourcesCompat.getFont(this, R.font.semi_bold)
  }

  override fun getToolbarTitleSize(): Float? {
    return resources.getDimension(R.dimen.heading_6)
  }

  override fun getNavIconScale(): Float {
    return 1.0f
  }

  override fun getToolbarBackgroundColor(): Int? {
    return ContextCompat.getColor(this,R.color.colorPrimary)
  }

  override fun getToolbarTitleColor(): Int? {
    return ContextCompat.getColor(this,R.color.white)
  }

  override fun getToolbarTitleGravity(): Int {
    return Gravity.CENTER
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


  fun changeTheme(color: Int, taskBarColor: Int) {
    getToolbar()?.setBackgroundColor(ContextCompat.getColor(this, color))
    window?.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      window?.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
      window?.statusBarColor = ContextCompat.getColor(this, taskBarColor)
    }
  }
}
