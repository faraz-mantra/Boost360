package com.boost.presignin.ui.newOnboarding

import android.os.Bundle
import android.util.Log
import android.view.View
import com.appservice.utils.WebEngageController
import com.appservice.utils.capitalizeUtil
import com.boost.presignin.R
import com.boost.presignin.base.AppBaseFragment
import com.boost.presignin.constant.IntentConstant
import com.boost.presignin.databinding.LayoutSetUpMyWebsiteStep2Binding
import com.boost.presignin.model.category.CategoryDataModel
import com.boost.presignin.model.onBoardingInfo.Data
import com.boost.presignin.model.onBoardingInfo.OnBoardingInfo
import com.boost.presignin.viewmodel.LoginSignUpViewModel
import com.framework.extensions.afterTextChanged
import com.framework.extensions.observeOnce
import com.framework.pref.UserSessionManager
import com.framework.pref.clientId2
import com.framework.utils.fromHtml
import com.framework.utils.showKeyBoard
import com.framework.views.blur.setBlur
import com.framework.webengageconstant.*
import java.util.regex.Matcher
import java.util.regex.Pattern

class SetupMyWebsiteStep2Fragment : AppBaseFragment<LayoutSetUpMyWebsiteStep2Binding, LoginSignUpViewModel>() {
  private var session: UserSessionManager? = null
  companion object {
    @JvmStatic
    fun newInstance(bundle: Bundle? = null): SetupMyWebsiteStep2Fragment {
      val fragment = SetupMyWebsiteStep2Fragment()
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

  override fun getLayout(): Int {
    return R.layout.layout_set_up_my_website_step_2
  }

  override fun getViewModelClass(): Class<LoginSignUpViewModel> {
    return LoginSignUpViewModel::class.java
  }

  override fun onCreateView() {
    super.onCreateView()
    WebEngageController.trackEvent(PS_BUSINESS_PROFILE_PAGE_LOAD, PAGE_VIEW, NO_EVENT_VALUE)
    session = UserSessionManager(baseActivity)
    binding?.includeMobileView?.blurView?.setBlur(baseActivity, 1F)
    binding?.includeMobileView?.tvCategoryName?.text = categoryModel?.getCategoryWithoutNewLine() ?: ""
    setOnClickListeners()
  }

  private fun setOnClickListeners() {
    binding?.tvNextStep2.setOnClickListener {
      val bName = binding?.businessNameInputLayout?.etInput?.text.toString()
      if (validateBusinessName(bName)) {
        if (checkForConsecutiveDigits(bName)) {
          if (checkForConsecutiveSpecialCharacters(bName)) {
            WebEngageController.trackEvent(
              PS_BUSINESS_PROFILE_CLICK_NEW_UPPERCASE,
              CLICK,
              NO_EVENT_VALUE
            )
            addFragment(
              R.id.inner_container, SetupMyWebsiteStep3Fragment.newInstance(
                Bundle().apply {
                  putString(IntentConstant.DESKTOP_PREVIEW.name, desktopPreview)
                  putString(IntentConstant.MOBILE_PREVIEW.name, mobilePreview)
                  putString(IntentConstant.EXTRA_PHONE_NUMBER.name, phoneNumber)
                  putString(IntentConstant.CATEGORY_SUGG_UI.name, categoryLiveName)
                  putString(IntentConstant.SUB_CATEGORY_ID.name, subCategoryID)
                  putSerializable(IntentConstant.CATEGORY_DATA.name, categoryModel)
                  putBoolean(IntentConstant.WHATSAPP_CONSENT_FLAG.name, whatsappConsent ?: false)
                  putString(
                    IntentConstant.EXTRA_BUSINESS_NAME.name,
                    binding?.businessNameInputLayout?.etInput?.text.toString()
                  )
                }), true
            )
          } else {
            showShortToast(getString(R.string.businessname_special_characters_invalid_toast))
          }
        } else {
          showShortToast(getString(R.string.businessname_digits_invalid_toast))
        }
      } else {
        showShortToast(getString(R.string.businessname_format_invalid_toast))
      }
    }

    binding?.businessNameInputLayout?.etInput?.afterTextChanged {
      binding?.tvNextStep2?.isEnabled = it.isEmpty().not()
      binding?.includeMobileView?.tvTitle?.text = it.capitalizeUtil()
      binding?.businessNameInputLayout?.tvWordCount?.text = fromHtml("<font color=${if (it.isEmpty()) "#9DA4B2" else "#09121F"}>${it.length}</font><font color=#9DA4B2> /40</font>")
    }

//    binding?.businessNameInputLayout?.etInput?.setOnEditorActionListener { v, actionId, _ ->
//      if (actionId == EditorInfo.IME_ACTION_DONE) {
//        if (binding?.businessNameInputLayout?.etInput!!.text?.trim()?.isEmpty() == false) {
//          binding?.businessNameInputLayout?.etInput?.isEnabled = false
//          binding?.businessNameInputLayout?.tvWordCount?.gone()
//          binding?.businessNameInputLayout?.ivIcon?.visible()
//        }
//      }
//      false
//    }

    binding?.businessNameInputLayout?.etInput?.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
      if (hasFocus) binding?.businessNameInputLayout?.inputLayout?.setBackgroundResource(R.drawable.bg_dark_stroke_et_onboard)
    }

//    binding?.businessNameInputLayout?.ivIcon?.setOnClickListener {
//      baseActivity.showKeyBoard(binding?.businessNameInputLayout?.etInput)
//      binding?.businessNameInputLayout?.etInput?.isEnabled = true
//      binding?.businessNameInputLayout?.tvWordCount?.visible()
//      binding?.businessNameInputLayout?.ivIcon?.gone()
//    }
  }



  override fun onResume() {
    super.onResume()
    binding?.businessNameInputLayout?.etInput?.run { baseActivity.showKeyBoard(this) }
  }

  private fun validateBusinessName(bName: String): Boolean {
    val regex= "^[a-zA-Z0-9*@,.:;#\\-&\\s]*$"
    val p: Pattern = Pattern.compile(regex)
    val m: Matcher = p.matcher(bName)
    return m.matches()
  }

  private fun checkForConsecutiveDigits(bName: String): Boolean {
    val digitsArray = bName.replace("[a-zA-Z*@,.:;#\\-&]".toRegex(), " ").replace("\\s+".toRegex(), " ").split(" ")

    var result = true
    for (element in digitsArray) {
      if (!element.matches("[0-9]{0,4}".toRegex())){
        result = false;
        break
      }
    }
    return result
  }

  private fun checkForConsecutiveSpecialCharacters(bName: String): Boolean {
    val specialCharArray = bName.replace("[a-zA-Z0-9]".toRegex(), " ").replace("\\s+".toRegex(), " ").split(" ")

    var result = true
    for (element in specialCharArray) {
      if (!element.matches("[*@,.:;#\\-&]{0,3}".toRegex())) {
        result = false;
        break
      }
    }
    return result
  }

  override fun onPause() {
    super.onPause()
    val onBoardingParameters = Data()
    onBoardingParameters.desktopPreview=desktopPreview!!
    onBoardingParameters.mobilePreview=mobilePreview!!
    onBoardingParameters.phoneNumber=phoneNumber!!
    onBoardingParameters.categoryLiveName=categoryLiveName!!
    onBoardingParameters.subCategoryID=subCategoryID!!
    onBoardingParameters.selectedCategory= categoryModel.toString()
    onBoardingParameters.whatsappConsent=whatsappConsent!!
    onBoardingParameters.businessName=binding?.businessNameInputLayout?.etInput?.text.toString()
    onBoardingParameters.domainName=""
    onBoardingParameters.screen="One"
    val onBoardingData = OnBoardingInfo(phoneNumber!!, onBoardingParameters, session?.fPEmail!!,
      clientId2)
//    val gson = Gson()
//    val jsonString = gson.toJson(onBoardingData)
//    val jsonRequest = JSONObject(jsonString)
    viewModel?.storeNewOnBoardingData(onBoardingData)
      ?.observeOnce(viewLifecycleOwner) {
        if (it.isSuccess()) {
          Log.d("KAKAKAKAKAKAKAKAK", "KAKAKAKAKAKAKAKAK")
        }else{
          Log.d("KAKAKAKAKAKAKAKAK", "FAILED")
        }
      }
  }
}