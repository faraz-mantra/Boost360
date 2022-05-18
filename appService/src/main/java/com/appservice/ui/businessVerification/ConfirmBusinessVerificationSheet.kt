package com.appservice.ui.businessVerification

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import com.appservice.R
import com.appservice.databinding.SheetConfirmBusinessVerificationBinding
import com.bumptech.glide.Glide
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel

class ConfirmBusinessVerificationSheet : BaseBottomSheetDialog<SheetConfirmBusinessVerificationBinding, BaseViewModel>() {

  val imgUri by lazy {
    arguments?.getParcelable(IMG_URI) as Uri?
  }

  val pan by lazy {
    arguments?.getString(PAN)
  }
  val panName by lazy {
    arguments?.getString(PAN_NAME)
  }
  val gst by lazy {
    arguments?.getString(GST)
  }
  val rcm by lazy {
    arguments?.getBoolean(RCM)
  }

  lateinit var onSubmitClick:(Unit)->Unit
  override fun getLayout(): Int {
    return R.layout.sheet_confirm_business_verification
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  companion object{
    val IMG_URI="IMG_URI"
    val PAN="PAN"
    val PAN_NAME="PAN_NAME"
    val GST="GST"
    val RCM="RCM"

    fun newInstance(bundle: Bundle,onSubmitClickParam:(Unit)->Unit):ConfirmBusinessVerificationSheet{
      return ConfirmBusinessVerificationSheet().apply {
        arguments=bundle
        onSubmitClick=onSubmitClickParam
      }
    }
  }
  override fun onCreateView() {
    initUi()
    setOnClickListener(binding?.btnSubmitSubmit,binding?.ivClose)
  }

  private fun initUi() {
    Glide.with(this).load(imgUri).into(binding?.ivPanCardImage!!)
    binding?.headingPanName?.text = panName
    binding?.headingPanNumber?.text = pan
    if (gst.isNullOrEmpty()){
      binding?.gstDetail?.isAllCaps = false
      binding?.gstDetail?.text = getString(R.string.not_available)
      binding?.rcmDetail?.isVisible=false
    }else{
      binding?.gstDetail?.isAllCaps = true
      binding?.gstDetail?.text = gst
      binding?.rcmDetail?.isVisible= rcm == true
    }


  }

  override fun onClick(v: View) {
    super.onClick(v)
    when(v){
      binding?.btnSubmitSubmit->{
        onSubmitClick.invoke(Unit)
        dismiss()
      }
      binding?.ivClose->{
        dismiss()
      }
    }
  }
}