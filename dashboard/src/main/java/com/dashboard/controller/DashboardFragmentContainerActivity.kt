package com.dashboard.controller

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.dashboard.R
import com.dashboard.base.AppBaseActivity
import com.dashboard.constant.FragmentType
import com.dashboard.controller.ui.allAddOns.AllBoostAddonsFragment
import com.dashboard.controller.ui.drScore.DigitalReadinessScoreFragment
import com.dashboard.controller.ui.website.WebsiteFragment
import com.dashboard.controller.ui.website_theme.FragmentWebsiteTheme
import com.framework.base.BaseFragment
import com.framework.base.FRAGMENT_TYPE
import com.framework.databinding.ActivityFragmentContainerBinding
import com.framework.exceptions.IllegalFragmentTypeException
import com.framework.models.BaseViewModel
import com.framework.views.customViews.CustomToolbar


open class DashboardFragmentContainerActivity : AppBaseActivity<ActivityFragmentContainerBinding, BaseViewModel>() {

  private var type: FragmentType? = null

  private var digitalReadinessScoreFragment: DigitalReadinessScoreFragment? = null
  private var allBoostAddonsFragment: AllBoostAddonsFragment? = null
  private var websiteThemeFragment: FragmentWebsiteTheme? = null

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

  override fun getToolbarTitleGravity(): Int {
   return when(type){
      FragmentType.FRAGMENT_WEBSITE_THEME->Gravity.START
      else -> Gravity.CENTER
    }
  }
  override fun onCreateView() {
    super.onCreateView()
    setFragment()
  }

  override fun getToolbar(): CustomToolbar? {
    return binding?.appBarLayout?.toolbar
  }

  override fun getToolbarTitleSize(): Float? {
    return resources.getDimension(R.dimen.heading_7)
  }

  override fun customTheme(): Int? {
    return when (type) {
      FragmentType.DIGITAL_READINESS_SCORE -> R.style.DashboardThemeNew
      FragmentType.FRAGMENT_WEBSITE_THEME -> R.style.DashboardThemeNew
      else -> super.customTheme()
    }
  }

  override fun getToolbarBackgroundColor(): Int? {
    return when (type) {
      FragmentType.ALL_BOOST_ADD_ONS -> ContextCompat.getColor(this, R.color.colorPrimary)
      FragmentType.FRAGMENT_WEBSITE_THEME->ContextCompat.getColor(this, R.color.gray_4e4e4e)
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
      FragmentType.FRAGMENT_WEBSITE_THEME -> getString(R.string.website_style_customisation)
      else -> super.getToolbarTitle()
    }
  }

  override fun getNavigationIcon(): Drawable? {
    return when (type) {
      FragmentType.ALL_BOOST_ADD_ONS , FragmentType.FRAGMENT_WEBSITE_THEME -> ContextCompat.getDrawable(this, R.drawable.ic_back_arrow_toolbar_d)
      else -> super.getNavigationIcon()
    }
  }

  override fun isHideToolbar(): Boolean {
    return when (type) {
      FragmentType.DIGITAL_READINESS_SCORE -> true
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
      FragmentType.DIGITAL_READINESS_SCORE -> {
        digitalReadinessScoreFragment = DigitalReadinessScoreFragment.newInstance()
        digitalReadinessScoreFragment
      }
      FragmentType.ALL_BOOST_ADD_ONS -> {
        allBoostAddonsFragment = AllBoostAddonsFragment.newInstance()
        allBoostAddonsFragment
      }FragmentType.FRAGMENT_WEBSITE_THEME -> {
        websiteThemeFragment = FragmentWebsiteTheme.newInstance()
        websiteThemeFragment
      }
      else -> throw IllegalFragmentTypeException()
    }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    digitalReadinessScoreFragment?.onActivityResult(requestCode, resultCode, data)
    allBoostAddonsFragment?.onActivityResult(requestCode, resultCode, data)
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
