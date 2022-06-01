package com.onboarding.nowfloats.ui.registration.instagram

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.framework.BaseApplication
import com.framework.extensions.gone
import com.framework.pref.UserSessionManager
import com.framework.pref.clientId
import com.framework.utils.ExoPlayerUtils
import com.framework.utils.PreferencesUtils
import com.framework.utils.spanBold
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.nowfloats.facebook.FacebookLoginHelper
import com.nowfloats.facebook.constants.FacebookGraphRequestType
import com.nowfloats.facebook.constants.FacebookPermissions
import com.nowfloats.facebook.graph.FacebookGraphManager
import com.nowfloats.facebook.models.BaseFacebookGraphResponse
import com.nowfloats.facebook.models.userPages.FacebookGraphUserPagesDataModel
import com.nowfloats.facebook.models.userPages.FacebookGraphUserPagesResponse
import com.nowfloats.instagram.graph.IGGraphManager
import com.nowfloats.instagram.graph.IGUserResponse
import com.nowfloats.instagram.models.IGFBPageLinkedResponse
import com.onboarding.nowfloats.R
import com.onboarding.nowfloats.base.AppBaseFragment
import com.onboarding.nowfloats.databinding.FragmentIgIntStepsBinding
import com.onboarding.nowfloats.model.channel.request.ChannelAccessToken
import com.onboarding.nowfloats.model.channel.request.UpdateChannelAccessTokenRequest
import com.onboarding.nowfloats.viewmodel.InstagramSetupViewModel
import com.onboarding.nowfloats.viewmodel.business.BusinessCreateViewModel


