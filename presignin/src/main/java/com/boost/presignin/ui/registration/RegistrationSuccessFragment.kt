package com.boost.presignin.ui.registration

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import androidx.activity.OnBackPressedCallback
import com.boost.presignin.R
import com.boost.presignin.base.AppBaseFragment
import com.boost.presignin.databinding.FragmentRegistrationSuccessBinding
import com.boost.presignin.model.onboardingRequest.CategoryFloatsRequest
import com.boost.presignin.service.APIService
import com.boost.presignin.ui.WebPreviewActivity
import com.framework.models.BaseViewModel
import com.framework.pref.Key_Preferences.GET_FP_DETAILS_TAG
import com.framework.pref.Key_Preferences.GET_FP_EXPERIENCE_CODE
import com.framework.pref.UserSessionManager

private const val TIME_INTERVAL = 2000 // # milliseconds, desired time passed between two back presses.

private var mBackPressed: Long = 0

class RegistrationSuccessFragment : AppBaseFragment<FragmentRegistrationSuccessBinding, BaseViewModel>() {
    private var floatsRequest: CategoryFloatsRequest? = null
    private var session: UserSessionManager? = null

    companion object {
        @JvmStatic
        fun newInstance(registerRequest: CategoryFloatsRequest) =
                RegistrationSuccessFragment().apply {
                    arguments = Bundle().apply {
                        putSerializable("request", registerRequest)
                    }
                }
    }

    override fun getLayout(): Int {
        return R.layout.fragment_registration_success
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {
        floatsRequest = arguments?.getSerializable("request") as? CategoryFloatsRequest
        session = UserSessionManager(baseActivity)
        val businessName = floatsRequest?.businessName
        val name = floatsRequest?.requestProfile?.ProfileProperties?.userName
        val websiteUrl = floatsRequest?.webSiteUrl

        binding?.headingTv?.text = String.format(getString(R.string.congratulations_n_s), name)
        binding?.businessNameTv?.text = businessName;

        saveSessionData()
        val amountSpannableString = SpannableString(" $businessName ").apply {
            setSpan(ForegroundColorSpan(Color.rgb(0, 0, 0)), 0, length, 0)
            setSpan(StyleSpan(Typeface.BOLD), 0, length, 0)

        }


        val your = getString(R.string.you);
        binding?.subheading?.text = SpannableStringBuilder().apply {
            append(your)
            append(amountSpannableString)
            append(getString(R.string.registration_complete_subheading))
        }


        val underLineSpan = SpannableString(websiteUrl).apply {
            setSpan(UnderlineSpan(), 0, length, 0)
        }
        binding?.websiteTv?.text = SpannableStringBuilder().apply { append(underLineSpan) }

        binding?.lottieAnimation?.setAnimation(R.raw.lottie_anim_congratulation)
        binding?.lottieAnimation?.repeatCount = 0
        binding?.lottieAnimation?.playAnimation()

        binding?.previewAccountBt?.setOnClickListener {
            val bundle = Bundle()
            bundle.putSerializable("request", floatsRequest)
            navigator?.startActivity(WebPreviewActivity::class.java, bundle)
        }
        binding?.dashboardBt?.setOnClickListener {
            try {
                showProgress()
                val intent = Intent(baseActivity, Class.forName("com.nowfloats.PreSignUp.SplashScreen_Activity"))
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startService()
                baseActivity.startActivity(intent)
                baseActivity.finish()
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
            }
        }
        onBackPressed()
    }

    private fun startService() {
        baseActivity.startService(Intent(baseActivity, APIService::class.java))
    }

    private fun onBackPressed() {
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {
                    baseActivity.finishAffinity()
                    baseActivity.finish()
                } else {
                    showShortToast(getString(R.string.press_again_exit))
                }
                mBackPressed = System.currentTimeMillis();
            }
        })
    }

    private fun saveSessionData() {
        session?.storeFpTag(floatsRequest?.fpTag)
        session?.storeFPID(floatsRequest?.floatingPointId)
        session?.storeFPDetails(GET_FP_DETAILS_TAG, floatsRequest?.getWebSiteId())
        session?.storeFPDetails(GET_FP_EXPERIENCE_CODE, floatsRequest?.categoryDataModel?.experience_code)
        session?.userProfileId = floatsRequest?.requestProfile?.profileId
        session?.setAccountSave(true)
        session?.setUserLogin(true)
    }


}