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
import com.boost.presignin.R
import com.boost.presignin.base.AppBaseFragment
import com.boost.presignin.databinding.FragmentRegistrationSuccessBinding
import com.boost.presignin.model.RequestFloatsModel
import com.boost.presignin.ui.WebPreviewActivity
import com.framework.models.BaseViewModel
import com.framework.pref.Key_Preferences.GET_FP_DETAILS_TAG
import com.framework.pref.Key_Preferences.GET_FP_EXPERIENCE_CODE
import com.framework.pref.UserSessionManager


class RegistrationSuccessFragment : AppBaseFragment<FragmentRegistrationSuccessBinding, BaseViewModel>() {
    private var registerRequest: RequestFloatsModel? = null
    private var session: UserSessionManager? = null

    companion object {
        @JvmStatic
        fun newInstance(registerRequest: RequestFloatsModel) =
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
        registerRequest = arguments?.getSerializable("request") as? RequestFloatsModel
        session = UserSessionManager(baseActivity)
        val businessName = registerRequest?.ProfileProperties?.businessName
        val name = registerRequest?.ProfileProperties!!.userName
        val websiteUrl = registerRequest!!.webSiteUrl!!

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
            bundle.putSerializable("request", registerRequest)
            navigator?.startActivity(WebPreviewActivity::class.java,bundle)
        }
        binding?.dashboardBt?.setOnClickListener {
            try {
                showProgress()
                val intent = Intent(baseActivity, Class.forName("com.nowfloats.PreSignUp.SplashScreen_Activity"))
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                baseActivity.startActivity(intent)
                baseActivity.finish()
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
            }
        }
    }

    private fun saveSessionData() {
        session?.storeFpTag(registerRequest?.fpTag)
        session?.storeFPID(registerRequest?.floatingPointId)
        session?.storeFPDetails(GET_FP_DETAILS_TAG, registerRequest?.getWebSiteId())
        session?.storeFPDetails(GET_FP_EXPERIENCE_CODE, registerRequest?.categoryDataModel?.experience_code)
        session?.userProfileId = registerRequest?.profileId
        session?.setAccountSave(true)
        session?.setUserLogin(true)
    }

}