package com.appservice.ui.businessVerification

import android.content.Intent
import android.view.View
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.databinding.FragmentScanPanCardV2Binding
import com.framework.models.BaseViewModel
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.PictureResult

class ScanPanCardV2Fragment : AppBaseFragment<FragmentScanPanCardV2Binding, BaseViewModel>() {


  override fun getLayout(): Int {
    return R.layout.fragment_scan_pan_card_v2
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }


  companion object {
    val IMAGE_BYTE_ARRAY_KEY="IMAGE_BYTE_ARRAY_KEY"
    val RC_CODE=500
    fun newInstance(): ScanPanCardV2Fragment {
      return ScanPanCardV2Fragment()
    }
  }


  override fun onCreateView() {
    super.onCreateView()
    binding?.panCardScanCameraView?.addCameraListener(object : CameraListener() {
      override fun onPictureTaken(result: PictureResult) {
        super.onPictureTaken(result)
        val intent = Intent()
        intent.putExtra(IMAGE_BYTE_ARRAY_KEY,result.data)
        activity?.setResult(RC_CODE,intent)
        activity?.finish()
      }
    })
    setOnClickListener(binding?.btnClickPhoto)
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when(v){
      binding?.btnClickPhoto->{
        binding?.panCardScanCameraView?.takePicture()
      }
    }
  }

}