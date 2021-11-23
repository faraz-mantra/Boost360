package com.boost.presignin.ui.newOnboarding

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.boost.presignin.R
import com.boost.presignin.base.AppBaseFragment
import com.boost.presignin.constant.FragmentType
import com.boost.presignin.constant.IntentConstant
import com.boost.presignin.databinding.FragmentSetupMyWebsiteBinding
import com.boost.presignin.databinding.FragmentWelcomeBinding
import com.boost.presignin.databinding.LayoutSetUpMyWebsiteStep1Binding
import com.boost.presignin.databinding.LayoutSetUpMyWebsiteStep3Binding
import com.boost.presignin.helper.WebEngageController
import com.boost.presignin.model.BusinessInfoModel
import com.boost.presignin.model.authToken.saveAuthTokenData
import com.boost.presignin.model.business.BusinessCreateRequest
import com.boost.presignin.model.onboardingRequest.CategoryFloatsRequest
import com.boost.presignin.model.onboardingRequest.CreateProfileRequest
import com.boost.presignin.model.onboardingRequest.saveCategoryRequest
import com.boost.presignin.model.signup.FloatingPointCreateResponse
import com.boost.presignin.model.userprofile.BusinessProfileResponse
import com.boost.presignin.viewmodel.LoginSignUpViewModel
import com.framework.extensions.afterTextChanged
import com.framework.extensions.gone
import com.framework.extensions.observeOnce
import com.framework.models.BaseViewModel
import com.framework.pref.Key_Preferences
import com.framework.pref.UserSessionManager
import com.framework.pref.clientId
import com.framework.pref.clientIdThinksity
import com.framework.webengageconstant.NO_EVENT_VALUE
import com.framework.webengageconstant.PS_SIGNUP_SUCCESS
import com.framework.webengageconstant.SIGNUP_SUCCESS
import com.invitereferrals.invitereferrals.InviteReferralsApi

class SetupMyWebsiteStep3Fragment : AppBaseFragment<LayoutSetUpMyWebsiteStep3Binding, LoginSignUpViewModel>() {

    companion object {
        @JvmStatic
        fun newInstance(bundle: Bundle? = null): SetupMyWebsiteStep3Fragment {
            val fragment = SetupMyWebsiteStep3Fragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    private var session: UserSessionManager?=null
    private var responseCreateProfile: BusinessProfileResponse?=null
    var categoryFloatsReq: CategoryFloatsRequest?=null
    var createProfileReq: CreateProfileRequest?=null


    private val businessName by lazy {
        arguments?.getString(IntentConstant.EXTRA_BUSINESS_NAME.name)
    }

    private val phoneNumber by lazy {
        arguments?.getString(IntentConstant.EXTRA_PHONE_NUMBER.name)
    }


    override fun getLayout(): Int {
      return R.layout.layout_set_up_my_website_step_3
    }

    override fun getViewModelClass(): Class<LoginSignUpViewModel> {
        return LoginSignUpViewModel::class.java
    }

    override fun onCreateView() {
        super.onCreateView()
        setOnClickListeners()
    }

    private fun setOnClickListeners() {
        binding?.tvNextStep3?.setOnClickListener {
            apiHitCreateMerchantProfile()
        }

        binding?.addressInputLayout?.etInput?.afterTextChanged {
            binding?.tvNextStep3?.isEnabled = it.isEmpty().not()
            binding?.includeMobileView?.tvWebsiteName?.text = it
        }

        binding?.addressInputLayout?.ivIcon?.setOnClickListener {
            binding?.addressInputLayout?.ivIcon?.gone()
            binding?.addressInputLayout?.ivStatus?.gone()
            binding?.addressInputLayout?.etInput?.isEnabled = true

        }
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
        BusinessInfoModel(userMobile = phoneNumber)
        )
        categoryFloatsReq = CategoryFloatsRequest(createProfileReq)
        createProfileReq?.AuthToken = phoneNumber
        createProfileReq?.ClientId = clientId
        createProfileReq?.LoginKey = phoneNumber
        createProfileReq?.LoginSecret = ""
        createProfileReq?.Provider = "EMAIL"
        categoryFloatsReq?.businessName = businessName
    }

    private fun getBusinessRequest(): BusinessCreateRequest {
        val domain =binding!!.addressInputLayout.etInput.text.toString()
        val createRequest = BusinessCreateRequest()
        createRequest.autoFillSampleWebsiteData = true
        //createRequest.webTemplateId = floatsRequest?.categoryDataModel?.webTemplateId
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