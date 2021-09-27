package com.dashboard.controller.ui.profile.sheet

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.dashboard.R
import com.dashboard.constant.FragmentType
import com.dashboard.controller.startFragmentDashboardActivity
import com.dashboard.controller.ui.profile.CropProfileImageFragment
import com.dashboard.databinding.SheetImagePickerBinding
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel
import com.github.dhaval2404.imagepicker.ImagePicker
import com.github.dhaval2404.imagepicker.util.FileUtil

class ImagePickerSheet : BaseBottomSheetDialog<SheetImagePickerBinding, BaseViewModel>() {

  private val RC_CAMERA=100
  private val RC_GALLERY=100

  override fun getLayout(): Int {
    return R.layout.sheet_image_picker
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when(v){
      binding?.btnTakePhoto->{
            ImagePicker.with(this).cameraOnly().start(RC_CAMERA)
      }
      binding?.btnGallery->{
        ImagePicker.with(this).galleryOnly().start(RC_GALLERY)
      }
    }
  }
  override fun onCreateView() {
      setOnClickListener(binding?.btnTakePhoto,binding?.btnGallery)
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (resultCode== RESULT_OK&&requestCode==RC_CAMERA){

      openCropFragment(data)
    }else if (resultCode== RESULT_OK&&requestCode==RC_GALLERY){
      openCropFragment(data)
    }
  }

  private fun openCropFragment(data: Intent?) {
    val uri = data?.data!!

    val path = FileUtil.getTempFile(requireContext(),uri)?.path
    startFragmentDashboardActivity(FragmentType.FRAGMENT_USER_PROFILE_IMAGE_CROP,Bundle()
      .apply { putString(CropProfileImageFragment.BK_IMAGE_PATH,path) })
    dismiss()
  }
}