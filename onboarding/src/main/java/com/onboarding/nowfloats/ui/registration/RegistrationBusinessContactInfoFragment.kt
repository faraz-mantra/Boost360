package com.onboarding.nowfloats.ui.registration

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import com.framework.extensions.gone
import com.framework.extensions.isVisible
import com.framework.extensions.visible
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.widget.Autocomplete
import com.onboarding.nowfloats.R
import com.onboarding.nowfloats.databinding.FragmentRegistrationBusinessContactInfoBinding
import com.onboarding.nowfloats.extensions.fadeIn
import com.onboarding.nowfloats.model.registration.BusinessInfoModel
import com.onboarding.nowfloats.ui.CitySearchDialog

class RegistrationBusinessContactInfoFragment : BaseRegistrationFragment<FragmentRegistrationBusinessContactInfoBinding>() {

  private var businessInfoModel = BusinessInfoModel()
  private val PLACE_SEARCH_REQUEST = 1000

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
    placePickerApi()
    binding?.viewImage?.post {
      (binding?.viewImage?.fadeIn(500L)?.mergeWith(binding?.viewBusiness?.fadeIn(400L))
          ?.mergeWith(binding?.viewForm?.fadeIn(400L)))?.andThen(binding?.title?.fadeIn(150L)
              ?.mergeWith(binding?.subTitle?.fadeIn(150L)))?.andThen(binding?.formMain?.fadeIn(150L))
          ?.andThen(binding?.next?.fadeIn())?.subscribe()
    }
    setOnClickListener(binding?.next, binding?.address)
    binding?.number?.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
      if (hasFocus && binding?.countryCode?.visibility == GONE) {
        binding?.countryCode?.visible()
        binding?.number?.hint = ""
        binding?.number?.compoundDrawablePadding = resources.getDimensionPixelOffset(R.dimen.size_36)
      } else if (binding?.number?.text.isNullOrEmpty()) {
        binding?.countryCode?.gone()
        binding?.number?.hint = resources.getString(R.string.business_contact_number)
        binding?.number?.compoundDrawablePadding = resources.getDimensionPixelOffset(R.dimen.size_4)
      }
    }
    setSavedData()
  }

  private fun placePickerApi() {
    val apiKey = getString(R.string.places_api_key)
    if (apiKey.isEmpty()) return
    if (!Places.isInitialized()) Places.initialize(baseActivity, apiKey)
  }

  override fun setSavedData() {
    val contactInfo = this.requestFloatsModel?.contactInfo ?: return
    businessInfoModel = contactInfo
    binding?.storeName?.setText(contactInfo.businessName)
    binding?.address?.setText(contactInfo.address)
    binding?.email?.setText(contactInfo.email)
    val number = contactInfo.number ?: return
    binding?.number?.setText(number)
    binding?.countryCode?.visible()
    binding?.number?.hint = ""
    binding?.number?.compoundDrawablePadding = resources.getDimensionPixelOffset(R.dimen.size_36)
  }

  override fun onClick(v: View) {
    when (v) {
      binding?.next -> {
        requestFloatsModel?.contactInfo = businessInfoModel
        if (binding?.textBtn?.isVisible() == true && isValid()) {
          getDotProgress()?.let {
            binding?.textBtn?.visibility = GONE
            binding?.next?.addView(it)
            it.startAnimation()
            Handler().postDelayed({
              it.stopAnimation()
              it.removeAllViews()
              binding?.textBtn?.visibility = VISIBLE
              gotoBusinessWebsite()
            }, 1000)
          }
        }
      }
      binding?.address -> startAutocompleteActivity()
    }
  }

  private fun isValid(): Boolean {
    requestFloatsModel?.contactInfo?.businessName = binding?.storeName?.text?.toString()
    requestFloatsModel?.contactInfo?.address = binding?.address?.text?.toString()
    requestFloatsModel?.contactInfo?.email = binding?.email?.text?.toString()
    businessInfoModel.number = binding?.number?.text?.toString()

    return if (businessInfoModel.businessName.isNullOrBlank()) {
      showShortToast(resources.getString(R.string.business_cant_empty))
      false
    } else if (businessInfoModel.address.isNullOrBlank()) {
      showShortToast(resources.getString(R.string.business_address_cant_empty))
      false
    } else if (!businessInfoModel.isEmailValid()) {
      showShortToast(resources.getString(R.string.email_invalid))
      false
    } else if (!businessInfoModel.isNumberValid()) {
      showShortToast(resources.getString(R.string.phone_number_invalid))
      false
    } else true
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
}