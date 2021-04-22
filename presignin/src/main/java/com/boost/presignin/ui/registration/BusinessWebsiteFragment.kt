package com.boost.presignin.ui.registration

import BusinessDomainRequest
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.style.StyleSpan
import com.boost.presignin.R
import com.boost.presignin.constant.PreferenceConstant
import com.boost.presignin.databinding.FragmentBusinessWebsiteBinding
import com.boost.presignin.extensions.isWebsiteValid
import com.boost.presignin.model.RequestFloatsModel
import com.boost.presignin.model.business.BusinessCreateRequest
import com.boost.presignin.viewmodel.LoginSignUpViewModel
import com.framework.base.BaseFragment
import com.framework.base.BaseResponse
import com.framework.exceptions.NoNetworkException
import com.framework.extensions.afterTextChanged
import com.framework.extensions.gone
import com.framework.extensions.observeOnce
import com.framework.extensions.visible
import com.framework.pref.clientId
import com.framework.pref.clientId2
import com.framework.utils.NetworkUtils
import com.framework.views.DotProgressBar
import java.util.*

class BusinessWebsiteFragment : BaseFragment<FragmentBusinessWebsiteBinding, LoginSignUpViewModel>() {


    private var registerRequest: RequestFloatsModel? = null
    private var isDomain: Boolean = false
    private var domainValue: String? = null
    private var floatingPointId = ""
    private var isSyncCreateFpApi = false

    companion object {

        @JvmStatic
        fun newInstance(registerRequest: RequestFloatsModel) =
                BusinessWebsiteFragment().apply {
                    arguments = Bundle().apply {
                        putSerializable("request", registerRequest)
                    }
                }
    }

    override fun getLayout(): Int {
        return R.layout.fragment_business_website
    }

    override fun getViewModelClass(): Class<LoginSignUpViewModel> {
        return LoginSignUpViewModel::class.java
    }

    private fun setSubDomain(storeName: String, isInitial: Boolean = false) {
        val subDomain = storeName.filter { it.isLetterOrDigit() }
        val lengthDifference = storeName.length - subDomain.length
        if (subDomain != storeName || isInitial) {
            val selection = binding?.websiteEt?.selectionEnd?.minus(lengthDifference) ?: return
            binding?.websiteEt?.setText(subDomain.toLowerCase(Locale.ROOT))
            if (selection > 1) binding?.websiteEt?.setSelection(selection)
        }
        apiCheckDomain(storeName.toLowerCase(Locale.ROOT))
    }

    private fun errorSet() {
        isDomain = false
        domainValue = ""
        binding?.confirmButton?.isEnabled = false
        binding?.fragmentStatusIv?.setImageResource(R.drawable.ic_error)
    }

    private fun apiCheckDomain(subDomain: String, onSuccess: () -> Unit = {}) {
        if (!TextUtils.isEmpty(subDomain)) {
            val data = BusinessDomainRequest(clientId2, subDomain, subDomain)
            viewModel?.postCheckBusinessDomain(data)?.observeOnce(viewLifecycleOwner, {
                onPostBusinessDomainCheckResponse(it, onSuccess)
            })
        } else errorSet()
    }

    private fun onPostBusinessDomainCheckResponse(response: BaseResponse, onSuccess: () -> Unit) {
        if (response.error is NoNetworkException) {
            errorSet()
            return
        }
        if (response.stringResponse.isNullOrEmpty().not()) {
            isDomain = true
            binding?.confirmButton?.isEnabled = true
            domainValue = response.stringResponse?.toLowerCase(Locale.ROOT)
            binding?.fragmentStatusIv?.setImageResource(R.drawable.ic_valid)
            onSuccess()
        } else errorSet()
    }

