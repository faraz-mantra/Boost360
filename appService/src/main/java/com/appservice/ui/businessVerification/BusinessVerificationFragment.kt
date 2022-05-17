package com.appservice.ui.businessVerification

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.databinding.FragmentBusinessVerificationBinding
import com.appservice.model.GSTDetails
import com.appservice.model.PanDetails
import com.appservice.model.PanGstUpdateBody
import com.appservice.viewmodel.BusinessVerificationViewModel
import com.bumptech.glide.Glide
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.framework.pref.clientId
import com.framework.utils.FileUtils.getFileName
import com.framework.utils.toBase64

class BusinessVerificationFragment :
  AppBaseFragment<FragmentBusinessVerificationBinding, BusinessVerificationViewModel>() {


  val RC_IMAGE_PCIKER=100
  var panImgUri:Uri?=null

  override fun getLayout(): Int {
    return R.layout.fragment_business_verification
  }

  override fun getViewModelClass(): Class<BusinessVerificationViewModel> {
    return BusinessVerificationViewModel::class.java
  }

  companion object {
    fun newInstance(): BusinessVerificationFragment {
      return BusinessVerificationFragment()
    }
  }

  override fun onCreateView() {
    super.onCreateView()
    setOnClickListener(binding?.uploadImageView,
      binding?.btnRetakePanImage,binding?.btnSubmit,
    binding?.rGst,binding?.rNotRegisterGst,binding?.rcmWhat)
    viewListeners()
  }

  private fun viewListeners() {

  }


  override fun onClick(v: View) {
    super.onClick(v)
    when(v){
      binding?.rGst->{
        uiEnabledGst()
      }
      binding?.rNotRegisterGst->{
        uiDisabledGst()
      }
      binding?.uploadImageView->{
        showImagePicker()
      }

      binding?.btnRetakePanImage->{
        showImagePicker()
      }
      binding?.btnSubmit->{

        submitVerificationData()
      }
      binding?.rcmWhat->{
        RcmHelpSheet.newInstance().show(parentFragmentManager,RcmHelpSheet::class.java.name)
      }
    }
  }

  private fun uiDisabledGst() {
    binding?.layoutGstDetail?.isVisible=false
    binding?.ckbGstNotReg?.isVisible=true
    binding?.rGst?.isChecked = false
  }

  private fun uiEnabledGst() {
    binding?.layoutGstDetail?.isVisible=true
    binding?.ckbGstNotReg?.isVisible=false
    binding?.rNotRegisterGst?.isChecked = false
  }

  private fun submitVerificationData() {
    val pan = binding?.edtPanNumber?.text?.toString()
    val panName = binding?.edtNameOnPanCard?.text?.toString()
    val isRegGst = if (binding?.rGst?.isChecked==true) true else false
    val gstin = binding?.edtGstinName?.text?.toString()
    val rcm = binding?.ckbRcmSales?.isChecked==true && isRegGst
    val panImgBase64 = panImgUri?.toBase64()

    if (panImgBase64.isNullOrEmpty()){
      showLongToast(getString(R.string.upload_pan_card_image))
      return
    }
    if (pan.isNullOrEmpty()){
      binding?.edtPanNumber?.error = getString(R.string.mandatory_field)
      return
    }
    if (panName.isNullOrEmpty()){
      binding?.edtNameOnPanCard?.error = getString(R.string.mandatory_field)
      return
    }

    if (isRegGst&&gstin.isNullOrEmpty()){
      binding?.edtGstinName?.error = getString(R.string.mandatory_field)
      return
    }

    val bundle = Bundle().apply {
      putParcelable(ConfirmBusinessVerificationSheet.IMG_URI,panImgUri)
      putString(ConfirmBusinessVerificationSheet.PAN,pan)
      putString(ConfirmBusinessVerificationSheet.PAN_NAME,panName)
      putString(ConfirmBusinessVerificationSheet.GST,gstin)
      putBoolean(ConfirmBusinessVerificationSheet.RCM,rcm)

    }


    ConfirmBusinessVerificationSheet.newInstance(bundle){
     callPanGstUpdateApi(isRegGst,panImgBase64,panName,pan,gstin,rcm)
    }.show(parentFragmentManager,ConfirmBusinessVerificationSheet::class.java.name)




  }

  private fun callPanGstUpdateApi(
    isRegGst: Boolean,
    panImgBase64: String,
    panName: String,
    pan: String,
    gstin: String?,
    rcm: Boolean
  ) {

    val fileName = panImgUri?.getFileName(true)
    val fileExt = if (fileName?.lastIndexOf(".")?:-1>=0) fileName?.substring(
      fileName.lastIndexOf(".")+1,fileName.length) else null

    if (fileExt==null){
      showLongToast(getString(R.string.unsupported_file))
      return
    }

    val panGstUpdateBody = PanGstUpdateBody(clientId,sessionLocal.fPID!!,
      GSTDetails(sessionLocal.fpTag,isRegGst,null,null,null,gstin,rcm),
      PanDetails(panImgBase64,"pan",fileExt,panName,pan)
    )

    showProgress()

    viewModel?.panGstUpdate(panGstUpdateBody)?.observe(viewLifecycleOwner){
      hideProgress()
      if (it.isSuccess()){
        BusinessVerificationUnderwaySheet.newInstance{
          activity?.finish()
        }
          .show(parentFragmentManager,BusinessVerificationUnderwaySheet::class.java.name)
      }else{
        showLongToast(getString(R.string.something_went_wrong))
      }
    }

  }


  private fun showImagePicker() {
    BusinessVerificationImagePickerSheet.newInstance {
      binding?.uploadImageView?.gone()
      binding?.imageView?.visible()
      Glide.with(this).load(it).into(binding?.imagePanCard!!)
      panImgUri= it
    }.show(parentFragmentManager,
      BusinessVerificationImagePickerSheet::class.java.name)
  }


}