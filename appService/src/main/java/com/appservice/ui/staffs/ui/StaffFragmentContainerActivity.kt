package com.appservice.ui.staffs.ui

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.appservice.R
import com.appservice.base.AppBaseActivity
import com.appservice.constant.FragmentType
import com.appservice.constant.IntentConstant
import com.appservice.ui.staffs.ui.breaks.ScheduledBreaksFragmnt
import com.appservice.ui.staffs.ui.breaks.StaffBreakConfirmFragment
import com.appservice.ui.staffs.ui.details.StaffDetailsFragment
import com.appservice.ui.staffs.ui.home.StaffAddFragment
import com.appservice.ui.staffs.ui.home.StaffHomeFragment
import com.appservice.ui.staffs.ui.home.StaffProfileListingFragment
import com.appservice.ui.staffs.ui.profile.StaffProfileDetailsFragment
import com.appservice.ui.staffs.ui.services.StaffServicesFragment
import com.appservice.ui.catalog.common.WeeklyAppointmentFragment
import com.framework.base.BaseFragment
import com.framework.base.FRAGMENT_TYPE
import com.framework.databinding.ActivityFragmentContainerBinding
import com.framework.exceptions.IllegalFragmentTypeException
import com.framework.models.BaseViewModel
import com.framework.views.customViews.CustomToolbar

class StaffFragmentContainerActivity : AppBaseActivity<ActivityFragmentContainerBinding, BaseViewModel>() {

  private var fragmentType: FragmentType? = null
  private var staffAddFragment: StaffAddFragment? = null
  private var staffHomeFragment: StaffHomeFragment? = null
  private var staffDetailsFragment: StaffDetailsFragment? = null
  private var staffServicesFragment: StaffServicesFragment? = null
  private var staffTimingFragment: WeeklyAppointmentFragment? = null
  private var scheduledBreaksFragment: ScheduledBreaksFragmnt? = null
  private var breakConfirmFragment: StaffBreakConfirmFragment? = null
  private var staffProfileDetailsFragment: StaffProfileDetailsFragment? = null
  private var staffProfileListingFragment: StaffProfileListingFragment? = null

  override fun getLayout(): Int {
    return R.layout.activity_fragment_container
  }


  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun onCreateView() {
    super.onCreateView()
    getBundle()
    setFragment()
  }

  private fun getBundle() {
    intent.getStringExtra(IntentConstant.FP_TAG.name)?.let {
      UserSession.apply {
        fpTag = intent.getStringExtra(IntentConstant.FP_TAG.name)
        fpId = intent.getStringExtra(IntentConstant.FP_ID.name)
        clientId = intent.getStringExtra(IntentConstant.CLIENT_ID.name)
      }
    }
  }

  fun changeTheme(color: Int) {
    getToolbar()?.setBackgroundColor(ContextCompat.getColor(this, color))
    window?.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      window?.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
      window?.statusBarColor = ContextCompat.getColor(this, color)
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    when (intent.extras) {
      null -> fragmentType = FragmentType.STAFF_HOME_FRAGMENT
      else -> intent?.extras?.getInt(FRAGMENT_TYPE)
        ?.let { fragmentType = FragmentType.values()[it] }
    }
    super.onCreate(savedInstanceState)
  }

  override fun getToolbar(): CustomToolbar? {
    return binding?.appBarLayout?.toolbar
  }



  override fun customTheme(): Int? {
    return when (fragmentType) {
      FragmentType.STAFF_PROFILE_LISTING_FRAGMENT, FragmentType.STAFF_HOME_FRAGMENT, FragmentType.STAFF_ADD_FRAGMENT, FragmentType.STAFF_DETAILS_FRAGMENT, FragmentType.STAFF_TIMING_FRAGMENT,
      FragmentType.STAFF_SELECT_SERVICES_FRAGMENT, FragmentType.STAFF_PROFILE_DETAILS_FRAGMENT,
      -> R.style.AppTheme_staff_home
      FragmentType.STAFF_SCHEDULED_BREAK_FRAGMENT -> R.style.AppTheme_staff_details
      else -> super.customTheme()
    }
  }

  override fun getToolbarBackgroundColor(): Int? {
    return when (fragmentType) {
      FragmentType.STAFF_HOME_FRAGMENT, FragmentType.STAFF_ADD_FRAGMENT, FragmentType.STAFF_PROFILE_LISTING_FRAGMENT,
      FragmentType.STAFF_DETAILS_FRAGMENT, FragmentType.STAFF_TIMING_FRAGMENT,
      FragmentType.STAFF_SELECT_SERVICES_FRAGMENT, FragmentType.STAFF_PROFILE_DETAILS_FRAGMENT,
      -> ContextCompat.getColor(this, R.color.yellow_ffb900)
      else -> super.getToolbarBackgroundColor()
    }
  }

  override fun getToolbarTitleColor(): Int? {
    return when (fragmentType) {
      FragmentType.STAFF_HOME_FRAGMENT, FragmentType.STAFF_ADD_FRAGMENT,
      FragmentType.STAFF_DETAILS_FRAGMENT,
      -> ContextCompat.getColor(this, R.color.white)
      else -> super.getToolbarTitleColor()
    }
  }

  override fun getNavigationIcon(): Drawable? {
    return ContextCompat.getDrawable(this, R.drawable.ic_back_arrow_new)
  }

  private fun shouldAddToBackStack(): Boolean {
    return when (fragmentType) {
      else -> false
    }
  }

