package com.dashboard.controller

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.dashboard.R
import com.dashboard.base.AppBaseActivity
import com.dashboard.constant.FragmentType
import com.dashboard.controller.ui.DemoToDoListFragment
import com.dashboard.controller.ui.allAddOns.AllBoostAddonsFragment
import com.dashboard.controller.ui.business.BusinessProfileFragment
import com.dashboard.controller.ui.customisationnav.CustomisationNavFragment
import com.dashboard.controller.ui.drScore.DigitalReadinessScoreFragment
import com.dashboard.controller.ui.profile.CropProfileImageFragment
import com.dashboard.controller.ui.profile.UserProfileFragment
import com.dashboard.controller.ui.websiteTheme.FragmentWebsiteTheme
import com.framework.base.BaseFragment
import com.framework.base.FRAGMENT_TYPE
import com.framework.databinding.ActivityFragmentContainerBinding
import com.framework.exceptions.IllegalFragmentTypeException
import com.framework.models.BaseViewModel
import com.framework.views.customViews.CustomToolbar

open class DashboardFragmentContainerActivity : AppBaseActivity<ActivityFragmentContainerBinding, BaseViewModel>() {

  private var type: FragmentType? = null
  private var userFragment: UserProfileFragment? = null

  override fun getLayout(): Int {
    return R.layout.activity_fragment_container
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    intent?.extras?.getInt(FRAGMENT_TYPE)?.let { type = FragmentType.values()[it] }
    super.onCreate(savedInstanceState)
  }


  override fun onCreateView() {
    super.onCreateView()
    setFragment()
  }

  override fun getToolbar(): CustomToolbar? {
    return binding?.appBarLayout?.toolbar
  }


  override fun customTheme(): Int? {
    return when (type) {
      FragmentType.DIGITAL_READINESS_SCORE -> R.style.DashboardThemeNew
      FragmentType.FRAGMENT_USER_PROFILE, FragmentType.FRAGMENT_USER_PROFILE_IMAGE_CROP -> R.style.DashboardThemeNew
      FragmentType.FRAGMENT_BUSINESS_PROFILE -> R.style.BusinessProfileTheme
      FragmentType.FRAGMENT_WEBSITE_NAV, FragmentType.FRAGMENT_WEBSITE_THEME -> R.style.WebsiteCustomizationTheme
      else -> super.customTheme()
    }
  }

  override fun getToolbarBackgroundColor(): Int? {
    return when (type) {
      FragmentType.ALL_BOOST_ADD_ONS -> ContextCompat.getColor(this, R.color.colorPrimary)
      FragmentType.FRAGMENT_USER_PROFILE, FragmentType.FRAGMENT_USER_PROFILE_IMAGE_CROP,
      FragmentType.FRAGMENT_BUSINESS_PROFILE -> ContextCompat.getColor(this, R.color.black_4a4a4a_jio)
      FragmentType.FRAGMENT_WEBSITE_NAV, FragmentType.FRAGMENT_WEBSITE_THEME -> ContextCompat.getColor(this, R.color.website_custom_toolbar_color)
      else -> super.getToolbarBackgroundColor()
    }
  }

  override fun getToolbarTitleColor(): Int? {
    return when (type) {
      FragmentType.ALL_BOOST_ADD_ONS -> ContextCompat.getColor(this, R.color.white)
      else -> super.getToolbarTitleColor()
    }
  }

  override fun getToolbarTitle(): String? {
    return when (type) {
      FragmentType.ALL_BOOST_ADD_ONS -> resources.getString(R.string.all_boost_add_ons)
      FragmentType.FRAGMENT_WEBSITE_NAV -> getString(R.string.website_style_customisation)
      FragmentType.FRAGMENT_BUSINESS_PROFILE -> getString(R.string.business_profile_)
      FragmentType.FRAGMENT_USER_PROFILE -> getString(R.string.my_profile_d)
      FragmentType.FRAGMENT_USER_PROFILE_IMAGE_CROP -> getString(R.string.crop_your_profile_photo)

      else -> super.getToolbarTitle()
    }
  }

