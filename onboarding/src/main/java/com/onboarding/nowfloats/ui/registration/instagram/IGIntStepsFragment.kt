package com.onboarding.nowfloats.ui.registration.instagram

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.framework.BaseApplication
import com.framework.base.BaseActivity
import com.framework.models.BaseViewModel
import com.framework.pref.UserSessionManager
import com.framework.pref.clientId
import com.framework.utils.PreferencesUtils
import com.framework.utils.spanBold
import com.nowfloats.facebook.FacebookLoginHelper
import com.nowfloats.facebook.constants.FacebookGraphRequestType
import com.nowfloats.facebook.constants.FacebookPermissions
import com.nowfloats.facebook.graph.FacebookGraphManager
import com.nowfloats.facebook.models.BaseFacebookGraphResponse
import com.nowfloats.facebook.models.userPages.FacebookGraphUserPagesResponse
import com.onboarding.nowfloats.R
import com.onboarding.nowfloats.base.AppBaseFragment
import com.onboarding.nowfloats.databinding.FragmentIgIntStepsBinding
import com.onboarding.nowfloats.model.channel.ChannelType
import com.onboarding.nowfloats.model.channel.request.ChannelAccessToken
import com.onboarding.nowfloats.model.channel.request.UpdateChannelAccessTokenRequest
import com.onboarding.nowfloats.viewmodel.business.BusinessCreateViewModel

class IGIntStepsFragment: AppBaseFragment<FragmentIgIntStepsBinding, BusinessCreateViewModel>(),FacebookLoginHelper,FacebookGraphManager.GraphRequestUserAccountCallback {


    private var accessToken: AccessToken?=null
    private val TAG = "IGIntStepsFragment"
    private val callbackManager = CallbackManager.Factory.create()
    private var session:UserSessionManager?=null
    private var channelAccessToken =
        ChannelAccessToken(type = ChannelAccessToken.AccessTokenType.instagram.name.toLowerCase())

    enum class Step{
        STEP1,
        STEP2,
        STEP3,
        STEP4,
        NEXT_SCREEN
    }
    var currentStep:String?=null
    var nextStep:Step=Step.STEP1
    companion object{
        val BK_STEP="BK_STEP"
        fun newInstance(step:Step):IGIntStepsFragment{
            val fragment = IGIntStepsFragment()
            val bundle = Bundle().apply {
                putString(BK_STEP,step.name)
            }
            fragment.arguments = bundle
            return fragment
        }
    }


    override fun getLayout(): Int {
        return R.layout.fragment_ig_int_steps
    }

    override fun getViewModelClass(): Class<BusinessCreateViewModel> {
        return BusinessCreateViewModel::class.java
    }

