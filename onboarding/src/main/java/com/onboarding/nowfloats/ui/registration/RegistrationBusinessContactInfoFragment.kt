package com.onboarding.nowfloats.ui.registration

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import com.framework.base.BaseResponse
import com.framework.extensions.gone
import com.framework.extensions.isVisible
import com.framework.extensions.observeOnce
import com.framework.extensions.visible
import com.framework.pref.clientId2
import com.framework.utils.showKeyBoard
import com.framework.views.DotProgressBar
import com.framework.webengageconstant.BUILDING_YOUR_BUSINESS_CONTACT_INFO
import com.framework.webengageconstant.CLICKED
import com.framework.webengageconstant.CONFIRM
import com.google.android.libraries.places.widget.Autocomplete
import com.onboarding.nowfloats.R
import com.onboarding.nowfloats.constant.PreferenceConstant
import com.onboarding.nowfloats.databinding.FragmentRegistrationBusinessContactInfoBinding
import com.onboarding.nowfloats.extensions.fadeIn
import com.onboarding.nowfloats.model.registration.BusinessInfoModel
import com.onboarding.nowfloats.model.verification.RequestValidateEmail
import com.onboarding.nowfloats.ui.CitySearchDialog
import com.onboarding.nowfloats.utils.WebEngageController
import okio.Buffer
import okio.BufferedSource
import java.nio.charset.Charset

