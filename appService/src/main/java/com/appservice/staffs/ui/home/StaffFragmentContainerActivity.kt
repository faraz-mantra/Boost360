package com.appservice.staffs.ui.home

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.appservice.R
import com.appservice.base.AppBaseActivity
import com.appservice.constant.FragmentType
import com.appservice.constant.IntentConstant
import com.appservice.staffs.ui.Constants
import com.appservice.staffs.ui.breaks.ScheduledBreaksFragmnt
import com.appservice.staffs.ui.breaks.StaffBreakConfirmFragment
import com.appservice.staffs.ui.details.StaffDetailsFragment
import com.appservice.staffs.ui.details.timing.StaffTimingFragment
import com.appservice.staffs.ui.profile.StaffProfileDetailsFragment
import com.appservice.staffs.ui.profile.StaffProfileListingFragment
import com.appservice.staffs.ui.services.StaffServicesFragment
import com.appservice.ui.catlogService.setFragmentType
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
    private var staffTimingFragment: StaffTimingFragment? = null
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
        when {
            intent.extras?.get(IntentConstant.FP_TAG.name)!= null -> {
                UserSession.fpId = intent.extras!!.getString(IntentConstant.FP_TAG.name)!!
                UserSession.customerID = intent.extras!!.getString(IntentConstant.CLIENT_ID.name)!!
            }

        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        when (intent.extras) {
            null -> {
                fragmentType = FragmentType.STAFF_HOME_FRAGMENT
            }
            else -> {
                intent?.extras?.getInt(FRAGMENT_TYPE)?.let {
                    fragmentType = FragmentType.values()[it]
                }
            }
        }
        super.onCreate(savedInstanceState)
    }

    override fun getToolbar(): CustomToolbar {
        return binding!!.appBarLayout.toolbar
    }

    override fun getToolbarTitleSize(): Float {
        return resources.getDimension(R.dimen.body_2)
    }

    override fun customTheme(): Int? {
        return when (fragmentType) {
            FragmentType.STAFF_PROFILE_LISTING_FRAGMENT, FragmentType.STAFF_HOME_FRAGMENT, FragmentType.STAFF_ADD_FRAGMENT -> R.style.AppTheme_staff_home
            FragmentType.STAFF_DETAILS_FRAGMENT, FragmentType.STAFF_TIMING_FRAGMENT,FragmentType.STAFF_PROFILE_DETAILS_FRAGMENT,
            FragmentType.STAFF_SELECT_SERVICES_FRAGMENT, FragmentType.STAFF_SCHEDULED_BREAK_FRAGMENT
            -> R.style.AppTheme_staff_details
            else -> super.customTheme()
        }
    }

    override fun getToolbarBackgroundColor(): Int? {
        return when (fragmentType) {
            FragmentType.STAFF_HOME_FRAGMENT, FragmentType.STAFF_ADD_FRAGMENT, FragmentType.STAFF_PROFILE_LISTING_FRAGMENT, FragmentType.STAFF_PROFILE_DETAILS_FRAGMENT -> ContextCompat.getColor(this, R.color.yellow_ffb900)
            else -> super.getToolbarBackgroundColor()
        }
    }

    override fun getToolbarTitleColor(): Int? {
        return when (fragmentType) {
            FragmentType.STAFF_HOME_FRAGMENT, FragmentType.STAFF_ADD_FRAGMENT,
            FragmentType.STAFF_DETAILS_FRAGMENT -> ContextCompat.getColor(this, R.color.white)
            else -> super.getToolbarTitleColor()
        }
    }

    override fun getNavigationIcon(): Drawable? {
        return getDrawable(R.drawable.ic_arrow_left)
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
            FragmentType.STAFF_ADD_FRAGMENT -> "STAFF LISTING"
            FragmentType.STAFF_HOME_FRAGMENT -> "STAFF LISTING"
            FragmentType.STAFF_DETAILS_FRAGMENT -> "Staff Details"
            FragmentType.STAFF_SELECT_SERVICES_FRAGMENT -> "Select Services"
            FragmentType.STAFF_TIMING_FRAGMENT -> "Staff Timing"
            FragmentType.STAFF_SCHEDULED_BREAK_FRAGMENT -> "Scheduled Breaks"
            FragmentType.STAFF_SERVICES_CONFIRM_FRAGMENT -> "Scheduled Breaks"
            FragmentType.STAFF_PROFILE_LISTING_FRAGMENT -> "STAFF LISTING"
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
                staffTimingFragment = StaffTimingFragment.newInstance()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        staffDetailsFragment?.onActivityResult(requestCode, resultCode, data)
    }

}

fun Fragment.startStaffFragmentActivity(type: FragmentType, bundle: Bundle = Bundle(), clearTop: Boolean = false, isResult: Boolean = false) {
    val intent = Intent(activity, StaffFragmentContainerActivity::class.java)
    intent.putExtras(bundle)
    intent.setFragmentType(type)
    if (clearTop) intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    if (isResult.not()) startActivity(intent) else startActivityForResult(intent, 101)
}

fun startStaffFragmentActivity(activity: Activity, type: FragmentType, bundle: Bundle = Bundle(), clearTop: Boolean, isResult: Boolean = false, requestCode: Int= Constants.REQUEST_CODE) {
    val intent = Intent(activity, StaffFragmentContainerActivity::class.java)
    intent.putExtras(bundle)
    intent.setFragmentType(type)
    if (clearTop) intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    if (isResult.not()) activity.startActivity(intent) else activity.startActivityForResult(intent, requestCode)
}

fun AppCompatActivity.startStaffFragmentActivity(type: FragmentType, bundle: Bundle = Bundle(), clearTop: Boolean = false) {
    val intent = Intent(this, StaffFragmentContainerActivity::class.java)
    intent.putExtras(bundle)
    intent.setFragmentType(type)
    if (clearTop) intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    startActivity(intent)
}

fun Intent.setFragmentType(type: FragmentType): Intent {
    return this.putExtra(FRAGMENT_TYPE, type.ordinal)
}