    override fun onCreateView() {
        super.onCreateView()
        session = UserSessionManager(requireActivity())
        registerFacebookLoginCallback(this, callbackManager)

        currentStep = arguments?.getString(BK_STEP)
        setupUi()
        setOnClickListener(binding!!.btnBack,binding!!.btnNext)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    private fun setupUi() {
        binding!!.tvBtnDesc.typeface = ResourcesCompat.getFont(BaseApplication.instance, R.font.semi_bold)
        binding!!.btnNext.setBackgroundColor(ContextCompat.getColor(requireActivity(),R.color.white))
        binding!!.btnNext.setTextColor(ContextCompat.getColor(requireActivity(),R.color.colorPrimary))
        when(currentStep){
            Step.STEP1.name->{
                binding!!.tvTitle.text = getString(R.string.create_your_instagram_page)
                binding!!.tvDesc.text=getString(R.string.create_your_instagram_account_or_log_in_if_you_already_have_one)
                binding!!.tvBtnDesc.text = getString(R.string.logged_in_to_your_instagram_account)
                binding!!.btnNext.text = getString(R.string.goto_step2)
                nextStep = Step.STEP2
            }
            Step.STEP2.name->{
                binding!!.tvTitle.text = getString(R.string.switch_to_professional_account)
                val boldText = "Settings ➜ Switch to Professional Account."
                binding!!.tvDesc.text=spanBold(getString(R.string.your_instagram_page_is_initially_a_personal_account_to_start_posting_your_business_updates_via_boost_360_you_need_to_convert_it_to_a_professional_account_from_instagram_go_to_settings_switch_to_professional_account),
                boldText)
                binding!!.tvBtnDesc.text = getString(R.string.switched_your_account)
                binding!!.btnNext.text = getString(R.string.goto_step3)
                nextStep = Step.STEP3
            }
            Step.STEP3.name->{
                binding!!.tvTitle.text = getString(R.string.set_up_two_factor_authentication)
                val boldText1 = "Settings ➜ Security ➜ Two-factor Authentication"
                val boldText2 = "Get Started"
                val boldText3 = "Enable Text Message"

                binding!!.tvDesc.text= spanBold(
                    getString(R.string.next_set_up_two_factor_authentication_in_your_instagram_account_go_to_settings_security_two_factor_authentication_tap_on_get_started_and_enable_text_message),boldText1,boldText2,boldText3)
                binding!!.tvBtnDesc.text = getString(R.string.completed_two_factor_authentication)
                binding!!.btnNext.text = getString(R.string.goto_step4)
                nextStep = Step.STEP4
            }
            Step.STEP4.name->{
                val boldText1 = "Settings ➜ Instagram ➜ Connect to Instagram."
                binding!!.tvTitle.text = getString(R.string.connect_your_facebook_page_with_instagram)
                binding!!.tvDesc.text= spanBold(
                    getString(R.string.linking_your_instagram_page_to_facebook_makes_it_easier_to_operate_your_social_media_accounts_to_connect_open_your_facebook_page_and_go_to_settings_instagram_connect_to_instagram),boldText1)

                val boldText2 = "Connected Facebook with Instagram?"

                binding!!.tvBtnDesc.typeface = ResourcesCompat.getFont(BaseApplication.instance, R.font.regular)
                binding!!.tvBtnDesc.text = spanBold(
                    getString(R.string.connected_facebook_with_instagram_if_yes_then_just_tap_on_authorise_boost_360_button_below_to_allow_boost_360_to_access_sync_your_instagram_account_info_post_updates_on_your_behalf_and_read_related_channel_insights),
                boldText2)
                binding!!.btnNext.text = getString(R.string.autho_boost_360)
                binding!!.btnNext.setBackgroundColor(ContextCompat.getColor(requireActivity(),R.color.colorPrimary))
                binding!!.btnNext.setTextColor(ContextCompat.getColor(requireActivity(),R.color.white))
                nextStep = Step.NEXT_SCREEN

            }

        }
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when(v){
            binding!!.btnBack->{
                requireActivity().onBackPressed()
            }
            binding!!.btnNext->{
                if (nextStep==Step.NEXT_SCREEN){
                  /*  addFragmentReplace(R.id.container,IGIntStatusFragment.newInstance(
                        IGIntStatusFragment.Status.FAILURE
                    ),true)*/
                    loginWithFacebook(
                        this, listOf(
                            FacebookPermissions.email,
                            FacebookPermissions.public_profile,
                            FacebookPermissions.read_insights,
                            FacebookPermissions.pages_show_list,
                            FacebookPermissions.pages_manage_cta,
                            FacebookPermissions.ads_management,
                            FacebookPermissions.pages_read_engagement,
                            FacebookPermissions.pages_manage_posts,
                            FacebookPermissions.pages_read_user_content,
                            FacebookPermissions.pages_manage_metadata,
                            FacebookPermissions.manage_pages,
                            FacebookPermissions.business_management,
                            FacebookPermissions.instagram_basic,
                            FacebookPermissions.instagram_content_publish,
                            FacebookPermissions.instagram_manage_insights,
                            )
                    )


                }else{
                    addFragmentReplace(R.id.container,IGIntStepsFragment.newInstance(
                        nextStep
                    ),true)
                }

            }
        }
    }

    override fun onFacebookLoginSuccess(result: LoginResult?) {
        Log.i(TAG, "onFacebookLoginSuccess: ")
        accessToken = result?.accessToken ?: return
        accessToken?.let {
            PreferencesUtils.instance.saveFacebookUserToken(it.token)
            PreferencesUtils.instance.saveFacebookUserId(it.userId)
            FacebookGraphManager.requestUserPages(accessToken, this)
        }

    }

    override fun onFacebookLoginCancel() {
        Log.i(TAG, "onFacebookLoginCancel: ")
    }

    override fun onFacebookLoginError(error: FacebookException?) {
        Log.e(TAG, "onFacebookLoginError: ${error?.localizedMessage}")
    }

    override fun onCompleted(
        type: FacebookGraphRequestType,
        facebookGraphResponse: BaseFacebookGraphResponse?
    ) {
        val response = facebookGraphResponse as? FacebookGraphUserPagesResponse
        val pages = response?.data ?: return
        val page = pages.firstOrNull() ?: return
        page.id?.let { FacebookGraphManager.requestIGAccount(it,accessToken) }

    }

    fun updateChannelAccessToken(){
        val channelAccessToken = ChannelAccessToken(ChannelType.INSTAGRAM.name)

        viewModel?.updateChannelAccessToken(
            UpdateChannelAccessTokenRequest(
                channelAccessToken,
                clientId,
                session?.fPID!!
            ))
    }

}