    override fun onCreateView() {
        registerRequest = arguments?.getSerializable("request") as? RequestFloatsModel
        val websiteHint = registerRequest?.ProfileProperties?.businessName?.trim()?.replace(" ", "")
        val amountSpannableString = SpannableString("'$websiteHint' ").apply {
            setSpan(StyleSpan(Typeface.BOLD), 0, length, 0)
        }
        binding?.websiteEt?.afterTextChanged {
            setSubDomain(binding?.websiteEt?.text.toString())
            val sp = SpannableString(binding?.websiteEt?.text.toString()).apply {
                setSpan(StyleSpan(Typeface.BOLD), 0, length, 0)
            }
            binding?.websiteStatusTv?.text = SpannableStringBuilder().apply {
                append(sp)
                append(getString(R.string.website_available_text))
            }
        }

        binding?.websiteEt?.setText(websiteHint)

        binding?.websiteStatusTv?.text = SpannableStringBuilder().apply {
            append(amountSpannableString)
            append(getString(R.string.website_available_text))
        }
        binding?.confirmButton?.setOnClickListener {
            val website = binding?.websiteEt?.text?.toString()
            apiHitBusiness()
            if (!website.isWebsiteValid()) {
                showShortToast("Enter a valid website name")
                return@setOnClickListener
            }

            registerRequest?.webSiteUrl = "$website.nowfloats.com"

        }
    }
    protected val pref: SharedPreferences?
        get() {
            return baseActivity.getSharedPreferences(PreferenceConstant.NOW_FLOATS_PREFS, Context.MODE_PRIVATE)
        }
    protected val userProfileId: String?
        get() {
            return pref?.getString(PreferenceConstant.USER_PROFILE_ID, "")
        }

    private fun apiHitBusiness() {
        getDotProgress()?.let {

            if (NetworkUtils.isNetworkConnected()) {
                it.startAnimation()
                putCreateBusinessOnboarding(it)
            }
        }
    }
    fun getDotProgress(): DotProgressBar? {
        return DotProgressBar.Builder().setMargin(0).setAnimationDuration(800)
                .setDotBackground(R.drawable.ic_dot).setMaxScale(.7f).setMinScale(0.3f)
                .setNumberOfDots(3).setdotRadius(8).build(baseActivity)
    }
    private fun putCreateBusinessOnboarding(dotProgressBar: DotProgressBar) {
        if (checkFpCreate(dotProgressBar)) return
        val request = getBusinessRequest()
        isSyncCreateFpApi = true
        viewModel?.putCreateBusinessOnboarding(userProfileId, request)?.observeOnce(viewLifecycleOwner, {
            if (it.status == 200 || it.status == 201 || it.status == 202) {
                if (it.stringResponse.isNullOrEmpty().not()) {
                    floatingPointId = it.stringResponse ?: ""
//                    saveFpCreateData()
                    addFragmentReplace(com.framework.R.id.container, RegistrationSuccessFragment.newInstance(registerRequest!!), true);

                }
            }
        })
    }

    private fun checkFpCreate(dotProgressBar: DotProgressBar): Boolean {
        if (floatingPointId.isNotEmpty()) {
            return true
        }
        return false
    }
    private fun getBusinessRequest(): BusinessCreateRequest {
        val createRequest = BusinessCreateRequest()
        createRequest.autoFillSampleWebsiteData = true
        createRequest.webTemplateId = registerRequest?.categoryDataModel?.webTemplateId
        createRequest.clientId = clientId
        createRequest.tag = registerRequest?.ProfileProperties?.domainName
        createRequest.name = registerRequest?.ProfileProperties?.businessName
        createRequest.address = ""
        createRequest.pincode = ""
        createRequest.country = "India"
        createRequest.primaryNumber = registerRequest?.ProfileProperties?.userMobile
        createRequest.email = registerRequest?.ProfileProperties?.userEmail
        createRequest.primaryNumberCountryCode = "+91"
        createRequest.uri = ""
        createRequest.primaryCategory = registerRequest?.categoryDataModel?.category_key
        createRequest.appExperienceCode = registerRequest?.categoryDataModel?.experience_code
//        createRequest.whatsAppNumber = registerRequest?.channelActionDatas?.firstOrNull()?.getNumberWithCode()
//        createRequest.whatsAppNotificationOptIn = registerRequest?.whatsappEntransactional ?: false
        createRequest.boostXWebsiteUrl = "www.${registerRequest?.ProfileProperties?.domainName?.toLowerCase(Locale.ROOT)}.nowfloats.com"
        return createRequest
    }

}