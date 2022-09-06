package com.appservice.ui.businessVerification

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.view.View
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.databinding.FragmentScanPanCardV2Binding
import com.framework.models.BaseViewModel
import com.framework.utils.FileUtils
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.FileCallback
import com.otaliastudios.cameraview.PictureResult
import java.io.File

class ScanPanCardV2Fragment : AppBaseFragment<FragmentScanPanCardV2Binding, BaseViewModel>() {


  override fun getLayout(): Int {
    return R.layout.fragment_scan_pan_card_v2
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }


  companion object {
    val IMAGE_PATH="IMAGE_BYTE_ARRAY_KEY"
    val RC_CODE=500
    fun newInstance(): ScanPanCardV2Fragment {
      return ScanPanCardV2Fragment()
    }
  }


  override fun onCreateView() {
    super.onCreateView()
    binding?.panCardScanCameraView?.setLifecycleOwner(viewLifecycleOwner)
    binding?.panCardScanCameraView?.addCameraListener(object : CameraListener() {
      override fun onPictureTaken(result: PictureResult) {
        super.onPictureTaken(result)
        binding?.panCardScanCameraView?.removeCameraListener(this)
        val intent = Intent()
        result.toFile(FileUtils.getTempFile("jpg")) {
          intent.putExtra(IMAGE_PATH,it?.path)
          requireActivity().setResult(RESULT_OK,intent)
          requireActivity().finish()
        }

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