class RegistrationBusinessContactInfoFragment :
  BaseRegistrationFragment<FragmentRegistrationBusinessContactInfoBinding>() {

  private var businessInfoModel = BusinessInfoModel()
  private val PLACE_SEARCH_REQUEST = 1000
  private var personNumber = ""

  companion object {
    @JvmStatic
    fun newInstance(bundle: Bundle? = null): RegistrationBusinessContactInfoFragment {
      val fragment = RegistrationBusinessContactInfoFragment()
      fragment.arguments = bundle
      return fragment
    }
  }

  override fun onCreateView() {
    super.onCreateView()
    personNumber = (pref?.getString(PreferenceConstant.PERSON_NUMBER, "") ?: "")
    binding?.viewImage?.post {
      (binding?.viewImage?.fadeIn(200L)?.mergeWith(binding?.viewBusiness?.fadeIn(100L))
        ?.mergeWith(binding?.viewForm?.fadeIn(100L)))?.andThen(
          binding?.title?.fadeIn(100L)
            ?.mergeWith(binding?.subTitle?.fadeIn(50L))
        )?.andThen(binding?.formMain?.fadeIn(50L))
        ?.andThen(binding?.next?.fadeIn())?.doOnComplete {
          baseActivity.showKeyBoard(binding?.storeName)
        }?.subscribe()
    }
    setOnClickListener(binding?.next, binding?.textBtn, binding?.address)
    binding?.number?.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
      if (hasFocus && binding?.countryCode?.visibility == GONE) {
        binding?.countryCode?.visible()
        binding?.number?.hint = ""
        binding?.number?.compoundDrawablePadding =
          resources.getDimensionPixelOffset(R.dimen.size_36)
      } else if (binding?.number?.text.isNullOrEmpty()) {
        binding?.countryCode?.gone()
        binding?.number?.hint = resources.getString(R.string.business_contact_number)
        binding?.number?.compoundDrawablePadding = resources.getDimensionPixelOffset(R.dimen.size_4)
      }
    }
    setSavedData()
  }

  override fun setSavedData() {
    val contactInfo = this.requestFloatsModel?.contactInfo
    if (contactInfo != null) {
      businessInfoModel = contactInfo
      binding?.storeName?.setText(contactInfo.businessName)
      binding?.address?.setText(contactInfo.addressCity)
      binding?.email?.setText(contactInfo.email)
      val number = contactInfo.number ?: return
      binding?.number?.setText(number)
      binding?.countryCode?.visible()
      binding?.number?.hint = ""
      binding?.number?.compoundDrawablePadding = resources.getDimensionPixelOffset(R.dimen.size_36)
    }
    if (personNumber.isNotEmpty()) {
      binding?.number?.setText(personNumber)
      binding?.countryCode?.visible()
      binding?.number?.hint = ""
      binding?.number?.compoundDrawablePadding = resources.getDimensionPixelOffset(R.dimen.size_36)
    }
  }

  override fun onClick(v: View) {
    when (v) {
      binding?.next, binding?.textBtn -> {
        requestFloatsModel?.contactInfo = businessInfoModel
        if (binding?.textBtn?.isVisible() == true && isValid()) {
          getDotProgress()?.let {
            binding?.textBtn?.visibility = GONE
            binding?.next?.addView(it)
            it.startAnimation()
            checkIsValidEmail(it)
          }
        }
      }
      binding?.address -> startAutocompleteActivity()
    }
  }

  private fun checkIsValidEmail(dotProgressBar: DotProgressBar) {
    if (businessInfoModel.email.isNullOrEmpty().not()) {
      viewModel?.validateUsersEmail(RequestValidateEmail(clientId2, businessInfoModel.email))
        ?.observeOnce(viewLifecycleOwner, { it1 ->
          hideProgress()
          if (it1.isSuccess()) {
            if (parseResponse(it1)) errorMessageShow(
              dotProgressBar,
              getString(R.string.this_email_is_already_in_use)
            )
            else goNextPageDomain(dotProgressBar)
          } else errorMessageShow(dotProgressBar, getString(R.string.validation_error_try_again))
        })
    } else goNextPageDomain(dotProgressBar)
  }

  private fun errorMessageShow(dotProgressBar: DotProgressBar, error: String) {
    Handler().postDelayed({
      showLongToast(error)
      dotProgressBar.stopAnimation()
      binding?.next?.removeView(dotProgressBar)
      dotProgressBar.removeAllViews()
      binding?.textBtn?.visibility = VISIBLE
    }, 100)
  }

  private fun goNextPageDomain(dotProgressBar: DotProgressBar) {
    Handler().postDelayed({
      dotProgressBar.stopAnimation()
      binding?.next?.removeView(dotProgressBar)
      dotProgressBar.removeAllViews()
      binding?.textBtn?.visibility = VISIBLE
      gotoBusinessWebsite()
      //Business Contact Info Event Tracker.
      WebEngageController.trackEvent(BUILDING_YOUR_BUSINESS_CONTACT_INFO, CONFIRM, CLICKED)
    }, 100)
  }

  private fun isValid(): Boolean {
    requestFloatsModel?.contactInfo?.businessName = binding?.storeName?.text?.toString()
    requestFloatsModel?.contactInfo?.addressCity = binding?.address?.text?.toString()
    requestFloatsModel?.contactInfo?.email = binding?.email?.text?.toString()
//    businessInfoModel.number = binding?.number?.text?.toString()

    return if (businessInfoModel.businessName.isNullOrBlank()) {
      showShortToast(resources.getString(R.string.business_cant_empty))
      false
    } else if (businessInfoModel.addressCity.isNullOrBlank()) {
      showShortToast(resources.getString(R.string.business_address_cant_empty))
      false
    } else if (!businessInfoModel.email.isNullOrEmpty() && !businessInfoModel.isEmailValid()) {
      showShortToast(resources.getString(R.string.email_invalid))
      false
    }
//    else if (businessInfoModel.number.isNullOrEmpty()) {
//      showShortToast(resources.getString(R.string.phone_number_cannot_be_empty))
//      false
//    } else if (!businessInfoModel.isNumberValid()) {
//      showShortToast(resources.getString(R.string.phone_number_invalid))
//      false
//    }
    else true
  }

  override fun updateInfo() {
    requestFloatsModel?.contactInfo?.clearAllDomain()
    super.updateInfo()
  }


  private fun startAutocompleteActivity() {
    val dialog = CitySearchDialog()
    dialog.onClicked = { binding?.address?.setText(it.name) }
    dialog.show(parentFragmentManager, dialog.javaClass.name)
//        val field = listOf(Place.Field.NAME, Place.Field.ADDRESS)
//        val autocompleteIntent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, field)
//                .setTypeFilter(TypeFilter.CITIES).setCountry("IN").build(baseActivity)
//        startActivityForResult(autocompleteIntent, PLACE_SEARCH_REQUEST)
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == PLACE_SEARCH_REQUEST && resultCode == RESULT_OK) {
      val place = data?.let { Autocomplete.getPlaceFromIntent(it) }
      val placeName = String.format("%s", place?.name)
      binding?.address?.setText(placeName)
      val address = String.format("%s", place?.address)
    }
  }

  private fun parseResponse(it: BaseResponse): Boolean {
    return try {
      val source: BufferedSource? = it.responseBody?.source()
      source?.request(Long.MAX_VALUE)
      val buffer: Buffer? = source?.buffer
      val responseBodyString: String? = buffer?.clone()?.readString(Charset.forName("UTF-8"))
      responseBodyString.toBoolean()
    } catch (e: Exception) {
      false
    }
  }
}
