package com.appservice.ui.businessVerification

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.databinding.FragmentBusinessVerificationBinding
import com.appservice.model.panGst.*
import com.appservice.utils.WebEngageController
import com.appservice.viewmodel.BusinessVerificationViewModel
import com.bumptech.glide.Glide
import com.framework.extensions.gone
import com.framework.extensions.observeOnce
import com.framework.extensions.visible
import com.framework.glide.util.glideLoad
import com.framework.pref.clientId
import com.framework.utils.FileUtils.getFileName
import com.framework.utils.ValidationUtils
import com.framework.utils.toBase64
import com.framework.webengageconstant.*

class BusinessVerificationFragment : AppBaseFragment<FragmentBusinessVerificationBinding, BusinessVerificationViewModel>() {

  var panImgUri: Uri? = null
  var isUpdate: Boolean = false
  var result: PanGstResult? = null

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
    WebEngageController.trackEvent(ECOM_BUSINESS_VERIFICATION_LOAD, PAGE_VIEW, NO_EVENT_VALUE)
    setOnClickListener(
      binding?.uploadImageView,
      binding?.btnRetakePanImage, binding?.btnSubmit,
      binding?.rGst, binding?.rNotRegisterGst, binding?.rcmWhat
    )
    getGstPanDetail()
  }

  private fun getGstPanDetail() {
    showProgress()
    viewModel?.getPanGstDetail(sessionLocal.fPID, clientId)?.observeOnce(viewLifecycleOwner) {
      val response = it as? PanGstDetailResponse
      if (response?.isSuccess() == true && response.result != null) setDetails(response.result!!)
      hideProgress()
    }
  }

  private fun setDetails(result: PanGstResult) {
    this.result = result
    if (result.panDetails?.imageLink.isNullOrEmpty().not()) {
      isUpdate = true
      binding?.uploadImageView?.gone()
      binding?.imageView?.visible()
      apply { baseActivity.glideLoad(binding?.imagePanCard!!, result.panDetails?.imageLink ?: "", R.drawable.placeholder_image_n) }
    } else {
      binding?.uploadImageView?.visible()
      binding?.imageView?.gone()
    }
    binding?.edtPanNumber?.setText(result.panDetails?.number ?: "")
    binding?.edtNameOnPanCard?.setText(result.panDetails?.name ?: "")
    if (result.gstDetails?.businessRegister == true) uiEnabledGst() else uiDisabledGst()
    binding?.edtGstinName?.setText(result.gstDetails?.gstIn ?: "")
    binding?.ckbRcmSales?.isChecked = result.gstDetails?.rcmApply ?: false
  }


  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.rGst -> {
        uiEnabledGst()
      }
      binding?.rNotRegisterGst -> {
        uiDisabledGst()
      }
      binding?.uploadImageView -> {
        showImagePicker()
      }

      binding?.btnRetakePanImage -> {
        showImagePicker()
      }
      binding?.btnSubmit -> {
        submitVerificationData()
      }
      binding?.rcmWhat -> {
        RcmHelpSheet.newInstance().show(parentFragmentManager, RcmHelpSheet::class.java.name)
      }
    }
  }

  private fun uiDisabledGst() {
    binding?.layoutGstDetail?.isVisible = false
    binding?.ckbGstNotReg?.isVisible = true
    binding?.rGst?.isChecked = false
    binding?.rNotRegisterGst?.isChecked = true
  }

  private fun uiEnabledGst() {
    binding?.layoutGstDetail?.isVisible = true
    binding?.ckbGstNotReg?.isVisible = false
    binding?.rGst?.isChecked = true
    binding?.rNotRegisterGst?.isChecked = false
  }

  private fun submitVerificationData() {
    val pan = binding?.edtPanNumber?.text?.toString()
    val panName = binding?.edtNameOnPanCard?.text?.toString()
    val isRegGst = binding?.rGst?.isChecked == true
    val gstin = binding?.edtGstinName?.text?.toString()
    val rcm = binding?.ckbRcmSales?.isChecked == true && isRegGst
    val panImgBase64 = panImgUri?.toBase64()

    if (panImgBase64.isNullOrEmpty() && isUpdate.not()) {
      showLongToast(getString(R.string.upload_pan_card_image))
      return
    }
    if (pan.isNullOrEmpty()) {
      binding?.edtPanNumber?.error = getString(R.string.mandatory_field)
      return
    }
    if (ValidationUtils.isPanNumberValid(pan).not()) {
      binding?.edtPanNumber?.error = getString(R.string.enter_valid_pan_num)
      return
    }
    if (panName.isNullOrEmpty()) {
      binding?.edtNameOnPanCard?.error = getString(R.string.mandatory_field)
      return
    }
    if (isRegGst && gstin.isNullOrEmpty()) {
      binding?.edtGstinName?.error = getString(R.string.mandatory_field)
      return
    }
    if (isRegGst && ValidationUtils.isGstValid(gstin).not()) {
      binding?.edtGstinName?.error = getString(R.string.enter_valid_gstin_number)
      return
    }

    val bundle = Bundle().apply {
      putParcelable(ConfirmBusinessVerificationSheet.IMG_URI, panImgUri)
      putString(ConfirmBusinessVerificationSheet.PAN, pan)
      putSerializable(ConfirmBusinessVerificationSheet.RESULT_DATA, result)
      putString(ConfirmBusinessVerificationSheet.PAN_NAME, panName)
      putString(ConfirmBusinessVerificationSheet.GST, gstin)
      putBoolean(ConfirmBusinessVerificationSheet.RCM, rcm)
    }

    ConfirmBusinessVerificationSheet.newInstance(bundle) {
      callPanGstUpdateApi(isRegGst, panImgBase64, panName, pan, gstin, rcm)
    }.show(parentFragmentManager, ConfirmBusinessVerificationSheet::class.java.name)
  }

  private fun callPanGstUpdateApi(isRegGst: Boolean, panImgBase64: String?, panName: String, pan: String, gstin: String?, rcm: Boolean) {
    var fileExt: String? = null
    if (panImgUri != null) {
      val fileName = panImgUri?.getFileName(true)
      fileExt = if (fileName?.lastIndexOf(".") ?: -1 >= 0) fileName?.substring(fileName.lastIndexOf(".") + 1, fileName.length) else null
      if (fileExt == null) {
        showLongToast(getString(R.string.unsupported_file))
        return
      }
    }
    val panGstUpdateBody = PanGstUpdateBody(
      clientId, sessionLocal.fPID!!,
      GSTDetailsRequest(sessionLocal.fpTag, isRegGst, null, null, null, gstin, rcm),
      PanDetailsRequest(panImgBase64, "pan", fileExt, panName, pan)
    )
    showProgress()
    viewModel?.panGstUpdate(panGstUpdateBody)?.observe(viewLifecycleOwner) {
      if (it.isSuccess()) {
        WebEngageController.trackEvent(ECOM_BUSINESS_VERIFICATION_UPDATE, PAGE_VIEW, NO_EVENT_VALUE)
        BusinessVerificationUnderwaySheet.newInstance { activity?.finish() }.show(parentFragmentManager, BusinessVerificationUnderwaySheet::class.java.name)
      } else showLongToast(getString(R.string.something_went_wrong))
      hideProgress()
    }
  }

  private fun showImagePicker() {
    BusinessVerificationImagePickerSheet.newInstance {
      binding?.uploadImageView?.gone()
      binding?.imageView?.visible()
      Glide.with(this).load(it).into(binding?.imagePanCard!!)
      panImgUri = it
    }.show(parentFragmentManager, BusinessVerificationImagePickerSheet::class.java.name)
  }
}