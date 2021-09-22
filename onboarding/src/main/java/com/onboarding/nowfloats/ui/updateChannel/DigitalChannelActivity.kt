package com.onboarding.nowfloats.ui.updateChannel

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.framework.base.BaseFragment
import com.framework.base.FRAGMENT_TYPE
import com.framework.models.BaseViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.onboarding.nowfloats.R
import com.onboarding.nowfloats.base.AppBaseActivity
import com.onboarding.nowfloats.constant.FragmentType
import com.onboarding.nowfloats.constant.PreferenceConstant
import com.onboarding.nowfloats.databinding.ActivityFragmentContainerUpdateBinding
import com.onboarding.nowfloats.ui.updateChannel.digitalChannel.MyDigitalChannelFragment
import com.onboarding.nowfloats.ui.updateChannel.digitalChannel.MyVisitingCardFragment

open class DigitalChannelActivity : AppBaseActivity<ActivityFragmentContainerUpdateBinding, BaseViewModel>() {

  private var isStartActivity: Boolean? = null

  override fun getLayout(): Int {
    return R.layout.activity_fragment_container_update
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun onCreateView() {
    super.onCreateView()
    binding?.backBtn?.setOnClickListener { onBackPressed() }
    setFragment()
  }


  override fun getToolbarBackgroundColor(): Int? {
    return ContextCompat.getColor(this, R.color.bg_dark_grey)
  }

  private fun setFragment() {
    val bundle = intent.extras
    isStartActivity = bundle?.getBoolean(PreferenceConstant.IS_START_ACTIVITY) ?: false
    val fragments = getFragments(bundle)
    val titleList = arrayListOf("Channels", "Visiting card")
    binding?.container?.apply {
      isUserInputEnabled = false
      val adapterInfo = FragmentAdapter(fragments, this@DigitalChannelActivity)
      adapter = adapterInfo
      val tabLayout = binding?.tabMode ?: return@apply
      TabLayoutMediator(tabLayout, this) { tab: TabLayout.Tab, position: Int ->
        tab.text = titleList[position]
        requestLayout()
      }.attach()
    }
  }

  private fun getFragments(bundle: Bundle?): ArrayList<BaseFragment<*, *>> {
    return arrayListOf(
      MyDigitalChannelFragment.newInstance(bundle),
      MyVisitingCardFragment.newInstance(bundle)
    )
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

  fun changeTheme(color: Int) {
    binding?.tabToolbar?.setBackgroundColor(ContextCompat.getColor(this, color))
    window?.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      window?.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
      window?.statusBarColor = ContextCompat.getColor(this, color)
    }
  }
}

fun Fragment.startFragmentActivity(
  type: FragmentType,
  bundle: Bundle = Bundle(),
  clearTop: Boolean = false,
  isResult: Boolean = false
) {
  val intent = Intent(activity, DigitalChannelActivity::class.java)
  intent.putExtras(bundle)
  intent.setFragmentType(type)
  if (clearTop) intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
  if (isResult.not()) startActivity(intent) else startActivityForResult(intent, 101)
}

fun startFragmentActivityNew(
  activity: Activity,
  type: FragmentType,
  bundle: Bundle = Bundle(),
  clearTop: Boolean
) {
  val intent = Intent(activity, DigitalChannelActivity::class.java)
  intent.putExtras(bundle)
  intent.setFragmentType(type)
  if (clearTop) intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
  activity.startActivity(intent)
}

fun AppCompatActivity.startFragmentActivity(
  type: FragmentType,
  bundle: Bundle = Bundle(),
  clearTop: Boolean = false
) {
  val intent = Intent(this, DigitalChannelActivity::class.java)
  intent.putExtras(bundle)
  intent.setFragmentType(type)
  if (clearTop) intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
  startActivity(intent)
}

fun Intent.setFragmentType(type: FragmentType): Intent {
  return this.putExtra(FRAGMENT_TYPE, type.name)
}
