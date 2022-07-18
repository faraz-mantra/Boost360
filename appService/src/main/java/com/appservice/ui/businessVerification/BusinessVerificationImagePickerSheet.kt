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
import java.io.File
import java.io.FileInputStream
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
    fun newInstance(onImagePicked:(Uri)->Unit): BusinessVerificationImagePickerSheet {
      return BusinessVerificationImagePickerSheet().apply {
        this.onImagePicked = onImagePicked
      }
    }
  }

  lateinit var onImagePicked:(Uri)->Unit


  override fun onCreateView() {

    setOnClickListener(binding?.btnGallery,binding?.btbTakePicture,binding?.ivClose)
  }


  override fun onClick(v: View) {
    super.onClick(v)
    when(v){
      binding?.btnGallery->{
        ImagePicker.with(this).galleryOnly().start(RC_GALLERY)
      }
      binding?.btbTakePicture->{
        startFragmentActivity(FragmentType.CAMERA_VIEW, isResult = true, requestCode = ScanPanCardV2Fragment.RC_CODE)
      }

      binding?.ivClose->{
        dismiss()
      }
    }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if(requestCode==RC_GALLERY&&resultCode== RESULT_OK){
      data?.data?.let {
        onImagePicked.invoke(it)
        dismiss()
      }
    }
    if (requestCode==ScanPanCardV2Fragment.RC_CODE&&resultCode== RESULT_OK){
      val path:String? = data?.getStringExtra(ScanPanCardV2Fragment.IMAGE_PATH)
      path?.let {
        onImagePicked.invoke(Uri.fromFile(File(path)))
        dismiss()
      }
    }
  }

}