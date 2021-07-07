package com.onboarding.nowfloats.ui.updateChannel

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.framework.base.BaseFragment
import com.framework.base.FRAGMENT_TYPE
import com.framework.databinding.ActivityFragmentContainerBinding
import com.framework.exceptions.IllegalFragmentTypeException
import com.framework.models.BaseViewModel
import com.framework.utils.ConversionUtils
import com.framework.views.customViews.CustomToolbar
import com.onboarding.nowfloats.R
import com.onboarding.nowfloats.base.AppBaseActivity
import com.onboarding.nowfloats.constant.FragmentType
import com.onboarding.nowfloats.constant.PreferenceConstant
import com.onboarding.nowfloats.ui.updateChannel.digitalChannel.MyDigitalChannelFragment

class ContainerDigitalChannelActivity :
  AppBaseActivity<ActivityFragmentContainerBinding, BaseViewModel>() {

  private var isStartActivity: Boolean? = null

  private var type: String? = null
  private var myDigitalChannelFragment: MyDigitalChannelFragment? = null

  override fun getLayout(): Int {
    return com.framework.R.layout.activity_fragment_container
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    if (type == null) intent?.extras?.getString(FRAGMENT_TYPE)?.let { type = it }
    super.onCreate(savedInstanceState)
  }

  override fun onCreateView() {
    super.onCreateView()
    isStartActivity = intent?.extras?.getBoolean(PreferenceConstant.IS_START_ACTIVITY) ?: false
    setFragment()
  }

  override fun getToolbar(): CustomToolbar? {
    return binding?.appBarLayout?.toolbar
  }

  override fun getToolbarBackgroundColor(): Int? {
    return when (type) {
      FragmentType.MY_DIGITAL_CHANNEL.name -> ContextCompat.getColor(this, R.color.bg_dark_grey)
      else -> super.getToolbarBackgroundColor()
    }
  }

  override fun getNavigationIcon(): Drawable? {
    return when (type) {
      FragmentType.MY_DIGITAL_CHANNEL.name -> ContextCompat.getDrawable(
        this,
        R.drawable.ic_round_arrow_white_n
      )
      else -> super.getNavigationIcon()
    }
  }

  override fun getToolbarTitleSize(): Float? {
    return when (type) {
      FragmentType.MY_DIGITAL_CHANNEL.name -> ConversionUtils.dp2px(16f).toFloat()
      else -> return super.getToolbarTitleSize()
    }
  }

  override fun getToolbarTitleColor(): Int? {
    return when (type) {
      FragmentType.MY_DIGITAL_CHANNEL.name -> ContextCompat.getColor(this, R.color.white)
      else -> super.getToolbarTitleColor()
    }
  }

  override fun getToolbarTitleGravity(): Int {
    return when (type) {
      FragmentType.MY_DIGITAL_CHANNEL.name -> Gravity.CENTER
      else -> super.getToolbarTitleGravity()
    }
  }

  override fun getToolbarTitle(): String? {
    return when (type) {
      FragmentType.MY_DIGITAL_CHANNEL.name -> getString(R.string.my_digital_channels)
      else -> super.getToolbarTitle()
    }
  }

  private fun setFragment() {
    val fragment = getFragmentInstance(type)
    fragment?.arguments = intent.extras
    binding?.container?.id?.let { addFragmentReplace(it, fragment, shouldAddToBackStack()) }
  }

  private fun getFragmentInstance(type: String?): BaseFragment<*, *>? {
    return when (type) {
      FragmentType.MY_DIGITAL_CHANNEL.name -> {
        myDigitalChannelFragment = MyDigitalChannelFragment.newInstance()
        myDigitalChannelFragment
      }
      else -> throw IllegalFragmentTypeException()
    }
  }

  private fun shouldAddToBackStack(): Boolean {
    return when (type) {
      else -> false
    }
  }

  override fun onBackPressed() {
    if (isStartActivity == true) {
      try {
        val intent = Intent(this, Class.forName("com.dashboard.controller.DashboardActivity"))
        startActivity(intent)
        finish()
      } catch (e: Exception) {
        super.onBackPressed()
      }
    } else super.onBackPressed()
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    myDigitalChannelFragment?.onActivityResult(requestCode, resultCode, data)
  }

  fun changeTheme(color: Int) {
    getToolbar()?.setBackgroundColor(ContextCompat.getColor(this, color))
    window?.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      window?.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
      window?.statusBarColor = ContextCompat.getColor(this, color)
    }
  }
}

fun Fragment.startFragmentChannelActivity(
  type: FragmentType,
  bundle: Bundle = Bundle(),
  clearTop: Boolean = false
) {
  val intent = Intent(activity, ContainerDigitalChannelActivity::class.java)
  intent.putExtras(bundle)
  intent.setFragmentType(type)
  if (clearTop) intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
  startActivity(intent)
}

fun AppCompatActivity.startFragmentChannelActivity(
  type: FragmentType,
  bundle: Bundle = Bundle(),
  clearTop: Boolean = false
) {
  val intent = Intent(this, ContainerDigitalChannelActivity::class.java)
  intent.putExtras(bundle)
  intent.setFragmentType(type)
  if (clearTop) intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
  startActivity(intent)
}