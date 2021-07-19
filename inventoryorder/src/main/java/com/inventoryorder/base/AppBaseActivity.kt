package com.inventoryorder.base

import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.ViewDataBinding
import com.framework.base.BaseActivity
import com.framework.models.BaseViewModel
import com.inventoryorder.R


abstract class AppBaseActivity<Binding : ViewDataBinding, ViewModel : BaseViewModel> :
  BaseActivity<Binding, ViewModel>() {


  override fun onCreate(savedInstanceState: Bundle?) {
    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    super.onCreate(savedInstanceState)
  }

  override fun onCreateView() {

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

  override fun onBackPressed() {
    super.onBackPressed()
    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
  }
}
