package com.festive.poster.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.festive.poster.R
import com.festive.poster.base.AppBaseBottomSheetFragment
import com.festive.poster.databinding.SheetPosterPaymentBinding
import com.festive.poster.databinding.SheetPosterPaymentv2Binding
import com.festive.poster.utils.WebEngageController
import com.festive.poster.viewmodels.FestivePosterSharedViewModel
import com.festive.poster.viewmodels.FestivePosterViewModel
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel
import com.framework.pref.Key_Preferences
import com.framework.pref.UserSessionManager
import com.framework.utils.convertListObjToString
import com.framework.utils.convertStringToList
import com.framework.webengageconstant.FESTIVAL_POSTER_CONFIRM_ORDER_CLICK
import com.framework.webengageconstant.FESTIVAL_POSTER_PAY_LATER_SCREEN

class PosterPaymentSheetV2 : AppBaseBottomSheetFragment<SheetPosterPaymentv2Binding, FestivePosterViewModel>() {

  private var sharedViewModel: FestivePosterSharedViewModel? = null
  private var session: UserSessionManager? = null

  companion object {
    @JvmStatic
    fun newInstance(): PosterPaymentSheetV2 {
      val bundle = Bundle().apply {
      }
      val fragment = PosterPaymentSheetV2()
      fragment.arguments = bundle
      return fragment
    }
  }

  override fun getLayout(): Int {
    return R.layout.sheet_poster_paymentv2
  }

  override fun getViewModelClass(): Class<FestivePosterViewModel> {
    return FestivePosterViewModel::class.java
  }

  override fun onCreateView() {
    sharedViewModel = ViewModelProvider(requireActivity()).get(FestivePosterSharedViewModel::class.java)
    session = UserSessionManager(baseActivity)
    setupUi()
    WebEngageController.trackEvent(FESTIVAL_POSTER_PAY_LATER_SCREEN, event_value = HashMap())
    setOnClickListener(binding?.btnConfirm,binding?.rivCloseBottomSheet)
  }

  private fun setupUi() {
    val posterPack = sharedViewModel?.selectedPosterPack
    posterPack?.let {
      binding?.tvPrice?.text = it.price.toInt().toString()
      binding?.tvPackName?.text = getString(R.string.for_pack_of_posters, it.tagsModel.name)
      binding?.tvPayLaterPrice?.text = getString(R.string.get_the_poster_pack_now_amp_pay_later, it.price.toInt().toString())
      binding?.tvPackSize?.text = getString(R.string.of_size_posters,it.posterList?.size.toString())
      val number = when {
        session?.userPrimaryMobile.isNullOrEmpty().not() -> session?.userPrimaryMobile
        session?.userProfileMobile.isNullOrEmpty().not() -> session?.userProfileMobile
        else -> ""
      }
      val email = if (session?.userProfileEmail.isNullOrEmpty()) "" else session?.userProfileEmail
      binding?.tvDesc?.text = "A day after the order is confirmed, we’ll send you a secure payment link on your registered email ID" +
              " ${if (email.isNullOrEmpty()) number else "$email & $number"} to make the payment for ₹${it.price.toInt()} (including taxes)."
    }
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.btnConfirm -> {
        val label = HashMap<String, Any>()
        label["feature_code"] = sharedViewModel?.selectedPosterPack?.tagsModel?.tag ?: ""
        label["price"] = sharedViewModel?.selectedPosterPack?.price ?: 0.0
        label["quantity"] = "1"
        WebEngageController.trackEvent(FESTIVAL_POSTER_CONFIRM_ORDER_CLICK, event_value = label)
        storePurchasedTag(sharedViewModel?.selectedPosterPack?.tagsModel?.tag ?: "")
        updatePurchase()
      }
      binding?.rivCloseBottomSheet->{
        dismiss()
      }
    }
  }

  fun updatePurchase(){
    showProgress()
    val templateIds=ArrayList<String>()
    sharedViewModel?.selectedPosterPack?.posterList?.forEach {
      it.id?.let { it1 -> templateIds.add(it1) }
    }

    viewModel?.updatePurchaseStatus(session?.fPID,
      session?.fpTag,sharedViewModel?.selectedPosterPack?.tagsModel?.tag,
      templateIds)?.observe(viewLifecycleOwner,{
        if (it.isSuccess()){
          PosterOrderConfirmSheet().show(parentFragmentManager, PosterOrderConfirmSheet::class.java.name)
          dismiss()
        }
      hideProgress()
    })
  }

  private fun storePurchasedTag(tag: String) {
    val oldTags: ArrayList<String> = ArrayList(convertStringToList(session?.getFPDetails(Key_Preferences.FESTIVE_POSTER_PURCHASED_TAG) ?: "") ?: arrayListOf())
    oldTags.add(tag)
    session?.storeFPDetails(Key_Preferences.FESTIVE_POSTER_PURCHASED_TAG, convertListObjToString(HashSet(oldTags).toList()))
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
  }
}