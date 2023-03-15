package com.boost.presignin.ui.newOnboarding

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.boost.presignin.R
import com.boost.presignin.base.AppBaseFragment
import com.boost.presignin.constant.FragmentType
import com.boost.presignin.constant.IntentConstant
import com.boost.presignin.databinding.LayoutBusinessCategoryPreviewBinding
import com.boost.presignin.helper.WebEngageController
import com.boost.presignin.model.BusinessInfoModel
import com.boost.presignin.model.authToken.saveAuthTokenData
import com.boost.presignin.model.business.BusinessCreateRequest
import com.boost.presignin.model.category.CategoryDataModel
import com.boost.presignin.model.onboardingRequest.CategoryFloatsRequest
import com.boost.presignin.model.onboardingRequest.CreateProfileRequest
import com.boost.presignin.model.onboardingRequest.saveCategoryRequest
import com.boost.presignin.model.signup.FloatingPointCreateResponse
import com.boost.presignin.model.userprofile.BusinessProfileResponse
import com.boost.presignin.viewmodel.CategoryVideoModel
import com.framework.extensions.gone
import com.framework.extensions.observeOnce
import com.framework.extensions.visible
import com.framework.firebaseUtils.FirebaseRemoteConfigUtil
import com.framework.glide.util.glideLoad
import com.framework.pref.*
import com.framework.utils.makeSectionOfTextBold
import com.framework.webengageconstant.*
import com.invitereferrals.invitereferrals.InviteReferralsApi
import kotlinx.coroutines.*
import org.json.JSONObject


