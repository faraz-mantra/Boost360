package com.boost.presignin.ui.newOnboarding

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.content.ContextCompat
import com.boost.presignin.R
import com.boost.presignin.base.AppBaseFragment
import com.boost.presignin.constant.FragmentType
import com.boost.presignin.constant.IntentConstant
import com.boost.presignin.databinding.FragmentSetupMyWebsiteBinding
import com.boost.presignin.helper.WebEngageController
import com.boost.presignin.model.BusinessInfoModel
import com.boost.presignin.model.authToken.saveAuthTokenData
import com.boost.presignin.model.business.BusinessCreateRequest
import com.boost.presignin.model.onboardingRequest.CategoryFloatsRequest
import com.boost.presignin.model.onboardingRequest.CreateProfileRequest
import com.boost.presignin.model.onboardingRequest.saveCategoryRequest
import com.boost.presignin.model.signup.FloatingPointCreateResponse
import com.boost.presignin.model.userprofile.BusinessProfileResponse
import com.boost.presignin.ui.registration.RegistrationActivity
import com.boost.presignin.ui.registration.SUCCESS_FRAGMENT
import com.boost.presignin.viewmodel.CategoryVideoModel
import com.boost.presignin.viewmodel.LoginSignUpViewModel
import com.framework.base.FRAGMENT_TYPE
import com.framework.extensions.afterTextChanged
import com.framework.extensions.gone
import com.framework.extensions.observeOnce
import com.framework.extensions.visible
import com.framework.models.BaseViewModel
import com.framework.pref.Key_Preferences
import com.framework.pref.UserSessionManager
import com.framework.pref.clientId
import com.framework.pref.clientIdThinksity
import com.framework.views.blur.BlurView
import com.framework.views.blur.RenderScriptBlur
import com.framework.webengageconstant.NO_EVENT_VALUE
import com.framework.webengageconstant.PS_SIGNUP_SUCCESS
import com.framework.webengageconstant.SIGNUP_SUCCESS
import com.invitereferrals.invitereferrals.InviteReferralsApi
import com.onboarding.nowfloats.viewmodel.category.CategoryViewModel
import java.util.*

class SetupMyWebsiteFragment : AppBaseFragment<FragmentSetupMyWebsiteBinding, LoginSignUpViewModel>() {

