package com.appservice.ui.businessVerification

import android.content.Intent
import android.net.Uri
import android.view.View
import com.appservice.R
import com.appservice.constant.FragmentType
import com.appservice.databinding.SheetBusinessVerificationImagePickerBinding
import com.appservice.databinding.SheetConfirmBusinessVerificationBinding
import com.appservice.ui.catalog.startFragmentActivity
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel
import com.github.dhaval2404.imagepicker.ImagePicker
import java.io.ByteArrayInputStream
import java.io.InputStream

class BusinessVerificationImagePickerSheet : BaseBottomSheetDialog<SheetBusinessVerificationImagePickerBinding, BaseViewModel>() {

  val RC_GALLERY=100

  override fun getLayout(): Int {
    return R.layout.sheet_business_verification_image_picker
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  companion object {
    fun newInstance(onImagePicked:(InputStream)->Unit): BusinessVerificationImagePickerSheet {
      return BusinessVerificationImagePickerSheet().apply {
        this.onImagePicked = onImagePicked
      }
    }
  }

  lateinit var onImagePicked:(InputStream)->Unit


  override fun onCreateView() {

    setOnClickListener(binding?.btnGallery,binding?.btbTakePicture)
  }


  override fun onClick(v: View) {
    super.onClick(v)
    when(v){
      binding?.btnGallery->{
        ImagePicker.with(this).galleryOnly().start(RC_GALLERY)
      }
      binding?.btbTakePicture->{
        startFragmentActivity(FragmentType.CAMERA_VIEW, requestCode = ScanPanCardV2Fragment.RC_CODE)
      }
    }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if(requestCode==RC_GALLERY&&resultCode== RESULT_OK){
      data?.data?.let {
        activity?.contentResolver?.openInputStream(it)?.let {
            it1 -> onImagePicked.invoke(it1)
        }

      }
    }
    if (requestCode==ScanPanCardV2Fragment.RC_CODE&&resultCode== RESULT_OK){
      val bArr = data?.getByteArrayExtra(ScanPanCardV2Fragment.IMAGE_BYTE_ARRAY_KEY)
      val inputStream = ByteArrayInputStream(bArr)
      onImagePicked.invoke(inputStream)
    }
  }

}