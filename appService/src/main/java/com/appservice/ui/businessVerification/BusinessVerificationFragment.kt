package com.appservice.ui.businessVerification

import android.content.Intent
import android.graphics.BitmapFactory
import android.view.View
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.databinding.FragmentBusinessVerificationBinding
import com.appservice.ui.aptsetting.ui.FragmentAddAccountDetails
import com.bumptech.glide.Glide
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.framework.models.BaseViewModel
import com.squareup.picasso.Picasso

class BusinessVerificationFragment : AppBaseFragment<FragmentBusinessVerificationBinding, BaseViewModel>() {


  val RC_IMAGE_PCIKER=100
  override fun getLayout(): Int {
    return R.layout.fragment_business_verification
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  companion object {
    fun newInstance(): BusinessVerificationFragment {
      return BusinessVerificationFragment()
    }
  }

  override fun onCreateView() {
    super.onCreateView()
    setOnClickListener(binding?.uploadImageView,binding?.btnRetakePanImage,binding?.btnSubmit)
  }


  override fun onClick(v: View) {
    super.onClick(v)
    when(v){
      binding?.uploadImageView->{
        showImagePicker()
      }

      binding?.btnRetakePanImage->{
        showImagePicker()
      }
      binding?.btnSubmit->{
        submitVerificationData()
      }
    }
  }

  private fun submitVerificationData() {
    val email = binding?.edtPanNumber?.text?.toString()
    val pancard = binding?.edtNameOnPanCard?.text?.toString()
    val isRegGst = if (binding?.rGst?.isChecked==true) true else false


  }

  private fun showImagePicker() {
    BusinessVerificationImagePickerSheet.newInstance {
      binding?.uploadImageView?.gone()
      binding?.imageView?.visible()
      Glide.with(this).load(it).into(binding?.imagePanCard!!)
    }.show(parentFragmentManager,
      BusinessVerificationImagePickerSheet::class.java.name)
  }


}