class BusinessCategoryPreviewFragment :
    AppBaseFragment<LayoutBusinessCategoryPreviewBinding, CategoryVideoModel>() {

    private val TAG = "BusinessCategoryPreview"
    var doNewFlowEnabled: Boolean? = null
    var createProfileReq: CreateProfileRequest? = null
    private var session: UserSessionManager? = null
    private var responseCreateProfile: BusinessProfileResponse? = null
    var categoryFloatsReq: CategoryFloatsRequest? = null
    val fragmentTtansaction = activity?.supportFragmentManager!!.beginTransaction()
    val progressFragment: DialogFragment = ProgressFragment()

    companion object {
        @JvmStatic
        fun newInstance(bundle: Bundle? = null): BusinessCategoryPreviewFragment {
            val fragment = BusinessCategoryPreviewFragment()
            fragment.arguments = bundle
            return fragment
        }
    }


    private val phoneNumber by lazy {
        arguments?.getString(IntentConstant.EXTRA_PHONE_NUMBER.name)
    }

    private val whatsappConsent by lazy {
        arguments?.getBoolean(IntentConstant.WHATSAPP_CONSENT_FLAG.name)
    }

    private val categoryLiveName by lazy {
        arguments?.getString(IntentConstant.CATEGORY_SUGG_UI.name)
    }

    private val subCategoryID by lazy {
        arguments?.getString(IntentConstant.SUB_CATEGORY_ID.name)
    }

    private val mobilePreview by lazy {
        arguments?.getString(IntentConstant.MOBILE_PREVIEW.name)
    }

    private val desktopPreview by lazy {
        arguments?.getString(IntentConstant.DESKTOP_PREVIEW.name)
    }

    private val categoryModel by lazy {
        arguments?.getSerializable(IntentConstant.CATEGORY_DATA.name) as? CategoryDataModel
    }

    private val businessName by lazy {
        arguments?.getString(IntentConstant.EXTRA_BUSINESS_NAME.name)
    }

    private val businessDomain by lazy {
        arguments?.getString(IntentConstant.BUSINESS_DOMAIN.name)
    }

    override fun getLayout(): Int {
        return R.layout.layout_business_category_preview
    }

    override fun getViewModelClass(): Class<CategoryVideoModel> {
        return CategoryVideoModel::class.java
    }

    override fun onCreateView() {
        super.onCreateView()
        doNewFlowEnabled = FirebaseRemoteConfigUtil.doNewOnBoardingJourneyEnabled()
        setOnClickListener(
            binding?.tvNextStep,
            binding?.layoutMobile,
            binding?.layoutDesktop,
            binding?.autocompleteSearchCategory
        )
        setupUi()
    }

    @SuppressLint("SetTextI18n")
    private fun setupUi() {
        if (doNewFlowEnabled!!) {
            binding.tvNextStep.text = "Launch my website"
        } else {
            binding.tvNextStep.text = "Next"
        }
        if (categoryLiveName.isNullOrEmpty().not()) {
            val totalString = categoryLiveName + " in " + categoryModel?.getCategoryWithoutNewLine()
            binding?.autocompleteSearchCategory?.text =
                makeSectionOfTextBold(totalString, categoryLiveName ?: "", font = R.font.semi_bold)
        } else {
            binding?.autocompleteSearchCategory?.text = makeSectionOfTextBold(
                categoryModel?.getCategoryWithoutNewLine() ?: "",
                categoryModel?.getCategoryWithoutNewLine() ?: "",
                font = R.font.semi_bold
            )
        }
        baseActivity.glideLoad(
            binding?.desktopPreview?.imgDesktop!!,
            desktopPreview ?: "",
            R.drawable.ic_placeholder
        )
        baseActivity.glideLoad(
            binding?.mobilePreview?.imgMobile!!,
            mobilePreview ?: "",
            R.drawable.ic_placeholder
        )
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when (v) {
            binding?.tvNextStep -> {
                WebEngageController.trackEvent(
                    PS_BUSINESS_CATEGORY_CLICK,
                    NEXT_CLICK,
                    NO_EVENT_VALUE
                )
                if (doNewFlowEnabled!!) {
                    progressFragment.show(fragmentTtansaction, "dialog")
                    apiHitCreateMerchantProfile()
                } else {
                    addFragment(
                        R.id.inner_container,
                        SetupMyWebsiteStep2Fragment.newInstance(Bundle().apply {
                            putString(IntentConstant.DESKTOP_PREVIEW.name, desktopPreview)
                            putString(IntentConstant.MOBILE_PREVIEW.name, mobilePreview)
                            putString(IntentConstant.EXTRA_PHONE_NUMBER.name, phoneNumber)
                            putString(IntentConstant.CATEGORY_SUGG_UI.name, categoryLiveName)
                            putString(IntentConstant.SUB_CATEGORY_ID.name, subCategoryID)
                            putSerializable(IntentConstant.CATEGORY_DATA.name, categoryModel)
                            putBoolean(
                                IntentConstant.WHATSAPP_CONSENT_FLAG.name,
                                whatsappConsent ?: false
                            )
                        }),
                        true
                    )
                }
            }
            binding?.layoutMobile -> {
                WebEngageController.trackEvent(
                    PS_SIGNUP_CATEGORY_PREVIEW_MOBILE_CLICK,
                    CLICK,
                    NO_EVENT_VALUE
                )
                setUpButtonSelectedUI()
            }
            binding?.layoutDesktop -> {
                WebEngageController.trackEvent(
                    PS_SIGNUP_CATEGORY_PREVIEW_DESKTOP_CLICK,
                    CLICK,
                    NO_EVENT_VALUE
                )
                setUpButtonSelectedUI(false)
            }
            binding?.autocompleteSearchCategory -> {
                baseActivity.onBackPressed()
            }
        }
    }

    private fun setUpButtonSelectedUI(isMobilePreviewMode: Boolean = true) {
        if (isMobilePreviewMode) {
            binding?.layoutMobile?.setBackgroundResource(R.drawable.ic_presignin_bg_yellow_solid_stroke)
            binding?.layoutDesktop?.setBackgroundResource(0)
            binding?.titleMobile?.setTextColor(getColor(R.color.colorAccent))
            binding?.titleDesktop?.setTextColor(getColor(R.color.black_4a4a4a))
            binding?.ivMobile?.visible()
            binding?.ivDesktop?.gone()
            binding?.desktopPreview?.root?.gone()
            binding?.mobilePreview?.root?.visible()
        } else {
            binding?.layoutMobile?.setBackgroundResource(0)
            binding?.layoutDesktop?.setBackgroundResource(R.drawable.ic_presignin_bg_yellow_solid_stroke)
            binding?.titleMobile?.setTextColor(getColor(R.color.black_4a4a4a))
            binding?.titleDesktop?.setTextColor(getColor(R.color.colorAccent))
            binding?.ivMobile?.gone()
            binding?.ivDesktop?.visible()
            binding?.desktopPreview?.root?.visible()
            binding?.mobilePreview?.root?.gone()
        }
    }

    private fun apiHitCreateMerchantProfile() {
        initRequest()
        WebEngageController.trackEvent(PS_SIGNUP_LAUNCHING_TRANSITION, PAGE_VIEW, NO_EVENT_VALUE)
        if (this.responseCreateProfile == null) {
            viewModel?.createMerchantProfile(request = categoryFloatsReq?.requestProfile)
                ?.observeOnce(viewLifecycleOwner) {
                    val businessProfileResponse = it as? BusinessProfileResponse
                    if (it.isSuccess() && businessProfileResponse != null && businessProfileResponse.result?.loginId.isNullOrEmpty()
                            .not()
                    ) {
                        putCreateBusinessOnBoarding(businessProfileResponse)
                    } else {
                        progressFragment.dismissAllowingStateLoss()
                        showShortToast(
                            it?.errorFlowMessage() ?: getString(R.string.unable_to_create_profile)
                        )
                    }
                }
        } else putCreateBusinessOnBoarding(this.responseCreateProfile!!)
    }

    private fun putCreateBusinessOnBoarding(response: BusinessProfileResponse) {
        this.responseCreateProfile = response
        val request = getBusinessRequest()
        viewModel?.putCreateBusinessV6(response.result?.loginId, request)
            ?.observeOnce(viewLifecycleOwner) {
                val result = it as? FloatingPointCreateResponse
                if (result?.isSuccess() == true && result.authTokens.isNullOrEmpty().not()) {
                    val authToken = result.authTokens?.firstOrNull()
                    WebEngageController.initiateUserLogin(response.result?.loginId)
                    WebEngageController.setUserContactAttributes(
                        response.result?.profileProperties?.userEmail,
                        response.result?.profileProperties?.userMobile,
                        response.result?.profileProperties?.userName,
                        response.result?.sourceClientId
                    )
                    WebEngageController.setFPTag(authToken?.floatingPointTag)
                    setReferralCode(authToken?.floatingPointId)
                    WebEngageController.trackEvent(
                        PS_SIGNUP_SUCCESS,
                        SIGNUP_SUCCESS,
                        NO_EVENT_VALUE
                    )
                    session?.userProfileId = response.result?.loginId
                    session?.userProfileEmail = response.result?.profileProperties?.userEmail
                    session?.userProfileName = response.result?.profileProperties?.userName
                    session?.userProfileMobile = response.result?.profileProperties?.userMobile
                    session?.storeISEnterprise(response.result?.isEnterprise.toString() + "")
                    session?.storeIsThinksity((response.result?.sourceClientId != null && response.result?.sourceClientId == clientIdThinksity).toString() + "")
                    session?.storeFPDetails(
                        Key_Preferences.GET_FP_EXPERIENCE_CODE,
                        categoryFloatsReq?.categoryDataModel?.experience_code
                    )
                    session?.storeFPID(authToken?.floatingPointId)
                    session?.storeFpTag(authToken?.floatingPointTag)
                    categoryFloatsReq?.floatingPointId = authToken?.floatingPointId!!
                    categoryFloatsReq?.fpTag = authToken.floatingPointTag
                    categoryFloatsReq?.requestProfile?.profileId = response.result?.loginId
                    session?.saveCategoryRequest(categoryFloatsReq!!)
                    session?.saveAuthTokenData(authToken)
                    session?.setUserSignUpComplete(true)
                    progressFragment.dismissAllowingStateLoss()
                    startFragmentFromNewOnBoardingActivity(
                        activity = baseActivity,
                        type = FragmentType.LOADING_ANIMATION_DASHBOARD_FRAGMENT,
                        bundle = arguments ?: Bundle(),
                        clearTop = true
                    )
                } else {
                    progressFragment.dismissAllowingStateLoss()
                    showShortToast(
                        it.message().ifEmpty { getString(R.string.error_create_business_fp) })
                }
            }
    }

    private fun initRequest() {
        val domain = businessDomain
        createProfileReq =
            CreateProfileRequest(ProfileProperties = BusinessInfoModel(userMobile = phoneNumber))
        createProfileReq?.AuthToken = phoneNumber
        createProfileReq?.ClientId = clientId
        createProfileReq?.LoginKey = phoneNumber
        createProfileReq?.LoginSecret = ""
        createProfileReq?.Provider = "EMAIL"
        categoryFloatsReq = CategoryFloatsRequest(
            requestProfile = createProfileReq,
            categoryDataModel = categoryModel,
            webSiteUrl = "$domain${getString(R.string.nowfloats_dot_com)}",
            businessName = businessName,
            desktopPreview = desktopPreview,
            mobilePreview = mobilePreview
        )
    }

    private fun getBusinessRequest(): BusinessCreateRequest {
        val domain = businessDomain
        val createRequest = BusinessCreateRequest()
        createRequest.autoFillSampleWebsiteData = true
        createRequest.webTemplateId = categoryModel?.webTemplateId
        createRequest.clientId = clientId
        createRequest.tag = domain
        createRequest.name = businessName
        createRequest.address = ""
        createRequest.pincode = ""
        createRequest.country = "India"
        createRequest.primaryNumber = phoneNumber
        // createRequest.email = floatsRequest?.userBusinessEmail
        createRequest.primaryNumberCountryCode = "+91"
        createRequest.uri = ""
        createRequest.primaryCategory = categoryModel?.category_key
        createRequest.appExperienceCode = categoryModel?.experience_code
        createRequest.whatsAppNumber = if (whatsappConsent == true) phoneNumber else null
        createRequest.whatsAppNotificationOptIn = whatsappConsent
        if (domain != null) {
            createRequest.boostXWebsiteUrl = "www.${domain.lowercase()}.nowfloats.com"
        }
        createRequest.SubCategory = categoryModel?.subCategoryName
        return createRequest
    }

    private fun setReferralCode(floatingPointId: String?) {
        InviteReferralsApi.getInstance(baseActivity)
            .ir_TrackingCallbackListener { tracking_response ->
                Log.e("Tracking", "Response ir_TrackingCallbackListener = $tracking_response")
            }
        InviteReferralsApi.getInstance(baseActivity).userDetailListener { ApiResponse ->
            Log.e("Users", "Response userDetailListener = $ApiResponse")
        }

        val email =
            if (categoryFloatsReq?.userBusinessEmail.isNullOrEmpty()) "noemail-${categoryFloatsReq?.userBusinessMobile}@noemail.com" else categoryFloatsReq?.userBusinessEmail
        Log.e("Set Referral", "Email: $email")
        InviteReferralsApi.getInstance(baseActivity).userDetails(
            categoryFloatsReq?.requestProfile?.ProfileProperties?.userName,
            email, categoryFloatsReq?.userBusinessMobile, 0, null, null
        )
        InviteReferralsApi.getInstance(baseActivity)
            .tracking("register", email, 0, null, null, null, JSONObject())
    }

    internal class ProgressFragment : DialogFragment() {

        var animationView: LottieAnimationView? = null
        var counterView: TextView? = null
        val scope =
            MainScope() // could also use an other scope such as viewModelScope if available
        var job: Job? = null
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setStyle(
                STYLE_NORMAL,
                android.R.style.Theme_Black_NoTitleBar_Fullscreen
            )
        }

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View {
            return inflater.inflate(R.layout.fragment_succuss_dialog, container, false)
        }

        companion object {
            fun newInstance(): ProgressFragment {
                return ProgressFragment()
            }
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            counterView = view.findViewById(R.id.counterView)
            if (animationView == null) animationView =
                view.findViewById(R.id.success_timer_animation)
            animationView?.setAnimation(R.raw.circle_loader_lottie_boost)
            animationView?.repeatCount = LottieDrawable.INFINITE
            animationView?.playAnimation()

            var counter = 15
            job = scope.launch {
                while (true) {
                    counterView?.text = counter.toString()
                    counter--
                    if (counter == -1) {
                        counter = 15
                    }
                    delay(1000)
                }
            }
            job!!.start()
        }

        override fun onDestroyView() {
            job!!.cancel()
            super.onDestroyView()
        }
    }
}
