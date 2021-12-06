package com.boost.presignin.ui.newOnboarding

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.appservice.base.AppBaseActivity
import com.boost.presignin.R
import com.boost.presignin.constant.FragmentType
import com.boost.presignin.databinding.ActivityNewOnboarddingContainerBinding
import com.boost.presignin.dialog.WebViewDialog
import com.framework.base.BaseFragment
import com.framework.base.FRAGMENT_TYPE
import com.framework.exceptions.IllegalFragmentTypeException
import com.framework.models.BaseViewModel
import com.framework.views.customViews.CustomToolbar

class NewOnBoardingContainerActivity :
    AppBaseActivity<ActivityNewOnboarddingContainerBinding, BaseViewModel>() {

    private var type: FragmentType? = null

    private var enterPhoneFragment: EnterPhoneFragment? = null
    private var introSlideShowFragment: IntroSlideShowFragment? = null
    private var setupMyWebsiteFragment: SetupMyWebsiteFragment? = null
    private var verifyPhoneFragment: VerifyPhoneFragment? = null
    private var welcomeFragment: WelcomeFragment? = null
    private var loadingAnimationFragment: LoaderAnimationFragment? = null

    override fun getLayout(): Int {
        return R.layout.activity_new_onboardding_container
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        intent?.extras?.getInt(FRAGMENT_TYPE)?.let {
            type = FragmentType.values()[it] }
        //makeFullScreen(type)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView() {
        super.onCreateView()
        setFragment()
    }

    override fun isHideToolbar(): Boolean {
        return when (type) {
            FragmentType.LOADING_ANIMATION_DASHBOARD_FRAGMENT,
            FragmentType.INTRO_SLIDE_SHOW_FRAGMENT,
            FragmentType.WELCOME_FRAGMENT -> true
            else -> super.isHideToolbar()
        }
    }

    override fun getToolbar(): CustomToolbar? {
        return binding?.toolbar
    }

    private fun setToolbarTitleNew(title: String) {
        binding?.title?.text = title
        getToolbarTitleColor()?.let { binding?.title?.setTextColor(it) }
    }

    override fun customTheme(): Int? {
        return when (type) {
            /*FragmentType.INTRO_SLIDE_SHOW_FRAGMENT -> {
                R.style.New_OnBoarding_Fullscreen
            }*/
            else -> super.customTheme()
        }
    }

    override fun getToolbarBackgroundColor(): Int {
        return when (type) {
            else -> ContextCompat.getColor(this, R.color.white_F5F8FD)
        }
    }

    override fun getToolbarTitleColor(): Int? {
        return when (type) {
            else -> ContextCompat.getColor(this, R.color.black_4a4a4a)
        }
    }

    override fun getNavigationIcon(): Drawable? {
        return when (type) {
            /*FragmentType.BOOK_A_DOMAIN_SSL_FRAGMENT -> ContextCompat.getDrawable(
                this,
                R.drawable.ic_domain_toolbar_back
            )*/
            else -> ContextCompat.getDrawable(this, R.drawable.ic_back_arrow_left_grey)
        }
    }

    private fun setFragment() {
        val fragment = getFragmentInstance(type)
        setFragmentTitle(type)
        fragment?.arguments = intent.extras
        binding?.container?.id?.let { addFragmentReplace(it, fragment, shouldAddToBackStack()) }
    }

    private fun setFragmentTitle(type: FragmentType?) {
        setToolbarTitleNew(
            resources.getString(
                when (type) {
                    FragmentType.ENTER_PHONE_FRAGMENT -> {
                        R.string.enter_your_phone_number
                    }
                    FragmentType.SET_UP_MY_WEBSITE_FRAGMENT -> {
                        R.string.setup_my_website
                    }
                    FragmentType.VERIFY_PHONE_FRAGMENT -> {
                        R.string.verify_your_number
                    }
                    FragmentType.WELCOME_FRAGMENT,
                    FragmentType.INTRO_SLIDE_SHOW_FRAGMENT ->
                        R.string.empty_string
                    else -> R.string.empty_string
                }
            )
        )
    }

    private fun getFragmentInstance(type: FragmentType?): BaseFragment<*, *>? {
        return when (type) {
            FragmentType.ENTER_PHONE_FRAGMENT -> {
                enterPhoneFragment = EnterPhoneFragment.newInstance(intent.extras)
                enterPhoneFragment
            }
            FragmentType.INTRO_SLIDE_SHOW_FRAGMENT -> {
                introSlideShowFragment = IntroSlideShowFragment.newInstance(intent.extras)
                introSlideShowFragment
            }
            FragmentType.SET_UP_MY_WEBSITE_FRAGMENT -> {
                setupMyWebsiteFragment = SetupMyWebsiteFragment.newInstance(intent.extras)
                setupMyWebsiteFragment
            }
            FragmentType.VERIFY_PHONE_FRAGMENT -> {
                verifyPhoneFragment = VerifyPhoneFragment.newInstance(intent.extras)
                verifyPhoneFragment
            }
            FragmentType.WELCOME_FRAGMENT -> {
                welcomeFragment = WelcomeFragment.newInstance(intent.extras)
                welcomeFragment
            }
            FragmentType.LOADING_ANIMATION_DASHBOARD_FRAGMENT -> {
                loadingAnimationFragment = LoaderAnimationFragment.newInstance(intent.extras)
                loadingAnimationFragment
            }
            else -> throw IllegalFragmentTypeException()
        }
    }

    private fun makeFullScreen(type: FragmentType?) {
        if (type == FragmentType.INTRO_SLIDE_SHOW_FRAGMENT) {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            this.window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }
    }

    private fun shouldAddToBackStack(): Boolean {
        return when (type) {
            else -> false
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        enterPhoneFragment?.onActivityResult(requestCode, resultCode, data)
        introSlideShowFragment?.onActivityResult(requestCode, resultCode, data)
        setupMyWebsiteFragment?.onActivityResult(requestCode, resultCode, data)
        verifyPhoneFragment?.onActivityResult(requestCode, resultCode, data)
        welcomeFragment?.onActivityResult(requestCode, resultCode, data)
    }
}

fun startFragmentFromNewOnBoardingActivity(
    activity: Activity,
    type: FragmentType,
    bundle: Bundle = Bundle(),
    clearTop: Boolean
) {
    val intent = Intent(activity, NewOnBoardingContainerActivity::class.java)
    intent.putExtras(bundle)
    intent.setFragmentType(type)
    if (clearTop) intent.flags =
        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    activity.startActivity(intent)
}

fun startFragmentFromNewOnBoardingActivityFinish(
    activity: Activity,
    type: FragmentType,
    bundle: Bundle = Bundle(),
    clearTop: Boolean
) {
    val intent = Intent(activity, NewOnBoardingContainerActivity::class.java)
    intent.putExtras(bundle)
    intent.setFragmentType(type)
    if (clearTop) intent.flags =
        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    activity.startActivity(intent)
    activity.finish()
}

fun Intent.setFragmentType(type: FragmentType): Intent {
    return this.putExtra(FRAGMENT_TYPE, type.ordinal)
}