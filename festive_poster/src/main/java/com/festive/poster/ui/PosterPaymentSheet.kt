package com.festive.poster.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.festive.poster.R
import com.festive.poster.databinding.BsheetCustomizePosterBinding
import com.festive.poster.databinding.SheetPosterPaymentBinding
import com.festive.poster.models.PosterCustomizationModel
import com.festive.poster.utils.WebEngageController
import com.festive.poster.viewmodels.FestivePosterSharedViewModel
import com.framework.base.BaseBottomSheetDialog
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.framework.models.BaseViewModel
import com.framework.pref.UserSessionManager
import com.framework.webengageconstant.FESTIVAL_POSTER_PAY_LATER_SCREEN
import com.github.dhaval2404.imagepicker.ImagePicker
import com.github.dhaval2404.imagepicker.util.FileUtil

class PosterPaymentSheet: BaseBottomSheetDialog<SheetPosterPaymentBinding, BaseViewModel>() {


    private var sharedViewModel:FestivePosterSharedViewModel?=null
    private var session:UserSessionManager?=null

    companion object{
        @JvmStatic
        fun newInstance(): PosterPaymentSheet {
            val bundle = Bundle().apply {
            }
            val fragment =PosterPaymentSheet()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getLayout(): Int {
        return R.layout.sheet_poster_payment
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {
        sharedViewModel = ViewModelProvider(requireActivity()).get(FestivePosterSharedViewModel::class.java)
        session = UserSessionManager(requireActivity())
        setupUi()
        WebEngageController.trackEvent(FESTIVAL_POSTER_PAY_LATER_SCREEN)
        setOnClickListener(binding?.btnConfirm)
    }

    private fun setupUi() {
        val posterPack = sharedViewModel?.selectedPosterPack
        posterPack?.let {
            binding?.tvHeading?.text=getString(R.string.paying_,it.price.toString())
            binding?.tvSubheading?.text=getString(R.string.for_pack_of_posters,it.posterList?.size.toString())
            binding?.tvPayLater?.text =getString(R.string.get_the_poster_pack_now_amp_pay_later,it.price.toString())

            if (session?.userProfileEmail.isNullOrEmpty()){
                binding?.tvPayLaterMsg?.text = "We’ll send you a secure payment" +
                        " link on your registered mobile ${session?.userProfileMobile} to make the payment for ₹${it.price}."
            }else{
                binding?.tvPayLaterMsg?.text =  "We’ll send you a secure payment" +
                        " link on your registered email ID ${session?.userProfileEmail} & ${session?.userProfileMobile} to make the payment for ₹${it.price}."
            }


        }
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when(v){
            binding?.btnConfirm->{
                PosterOrderConfirmSheet().show(parentFragmentManager,PosterOrderConfirmSheet::class.java.name)
                dismiss()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

    }


}