  private fun setFragment() {
    val fragment = getFragmentInstance(fragmentType)
    fragment?.arguments = intent.extras
    binding?.container?.id.let { addFragmentReplace(it, fragment, shouldAddToBackStack()) }
  }

  override fun getToolbarTitle(): String? {
    return when (fragmentType) {
      FragmentType.STAFF_ADD_FRAGMENT -> getString(R.string.toolbar_staff_listing)
      FragmentType.STAFF_HOME_FRAGMENT -> getString(R.string.toolbar_staff_listing)
      FragmentType.STAFF_DETAILS_FRAGMENT -> getString(R.string.toolbar_staff_details)
      FragmentType.STAFF_SELECT_SERVICES_FRAGMENT -> getString(R.string.toolbar_select_services)
      FragmentType.STAFF_TIMING_FRAGMENT -> getString(R.string.toolbar_staff_timing)
      FragmentType.STAFF_SCHEDULED_BREAK_FRAGMENT -> getString(R.string.toolbar_schedule_break)
      FragmentType.STAFF_SERVICES_CONFIRM_FRAGMENT -> getString(R.string.toolbar_schedule_breaks)
      FragmentType.STAFF_PROFILE_LISTING_FRAGMENT -> getString(R.string.toolbar_staff_listing)
      FragmentType.STAFF_PROFILE_DETAILS_FRAGMENT -> getString(R.string.toolbar_staff_details)
      else -> super.getToolbarTitle()
    }
  }

  private fun getFragmentInstance(type: FragmentType?): BaseFragment<*, *>? {
    return when (type) {
      FragmentType.STAFF_PROFILE_DETAILS_FRAGMENT -> {
        staffProfileDetailsFragment = StaffProfileDetailsFragment.newInstance()
        staffProfileDetailsFragment
      }
      FragmentType.STAFF_PROFILE_LISTING_FRAGMENT -> {
        staffProfileListingFragment = StaffProfileListingFragment.newInstance()
        staffProfileListingFragment
      }
      FragmentType.STAFF_ADD_FRAGMENT -> {
        staffAddFragment = StaffAddFragment.newInstance()
        staffAddFragment
      }
      FragmentType.STAFF_HOME_FRAGMENT -> {
        staffHomeFragment = StaffHomeFragment.newInstance()
        staffHomeFragment
      }
      FragmentType.STAFF_DETAILS_FRAGMENT -> {
        staffDetailsFragment = StaffDetailsFragment.newInstance()
        staffDetailsFragment
      }
      FragmentType.STAFF_TIMING_FRAGMENT -> {
        staffTimingFragment = WeeklyAppointmentFragment.newInstance()
        staffTimingFragment
      }
      FragmentType.STAFF_SCHEDULED_BREAK_FRAGMENT -> {
        scheduledBreaksFragment = ScheduledBreaksFragmnt.newInstance()
        scheduledBreaksFragment
      }
      FragmentType.STAFF_SELECT_SERVICES_FRAGMENT -> {
        staffServicesFragment = StaffServicesFragment.newInstance()
        staffServicesFragment
      }
      else -> throw IllegalFragmentTypeException()
    }
  }

  override fun isHideToolbar(): Boolean {
    return super.isHideToolbar()
  }

  override fun onBackPressed() {
    when (fragmentType) {
      FragmentType.STAFF_PROFILE_DETAILS_FRAGMENT -> staffProfileDetailsFragment?.onBackPresDetail()
      else -> super.onBackPressed()
    }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    staffDetailsFragment?.onActivityResult(requestCode, resultCode, data)
    staffHomeFragment?.onActivityResult(requestCode, resultCode, data)
    staffProfileDetailsFragment?.onActivityResult(requestCode, resultCode, data)
    staffServicesFragment?.onActivityResult(requestCode, resultCode, data)
    staffTimingFragment?.onActivityResult(requestCode, resultCode, data)
  }
}


fun Fragment.startStaffFragmentActivity(
  type: FragmentType,
  bundle: Bundle = Bundle(),
  clearTop: Boolean = false,
  isResult: Boolean = false
) {
  val intent = Intent(activity, StaffFragmentContainerActivity::class.java)
  intent.putExtras(bundle)
  intent.setFragmentType(type)
  if (clearTop) intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
  if (isResult.not()) startActivity(intent) else startActivityForResult(intent, 101)
}

fun startStaffFragmentActivity(
  activity: Activity,
  type: FragmentType,
  bundle: Bundle = Bundle(),
  clearTop: Boolean,
  isResult: Boolean = false,
  requestCode: Int = Constants.REQUEST_CODE
) {
  val intent = Intent(activity, StaffFragmentContainerActivity::class.java)
  intent.putExtras(bundle)
  intent.setFragmentType(type)
  if (clearTop) intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
  if (isResult.not()) activity.startActivity(intent) else activity.startActivityForResult(
    intent,
    requestCode
  )
}

fun AppCompatActivity.startStaffFragmentActivity(
  type: FragmentType,
  bundle: Bundle = Bundle(),
  clearTop: Boolean = false
) {
  val intent = Intent(this, StaffFragmentContainerActivity::class.java)
  intent.putExtras(bundle)
  intent.setFragmentType(type)
  if (clearTop) intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
  startActivity(intent)
}

fun Intent.setFragmentType(type: FragmentType): Intent {
  return this.putExtra(FRAGMENT_TYPE, type.ordinal)
}

