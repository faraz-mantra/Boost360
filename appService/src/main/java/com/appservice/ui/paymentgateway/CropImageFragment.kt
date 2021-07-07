package com.appservice.ui.paymentgateway

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.constant.FragmentType
import com.appservice.constant.IntentConstant
import com.appservice.databinding.FragmentImageCropBinding
import com.appservice.model.SessionData
import com.framework.models.BaseViewModel
import com.theartofdev.edmodo.cropper.CropImageView
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException

class CropImageFragment : AppBaseFragment<FragmentImageCropBinding, BaseViewModel>(),
  CropImageView.OnSetImageUriCompleteListener, CropImageView.OnCropImageCompleteListener {

  private var croppedImageFile: File = File(Environment.getExternalStorageDirectory(), "photo.jpg")
  private var session: SessionData? = null
  private var isEdit: Boolean = false

  companion object {
    @JvmStatic
    fun newInstance(bundle: Bundle? = null): CropImageFragment {
      val fragment = CropImageFragment()
      fragment.arguments = bundle
      return fragment
    }
  }

  override fun getLayout(): Int {
    return R.layout.fragment_image_crop
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun onCreateView() {
    super.onCreateView()
    isEdit = arguments?.getBoolean(IntentConstant.IS_EDIT.name) ?: false
    session = arguments?.getSerializable(IntentConstant.SESSION_DATA.name) as? SessionData
    if (session == null) return
    setOnClickListener(binding?.btnCropDone)
    binding?.cropImageView?.setOnCropImageCompleteListener(this)
    if (arguments?.getString(IntentConstant.PAN_CARD_IMAGE.name) != null) {
      val panImageUriString = arguments?.getString(IntentConstant.PAN_CARD_IMAGE.name)
      val panImageUri = Uri.parse(panImageUriString)
      binding?.cropImageView?.setImageUriAsync(panImageUri)
    } else {
      val cacheDir = context?.cacheDir
      val file = File(cacheDir, "image")
      var fileInputStream: FileInputStream? = null
      try {
        fileInputStream = FileInputStream(file)
      } catch (e: FileNotFoundException) {
        e.printStackTrace()
      }
      val panImageBitmap: Bitmap? = BitmapFactory.decodeStream(fileInputStream)
      binding?.cropImageView?.setImageBitmap(panImageBitmap)
    }
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.btnCropDone -> binding?.cropImageView?.saveCroppedImageAsync(
        Uri.fromFile(
          croppedImageFile
        )
      )
    }
  }

  override fun onSetImageUriComplete(view: CropImageView?, uri: Uri?, error: Exception?) {
  }

  override fun onCropImageComplete(view: CropImageView?, result: CropImageView.CropResult?) {
    handleCropResult(view, result)
  }

  private fun handleCropResult(view: CropImageView?, result: CropImageView.CropResult?) {
    if (result?.error == null) {
      if (isEdit) {
        val output = Intent()
        output.putExtra(IntentConstant.PAN_CARD_IMAGE.name, result?.uri.toString())
        baseActivity.setResult(AppCompatActivity.RESULT_OK, output)
        baseActivity.finish()
      } else {
        Log.d("Cropped Image", result?.toString() ?: "")
        if (result?.uri != null) {
          Log.d("Cropped Image URI", result.uri.toString())
          val bundle = Bundle()
          bundle.putBoolean(
            "isInstaMojoAccount",
            arguments?.getBoolean("isInstaMojoAccount") ?: false
          )
          bundle.putSerializable(IntentConstant.SESSION_DATA.name, session)
          bundle.putString(IntentConstant.PAN_CARD_IMAGE.name, result.uri.toString())
          startFragmentPaymentActivity(FragmentType.KYC_DETAILS, bundle)
        }
      }

    } else result.error.printStackTrace()
  }

}