    companion object {
        @JvmStatic
        fun newInstance(bundle: Bundle? = null): SetupMyWebsiteFragment {
            val fragment = SetupMyWebsiteFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    private var session: UserSessionManager?=null
    private var responseCreateProfile: BusinessProfileResponse?=null
    var categoryFloatsReq:CategoryFloatsRequest?=null
    var createProfileReq:CreateProfileRequest?=null
    private val phoneNumber by lazy {
        arguments?.getString(IntentConstant.EXTRA_PHONE_NUMBER.name)
    }

    override fun getLayout(): Int {
        return R.layout.fragment_setup_my_website
    }

    override fun getViewModelClass(): Class<LoginSignUpViewModel> {
        return LoginSignUpViewModel::class.java
    }

    override fun onCreateView() {
        super.onCreateView()
        this.session = UserSessionManager(baseActivity)

        initUI()
    }



    private fun initUI() {
        setOnClickListeners()
        setUpStepUI()
        setupStepOneData()
        manageStep2()
    }

    private fun manageStep2() {
        //handle business name
    }

    private fun setupStepOneData() {

    }

    private fun setOnClickListeners() {
        binding?.layoutStep1?.tvNextStep1?.setOnClickListener {
            setUpStepUI(1)
        }

        binding?.layoutStep2?.tvNextStep2?.setOnClickListener {
            setUpStepUI(2)
        }

        binding?.layoutStep3?.tvNextStep3?.setOnClickListener {
            apiHitCreateMerchantProfile()
        }

        binding?.layoutStep2?.edInputBusinessName?.afterTextChanged {
            binding?.layoutStep2?.tvNextStep2?.isEnabled = it.isEmpty().not()
            binding?.layoutStep2?.includeMobileView?.tvTitle?.text = it
        }

        binding?.layoutStep3?.edInputWebsiteAddress?.afterTextChanged {
            binding?.layoutStep3?.tvNextStep3?.isEnabled = it.isEmpty().not()
            binding?.layoutStep3?.includeMobileView?.tvWebsiteName?.text = it
        }


    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_help_on_boarding_new, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_item_help_onboard -> {
                showShortToast(getString(R.string.coming_soon))
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setUpStepUI(stepNumber: Int = 0) {
        when (stepNumber) {
            0 -> {
                binding?.layoutStep1?.root?.visible()
                binding?.layoutStep2?.root?.gone()
                binding?.layoutStep3?.root?.gone()
                binding?.ivStep1Setup?.setImageResource(R.drawable.ic_tick_circle_green_hollow)
                binding?.view1?.background = ContextCompat.getDrawable(requireContext(), R.color.gray_33727D82)
                binding?.ivStep2Setup?.setImageResource(R.drawable.ic_tick_circle_gray_hollow)
                binding?.view2?.background = ContextCompat.getDrawable(requireContext(), R.color.gray_33727D82)
                binding?.ivStep3Setup?.setImageResource(R.drawable.ic_tick_circle_gray_hollow)
            }
            1 -> {
                binding?.layoutStep1?.root?.gone()
                binding?.layoutStep2?.root?.visible()
                binding?.layoutStep3?.root?.gone()
                binding?.ivStep1Setup?.setImageResource(R.drawable.ic_tick_circle_green_solid_filled)
                binding?.view1?.background = ContextCompat.getDrawable(requireContext(), R.color.green_61CF96)
                binding?.ivStep2Setup?.setImageResource(R.drawable.ic_tick_circle_green_hollow)
                binding?.view2?.background = ContextCompat.getDrawable(requireContext(), R.color.gray_33727D82)
                binding?.ivStep3Setup?.setImageResource(R.drawable.ic_tick_circle_gray_hollow)
                binding?.layoutStep2?.includeMobileView?.blurView?.setBlur(0.25F)
            }
            2 -> {
                binding?.layoutStep1?.root?.gone()
                binding?.layoutStep2?.root?.gone()
                binding?.layoutStep3?.root?.visible()
                binding?.ivStep1Setup?.setImageResource(R.drawable.ic_tick_circle_green_solid_filled)
                binding?.view1?.background = ContextCompat.getDrawable(requireContext(), R.color.green_61CF96)
                binding?.ivStep2Setup?.setImageResource(R.drawable.ic_tick_circle_green_solid_filled)
                binding?.view2?.background = ContextCompat.getDrawable(requireContext(), R.color.green_61CF96)
                binding?.ivStep3Setup?.setImageResource(R.drawable.ic_tick_circle_green_hollow)
                binding?.layoutStep3?.includeMobileView?.blurView?.setBlur(0.25F)
            }
        }
    }

    private fun BlurView.setBlur(value: Float) {
        val decorView: View? = activity?.window?.decorView
        val rootView: ViewGroup = decorView?.findViewById(android.R.id.content) as ViewGroup
        val windowBackground: Drawable = decorView.background
        this.setupWith(rootView)?.setFrameClearDrawable(windowBackground)
            ?.setBlurAlgorithm(RenderScriptBlur(activity))?.setBlurRadius(value)
            ?.setHasFixedTransformationMatrix(true)
    }

    private fun apiHitBusiness(businessProfileResponse: BusinessProfileResponse) {
        putCreateBusinessOnBoarding(businessProfileResponse)
    }

    private fun putCreateBusinessOnBoarding(response: BusinessProfileResponse) {
        this.responseCreateProfile = response
        val request = getBusinessRequest()
        viewModel?.putCreateBusinessV6(response.result?.loginId, request)?.observeOnce(viewLifecycleOwner, {
            val result = it as? FloatingPointCreateResponse
            if (result?.isSuccess() == true && result.authTokens.isNullOrEmpty().not()) {
                val authToken = result.authTokens?.firstOrNull()
                WebEngageController.initiateUserLogin(response.result?.loginId)
                WebEngageController.setUserContactAttributes(
                    response.result?.profileProperties?.userEmail, response.result?.profileProperties?.userMobile,
                    response.result?.profileProperties?.userName, response.result?.sourceClientId
                )
                WebEngageController.setFPTag(authToken?.floatingPointTag)
                setReferralCode(authToken?.floatingPointId)
                WebEngageController.trackEvent(PS_SIGNUP_SUCCESS, SIGNUP_SUCCESS, NO_EVENT_VALUE)
                session?.userProfileId = response.result?.loginId
                session?.userProfileEmail = response.result?.profileProperties?.userEmail
                session?.userProfileName = response.result?.profileProperties?.userName
                session?.userProfileMobile = response.result?.profileProperties?.userMobile
                session?.storeISEnterprise(response.result?.isEnterprise.toString() + "")
                session?.storeIsThinksity((response.result?.sourceClientId != null && response.result?.sourceClientId == clientIdThinksity).toString() + "")
                session?.storeFPDetails(Key_Preferences.GET_FP_EXPERIENCE_CODE, categoryFloatsReq?.categoryDataModel?.experience_code)
                session?.storeFPID(authToken?.floatingPointId)
                session?.storeFpTag(authToken?.floatingPointTag)
                categoryFloatsReq?.floatingPointId = authToken?.floatingPointId!!
                categoryFloatsReq?.fpTag = authToken?.floatingPointTag
                categoryFloatsReq?.requestProfile?.profileId = response.result?.loginId
                session?.saveCategoryRequest(categoryFloatsReq!!)
                session?.saveAuthTokenData(authToken)
                session?.setUserSignUpComplete(true)
                startFragmentFromNewOnBoardingActivity(
                    activity = requireActivity(),
                    type = FragmentType.LOADING_ANIMATION_DASHBOARD_FRAGMENT,
                    bundle = Bundle(),
                    clearTop = true
                )
            } else {
                val msg = it.message()
                showShortToast(if (msg.isNotEmpty()) msg else getString(R.string.error_create_business_fp))
            }
            hideProgress()
        })
    }

    private fun initRequest() {
        createProfileReq = CreateProfileRequest(ProfileProperties =
        BusinessInfoModel(userMobile = phoneNumber))
        categoryFloatsReq = CategoryFloatsRequest(createProfileReq)
        createProfileReq?.AuthToken = phoneNumber
        createProfileReq?.ClientId = clientId
        createProfileReq?.LoginKey = phoneNumber
        createProfileReq?.LoginSecret = ""
        createProfileReq?.Provider = "EMAIL"
        categoryFloatsReq?.businessName = binding?.layoutStep2?.edInputBusinessName?.text.toString()
    }

    private fun getBusinessRequest(): BusinessCreateRequest {
        val domain =binding!!.layoutStep3.edInputWebsiteAddress.text.toString()
        val createRequest = BusinessCreateRequest()
        createRequest.autoFillSampleWebsiteData = true
        //createRequest.webTemplateId = floatsRequest?.categoryDataModel?.webTemplateId
        createRequest.clientId = clientId
        createRequest.tag = domain
        createRequest.name = binding!!.layoutStep2.edInputBusinessName.text.toString()
        createRequest.address = ""
        createRequest.pincode = ""
        createRequest.country = "India"
        createRequest.primaryNumber = phoneNumber
       // createRequest.email = floatsRequest?.userBusinessEmail
        createRequest.primaryNumberCountryCode = "+91"
        createRequest.uri = ""
       // createRequest.primaryCategory = floatsRequest?.categoryDataModel?.category_key
      //  createRequest.appExperienceCode = floatsRequest?.categoryDataModel?.experience_code
       // createRequest.whatsAppNumber = floatsRequest?.userBusinessMobile
       // createRequest.whatsAppNotificationOptIn = floatsRequest?.whatsAppFlag ?: false
        createRequest.boostXWebsiteUrl = "www.${domain.lowercase()}.nowfloats.com"
        return createRequest
    }

    private fun setReferralCode(floatingPointId: String?) {
        InviteReferralsApi.getInstance(baseActivity).ir_TrackingCallbackListener { tracking_response ->
            Log.e("Tracking", "Response ir_TrackingCallbackListener = $tracking_response")
        }
        InviteReferralsApi.getInstance(baseActivity).userDetailListener { ApiResponse ->
            Log.e("Users", "Response userDetailListener = $ApiResponse")
        }

        val email = if (categoryFloatsReq?.userBusinessEmail.isNullOrEmpty()) "noemail-${categoryFloatsReq?.userBusinessMobile}@noemail.com" else categoryFloatsReq?.userBusinessEmail
        Log.e("Set Referral", "Email: $email")
        InviteReferralsApi.getInstance(baseActivity).userDetails(
            categoryFloatsReq?.requestProfile?.ProfileProperties?.userName,
            email, categoryFloatsReq?.userBusinessMobile, 0, null, null
        )
        InviteReferralsApi.getInstance(baseActivity).tracking("register", email, 0, null, null)
    }

    private fun apiHitCreateMerchantProfile() {
        initRequest()
        showProgress("We're creating your online ${categoryFloatsReq?.categoryDataModel?.category_Name}...")
        if (this.responseCreateProfile == null) {
            viewModel?.createMerchantProfile(request = categoryFloatsReq?.requestProfile)?.observeOnce(viewLifecycleOwner, {
                val businessProfileResponse = it as? BusinessProfileResponse
                if (it.isSuccess() && businessProfileResponse != null && businessProfileResponse.result?.loginId.isNullOrEmpty().not()) {
                    apiHitBusiness(businessProfileResponse)
                } else {
                    hideProgress()
                    val msg = it?.errorNMessage()
                    showShortToast(if (msg.isNullOrEmpty().not()) msg else getString(R.string.unable_to_create_profile))
                }
            })
        } else apiHitBusiness(this.responseCreateProfile!!)
    }

}