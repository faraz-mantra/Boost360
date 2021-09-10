package com.dashboard.controller.ui.ownerinfo

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Gravity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.dashboard.R
import com.dashboard.base.AppBaseActivity
import com.dashboard.constant.FragmentType
import com.dashboard.constant.FragmentType.*
import com.dashboard.constant.IntentConstant
import com.dashboard.controller.setFragmentType
import com.framework.base.BaseFragment
import com.framework.base.FRAGMENT_TYPE
import com.framework.databinding.ActivityFragmentContainerBinding
import com.framework.exceptions.IllegalFragmentTypeException
import com.framework.models.BaseViewModel
import com.framework.pref.UserSessionManager
import com.framework.views.customViews.CustomToolbar

class OwnersInfoContainerActivity : AppBaseActivity<ActivityFragmentContainerBinding, BaseViewModel>() {
    override fun getLayout(): Int {
        return R.layout.activity_fragment_container
    }

    private var type: FragmentType? = null
    private var fragmentConsultationHours: FragmentConsultationHours? = null
    private var fragmentCreateDoctorProfile: FragmentCreateDoctorProfile? = null
    private var fragmentDoctorAdditionInfo: FragmentDoctorAdditionInfo? = null
    private var fragmentOwnerInfo: FragmentOwnerInfo? = null
    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        intent?.extras?.getInt(FRAGMENT_TYPE)?.let { type = values()[it] }
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView() {
        super.onCreateView()
        setFragment()
    }

    override fun getToolbar(): CustomToolbar? {
        return binding?.appBarLayout?.toolbar
    }

    override fun getToolbarTitleGravity(): Int {
        return Gravity.START
    }

    override fun getToolbarTitleSize(): Float? {
        return resources.getDimension(R.dimen.heading_7)
    }

    override fun customTheme(): Int? {
        return when (type) {
            DIGITAL_READINESS_SCORE -> R.style.DashboardThemeNew
            else -> super.customTheme()
        }
    }

    override fun getToolbarBackgroundColor(): Int? {
        return when (type) {
            CREATE_DOCTOR_PROFILE, DOCTOR_ADDITIONAL_INFO, OWNER_INFO, CONSULTATION_HOURS -> ContextCompat.getColor(this, R.color.colorPrimary)
            else -> super.getToolbarBackgroundColor()
        }
    }

    override fun getToolbarTitleColor(): Int? {
        return when (type) {
            CREATE_DOCTOR_PROFILE, DOCTOR_ADDITIONAL_INFO, OWNER_INFO, CONSULTATION_HOURS -> ContextCompat.getColor(this, R.color.white)
            else -> super.getToolbarTitleColor()
        }
    }

    override fun getToolbarTitle(): String? {
        return when (type) {
            OWNER_INFO -> getString(R.string.add_owners_info)
            else -> super.getToolbarTitle()
        }
    }


    override fun getNavigationIcon(): Drawable? {
        return when (type) {
            CREATE_DOCTOR_PROFILE, DOCTOR_ADDITIONAL_INFO, OWNER_INFO, CONSULTATION_HOURS -> ContextCompat.getDrawable(this, R.drawable.ic_back_arrow_toolbar_d)
            else -> super.getNavigationIcon()
        }
    }

    private fun setFragment() {
        val fragment = getFragmentInstance(type)
        fragment?.arguments = intent.extras
        binding?.container?.id?.let { addFragmentReplace(it, fragment, shouldAddToBackStack()) }
    }

    private fun getFragmentInstance(type: FragmentType?): BaseFragment<*, *>? {
        return when (type) {
            CONSULTATION_HOURS -> {
                fragmentConsultationHours = FragmentConsultationHours.newInstance()
                fragmentConsultationHours
            }
            CREATE_DOCTOR_PROFILE -> {
                fragmentCreateDoctorProfile = FragmentCreateDoctorProfile.newInstance()
                fragmentCreateDoctorProfile
            }
            OWNER_INFO -> {
                fragmentOwnerInfo = FragmentOwnerInfo.newInstance()
                fragmentOwnerInfo
            }
            DOCTOR_ADDITIONAL_INFO -> {
                fragmentDoctorAdditionInfo = FragmentDoctorAdditionInfo.newInstance()
                fragmentDoctorAdditionInfo
            }
            else -> throw IllegalFragmentTypeException()
        }
    }

    private fun shouldAddToBackStack(): Boolean {
        return when (type) {
            else -> false
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        fragmentConsultationHours?.onActivityResult(requestCode, resultCode, data)
        fragmentCreateDoctorProfile?.onActivityResult(requestCode, resultCode, data)
        fragmentOwnerInfo?.onActivityResult(requestCode, resultCode, data)
        fragmentDoctorAdditionInfo?.onActivityResult(requestCode, resultCode, data)
    }

}

fun Fragment.startOwnersInfoNewActivity(sessionManager: UserSessionManager, type: FragmentType, bundle: Bundle = Bundle(), clearTop: Boolean = false, isResult: Boolean = false, requestCode: Int = 101) {
    val intent = Intent(activity, OwnersInfoContainerActivity::class.java)
    bundle.putString(IntentConstant.FPTAG.name, sessionManager.fpTag)
    intent.putExtras(bundle)
    intent.setFragmentType(type)
    if (clearTop) intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    if (isResult.not()) startActivity(intent) else startActivityForResult(intent, requestCode)
}