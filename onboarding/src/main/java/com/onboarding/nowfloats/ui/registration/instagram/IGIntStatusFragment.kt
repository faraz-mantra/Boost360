package com.onboarding.nowfloats.ui.registration.instagram

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.View
import androidx.activity.addCallback
import androidx.fragment.app.FragmentManager
import com.framework.base.BaseActivity
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.framework.models.BaseViewModel
import com.framework.utils.spanBold
import com.framework.utils.spanClick
import com.framework.utils.spanColor
import com.onboarding.nowfloats.R
import com.onboarding.nowfloats.base.AppBaseFragment
import com.onboarding.nowfloats.databinding.FragmentIgIntStatusBinding
import com.onboarding.nowfloats.model.channel.haveFacebookShop
import com.onboarding.nowfloats.model.channel.haveInstagram
import com.onboarding.nowfloats.model.channel.haveTwitterChannels
import com.onboarding.nowfloats.model.channel.haveWhatsAppChannels
import com.onboarding.nowfloats.model.channel.request.isLinked
import com.onboarding.nowfloats.ui.registration.BaseRegistrationFragment

class IGIntStatusFragment: BaseRegistrationFragment<FragmentIgIntStatusBinding>() {


    private val TAG = "IGIntStatusFragment"
    enum class Status{
        SUCCESS,
        FAILURE
    }
    private var status:String?=null
    companion object{
        val BK_STATUS="BK_STATUS"
        fun newInstance(status: Status):IGIntStatusFragment{
            val fragment = IGIntStatusFragment()
            val bundle = Bundle().apply {
                putString(BK_STATUS,status.name)
            }
            fragment.arguments = bundle
            return fragment
        }
    }


    override fun getLayout(): Int {
        return R.layout.fragment_ig_int_status
    }



    override fun onCreateView() {
        super.onCreateView()
        status = arguments?.getString(BK_STATUS)
        setupUi()
        setOnClickListener(binding?.btnNext)
        baseActivity.onBackPressedDispatcher.addCallback {
            requireActivity().finish()
        }
    }

    private fun setupUi() {
        when(status){
            Status.SUCCESS.name->{
                binding!!.tvTitle.text = getString(R.string.instagram_integration_successful)
                binding!!.ivStatus.setImageResource(R.drawable.ic_ig_int_success)
                binding!!.layoutHelp.gone()
                binding!!.layoutFeatures.visible()
                binding!!.tvDesc.text = getString(R.string.congratulations_boost_360_is_successfully_connected_to_your_instagram_account_placeholder)
                binding!!.btnNext.text = getString(R.string.view_instagram_dashboard)
            }
            Status.FAILURE.name->{
                binding!!.tvTitle.text = getString(R.string.instagram_integration_failed)
                binding!!.ivStatus.setImageResource(R.drawable.ic_ig_int_failure)
                binding!!.layoutHelp.visible()
                binding!!.layoutFeatures.gone()

                binding!!.tvDesc.text = getString(R.string.looks_like_the_process_has_not_been_completed_properly_you_will_not_be_able_to_integrate_boost_360_with_instagram_if_you_do_not_complete_the_previous_steps)
                binding!!.btnNext.text = getString(R.string.re_check_previous_steps)



                binding!!.tvHelp.movementMethod = LinkMovementMethod.getInstance()
                binding!!.tvHelp.text = spanClick(getString(R.string.need_help_contact_support_for_assistance),
                    {
                        Log.i(TAG, "setupUi: ")

                    },
                    "Contact support")
                binding!!.tvHelp.text = spanColor(getString(R.string.need_help_contact_support_for_assistance),R.color.colorPrimary,
                    "Contact support")
            }
        }
    }


    override fun onClick(v: View) {
        super.onClick(v)
        when(v){
            binding!!.btnNext->{


            }
        }
    }

    private fun gotoNextScreen(isSkip: Boolean = false) {
        /* requestFloatsModel?.channelAccessTokens?.add(
            channelAccessToken
        )

        when {
            channels.haveInstagram() -> gotoInstagram()
            channels.haveFacebookShop() -> gotoFacebookShop()
            channels.haveTwitterChannels() -> gotoTwitterDetails()
            channels.haveWhatsAppChannels() -> gotoWhatsAppCallDetails()
            else -> gotoBusinessApiCallDetails()
        }*/
    }
}