  override fun getNavigationIcon(): Drawable? {
    return when (type) {
      FragmentType.ALL_BOOST_ADD_ONS, FragmentType.FRAGMENT_WEBSITE_THEME, FragmentType.FRAGMENT_WEBSITE_NAV,
      FragmentType.FRAGMENT_BUSINESS_PROFILE, FragmentType.FRAGMENT_USER_PROFILE -> ContextCompat.getDrawable(this, R.drawable.ic_back_arrow_toolbar_d)
      FragmentType.FRAGMENT_USER_PROFILE_IMAGE_CROP -> ContextCompat.getDrawable(this, R.drawable.ic_cross_white)
      else -> super.getNavigationIcon()
    }
  }

  override fun isHideToolbar(): Boolean {
    return when (type) {
      FragmentType.DIGITAL_READINESS_SCORE, FragmentType.FRAGMENT_WEBSITE_THEME -> true
      else -> super.isHideToolbar()
    }
  }


  override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    val toolbarMenu = menu ?: return super.onCreateOptionsMenu(menu)
    val menuRes = getMenuRes() ?: return super.onCreateOptionsMenu(menu)
    menuInflater.inflate(menuRes, toolbarMenu)
    return true
  }

  open fun getMenuRes(): Int? {
    return when (type) {
      else -> null
    }
  }

  private fun shouldAddToBackStack(): Boolean {
    return when (type) {
      else -> false
    }
  }

  private fun setFragment() {
    val fragment = getFragmentInstance(type)
    fragment?.arguments = intent.extras
    binding?.container?.id?.let { addFragmentReplace(it, fragment, shouldAddToBackStack()) }
  }

  private fun getFragmentInstance(type: FragmentType?): BaseFragment<*, *>? {
    return when (type) {
      FragmentType.DIGITAL_READINESS_SCORE -> DigitalReadinessScoreFragment.newInstance()
      FragmentType.ALL_BOOST_ADD_ONS -> AllBoostAddonsFragment.newInstance()
//      FragmentType.ALL_BOOST_ADD_ONS -> DemoToDoListFragment.newInstance()
      FragmentType.FRAGMENT_WEBSITE_THEME -> FragmentWebsiteTheme.newInstance()
      FragmentType.FRAGMENT_BUSINESS_PROFILE -> BusinessProfileFragment.newInstance()
      FragmentType.FRAGMENT_WEBSITE_NAV -> CustomisationNavFragment.newInstance()
      FragmentType.FRAGMENT_USER_PROFILE -> {
        userFragment = UserProfileFragment.newInstance()
        userFragment
      }
      FragmentType.FRAGMENT_USER_PROFILE_IMAGE_CROP -> CropProfileImageFragment.newInstance()
      else -> throw IllegalFragmentTypeException()
    }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    for (fragment in supportFragmentManager.fragments) {
      fragment.onActivityResult(requestCode, resultCode, data)
    }
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
      android.R.id.home -> {
        onNavPressed()
        return true
      }
    }
    return super.onOptionsItemSelected(item)
  }

  fun onRefresh() {
    if (userFragment != null) userFragment?.onResume()
  }

}

fun fetchUserProfileData() {

}

fun Fragment.startFragmentDashboardActivity(type: FragmentType, bundle: Bundle = Bundle(), clearTop: Boolean = false, isResult: Boolean = false, requestCode: Int = 101) {
  val intent = Intent(activity, DashboardFragmentContainerActivity::class.java)
  intent.putExtras(bundle)
  intent.setFragmentType(type)
  if (clearTop) intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
  if (isResult.not()) startActivity(intent) else startActivityForResult(intent, requestCode)
}

fun startFragmentAccountDashboardNew(activity: Activity, type: FragmentType, bundle: Bundle = Bundle(), clearTop: Boolean) {
  val intent = Intent(activity, DashboardFragmentContainerActivity::class.java)
  intent.putExtras(bundle)
  intent.setFragmentType(type)
  if (clearTop) intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
  activity.startActivity(intent)
}

fun AppCompatActivity.startFragmentDashboardActivity(type: FragmentType, bundle: Bundle = Bundle(), clearTop: Boolean = false) {
  val intent = Intent(this, DashboardFragmentContainerActivity::class.java)
  intent.putExtras(bundle)
  intent.setFragmentType(type)
  if (clearTop) intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
  startActivity(intent)
}

fun Intent.setFragmentType(type: FragmentType): Intent {
  return this.putExtra(FRAGMENT_TYPE, type.ordinal)
}