class IGIntStepsFragment: AppBaseFragment<FragmentIgIntStepsBinding, BusinessCreateViewModel>(),
    FacebookLoginHelper,FacebookGraphManager.GraphRequestUserAccountCallback,
    IGGraphManager.GraphRequestIGAccountCallback,IGGraphManager.GraphRequestIGUserCallback {


    private var igName: String?=null
    private var igId: String?=null
    private var accessToken: AccessToken?=null
    private var page: FacebookGraphUserPagesDataModel?=null
    private val TAG = "IGIntStepsFragment"
    private val callbackManager = CallbackManager.Factory.create()
    private var session:UserSessionManager?=null
    private var channelAccessToken =
        ChannelAccessToken(type = ChannelAccessToken.AccessTokenType.instagram.name.toLowerCase())
    private var instViewModel:InstagramSetupViewModel?=null

    enum class Step(val value:Int){
        STEP1(1),
        STEP2(2),
        STEP3(3),
        STEP4(4),
        NEXT_SCREEN(5);

        companion object{
            fun getByName(name: String?): Step? {
                return Step.values().find { it.name==name }
            }
        }

    }
    var currentStep:Step?=null
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
        currentStep = Step.getByName(arguments?.getString(BK_STEP))
        instViewModel = ViewModelProvider(requireActivity()).get(InstagramSetupViewModel::class.java)
        session = UserSessionManager(requireActivity())
        initExoPlayer()

        registerFacebookLoginCallback(this, callbackManager)

        setupUi()
        setOnClickListener(binding!!.btnBack,binding!!.btnNext,binding.ivPlayBtn)

    }

    private fun initExoPlayer() {
        ExoPlayerUtils.getInstance()
        binding.player.player = ExoPlayerUtils.player
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    private fun loadVideo(step: Step){

       val video= instViewModel?.getVideo(step)

        Glide.with(this).load(video?.thumbnailimage?.url)
            .into(binding?.thumbnail)

        video?.videourl?.url?.let {
            ExoPlayerUtils.prepare(it)
        }?:showLongToast(getString(R.string.unable_to_play_video))


    }

    private fun setupUi() {
        binding!!.tvBtnDesc.typeface = ResourcesCompat.getFont(BaseApplication.instance, R.font.semi_bold)
        binding!!.btnNext.setBackgroundColor(ContextCompat.getColor(requireActivity(),R.color.white))
        binding!!.btnNext.setTextColor(ContextCompat.getColor(requireActivity(),R.color.colorPrimary))
        when(currentStep){
            Step.STEP1->{
                binding!!.tvTitle.text = getString(R.string.create_your_instagram_page)
                binding!!.tvDesc.text=getString(R.string.create_your_instagram_account_or_log_in_if_you_already_have_one)
                binding!!.tvBtnDesc.text = getString(R.string.logged_in_to_your_instagram_account)
                binding!!.btnNext.text = getString(R.string.goto_step2)
                nextStep = Step.STEP2
            }
            Step.STEP2->{
                binding!!.tvTitle.text = getString(R.string.switch_to_professional_account)
                val boldText = "Settings ➜ Switch to Professional Account."
                binding!!.tvDesc.text=spanBold(getString(R.string.your_instagram_page_is_initially_a_personal_account_to_start_posting_your_business_updates_via_boost_360_you_need_to_convert_it_to_a_professional_account_from_instagram_go_to_settings_switch_to_professional_account),
                boldText)
                binding!!.tvBtnDesc.text = getString(R.string.switched_your_account)
                binding!!.btnNext.text = getString(R.string.goto_step3)
                nextStep = Step.STEP3

            }
            Step.STEP3->{
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
            Step.STEP4->{
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

        currentStep?.let { loadVideo(it) }
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when(v){
            binding.ivPlayBtn->{
                playVideo()
            }
            binding!!.btnBack->{
                requireActivity().onBackPressed()
            }
            binding!!.btnNext->{
                if (nextStep==Step.NEXT_SCREEN){
                  /*  addFragmentReplace(R.id.container,IGIntStatusFragment.newInstance(
                        IGIntStatusFragment.Status.FAILURE
                    ),true)*/
                    showProgress()
                    loginWithFacebook(
                        this, listOf(
                            FacebookPermissions.email,
                            FacebookPermissions.public_profile,
                            FacebookPermissions.read_insights,
                            FacebookPermissions.pages_show_list,
                            FacebookPermissions.ads_management,
                            FacebookPermissions.pages_read_engagement,
                            FacebookPermissions.pages_manage_posts,
                            FacebookPermissions.pages_read_user_content,
                            FacebookPermissions.pages_manage_metadata,
                            FacebookPermissions.instagram_basic,
                            FacebookPermissions.instagram_content_publish,
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

    private fun playVideo() {
            binding.thumbnail.gone()
            binding.ivPlayBtn.gone()
            ExoPlayerUtils.play()

    }

    override fun onFacebookLoginSuccess(result: LoginResult?) {
        Log.i(TAG, "onFacebookLoginSuccess: ")
        showProgress()
        accessToken = result?.accessToken ?: return
        accessToken?.let {
            PreferencesUtils.instance.saveFacebookUserToken(it.token)
            PreferencesUtils.instance.saveFacebookUserId(it.userId)
            FacebookGraphManager.requestUserPages(accessToken, this)
        }

    }

    override fun onFacebookLoginCancel() {
        Log.i(TAG, "onFacebookLoginCancel: ")
        hideProgress()
        openFailedState()
    }

    override fun onFacebookLoginError(error: FacebookException?) {
        Log.e(TAG, "onFacebookLoginError: ${error?.localizedMessage}")
        hideProgress()
        openFailedState()
    }

    override fun onCompleted(
        type: FacebookGraphRequestType,
        facebookGraphResponse: BaseFacebookGraphResponse?
    ) {
        showProgress()
        val response = facebookGraphResponse as? FacebookGraphUserPagesResponse
        val pages = response?.data ?: return
        if (pages.isEmpty().not()){
            page = pages.firstOrNull() ?: return
            page?.id?.let { IGGraphManager.requestIGAccount(it,accessToken,this) }
        }else{
            openFailedState()

        }


    }


    fun updateChannelAccessToken(igId:String,userName:String){

        val channelAccessToken = ChannelAccessToken(ChannelAccessToken.AccessTokenType.instagram.name
            ,userAccountName= userName
        ,userAccessTokenKey = page?.access_token,userAccountId = igId)

        viewModel?.updateChannelAccessToken(
            UpdateChannelAccessTokenRequest(
                channelAccessToken,
                clientId,
                session?.fPID!!
            ))?.observe(viewLifecycleOwner) {
            if (it.isSuccess()) {

                hideProgress()
                addFragmentReplace(
                    R.id.container, IGIntStatusFragment.newInstance(
                        igName,
                        IGIntStatusFragment.Status.SUCCESS
                    ), true
                )

            } else {
                openFailedState()
            }
        }
    }

    override fun onCompleted(response: IGFBPageLinkedResponse?) {
        igId = response?.instagram_business_account?.id

        if (igId!=null){
          //  updateChannelAccessToken(igId)
            IGGraphManager.requestIGUserDetails(igId!!,
                accessToken,this)
        }else{
            openFailedState()
        }
    }

    override fun onCompleted(response: IGUserResponse?) {
        igName = response?.username
        if (response?.username!=null){
            updateChannelAccessToken(igId!!,response.username)
        }else{
            openFailedState()
        }

    }

    private fun openFailedState() {
        hideProgress()
        addFragmentReplace(
            R.id.container, IGIntStatusFragment.newInstance(null,
                IGIntStatusFragment.Status.FAILURE
            ), true
        )
    }


    override fun onPause() {
        super.onPause()
        ExoPlayerUtils.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        ExoPlayerUtils.release()
    }
}