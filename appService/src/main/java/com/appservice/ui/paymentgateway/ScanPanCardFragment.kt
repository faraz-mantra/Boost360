package com.appservice.ui.paymentgateway


import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.constant.FragmentType
import com.appservice.constant.IntentConstant
import com.appservice.databinding.FragmentScanPanCardBinding
import com.appservice.model.SessionData
import com.framework.models.BaseViewModel
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.CameraView
import com.otaliastudios.cameraview.PictureResult
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException


class ScanPanCardFragment : AppBaseFragment<FragmentScanPanCardBinding, BaseViewModel>() {

  private var camera: CameraView? = null
  private var CAMERA_PERMISSIONS_CODE = 100
  private var STORAGE_PERMISSIONS_CODE = 101
  private var IMAGE_PICK_CODE = 102
  private var session: SessionData? = null
  private var isEdit: Boolean = false

  companion object {
    @JvmStatic
    fun newInstance(bundle: Bundle? = null): ScanPanCardFragment {
      val fragment = ScanPanCardFragment()
      fragment.arguments = bundle
      return fragment
    }
  }

  override fun onCreateView() {
    super.onCreateView()
    isEdit = arguments?.getBoolean(IntentConstant.IS_EDIT.name) ?: false
    session = arguments?.getSerializable(IntentConstant.SESSION_DATA.name) as? SessionData
    if (session == null) return
    checkAndAskPermissions()
    camera = binding?.panCardScanCameraView
    camera?.setLifecycleOwner(this)
    camera?.addCameraListener(object : CameraListener() {
      @SuppressLint("WrongThread")
      override fun onPictureTaken(result: PictureResult) {
        super.onPictureTaken(result)
        val cacheDir: File? = context?.cacheDir
        val file: File? = File(cacheDir, "image")
        val bitmapImage = BitmapFactory.decodeByteArray(result.data, 0, result.data.size)
        try {
          val outputStream = FileOutputStream(file)
          bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
          outputStream.flush()
          outputStream.close()
          val bundle = Bundle()
          bundle.putBoolean("isInstaMojoAccount", arguments?.getBoolean("isInstaMojoAccount") ?: false)
          bundle.putSerializable(IntentConstant.SESSION_DATA.name, session)
          bundle.putBoolean(IntentConstant.IS_EDIT.name, isEdit)
          startFragmentPaymentActivity(FragmentType.CROP_IMAGE, bundle, isResult = isEdit)
        } catch (e: FileNotFoundException) {
          e.printStackTrace()
        } catch (e: IOException) {
          e.printStackTrace()
        }
      }
    })
    setOnClickListener(binding?.btnUploadPanGallery, binding?.btnClickPhoto)
  }

  override fun getLayout(): Int {
    return R.layout.fragment_scan_pan_card
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.btnClickPhoto -> camera?.takePicture()
      binding?.btnUploadPanGallery -> openImagePicker()
    }
  }

  private fun checkAndAskPermissions() {
    if (ActivityCompat.checkSelfPermission(baseActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
        ActivityCompat.checkSelfPermission(baseActivity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
      requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), STORAGE_PERMISSIONS_CODE)
    }
  }

  private fun openImagePicker() {
    if (ActivityCompat.checkSelfPermission(baseActivity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
      requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), STORAGE_PERMISSIONS_CODE)
    } else startImagePickerIntent()
  }

  private fun startImagePickerIntent() {
    val intent = Intent(Intent.ACTION_PICK)
    intent.type = "image/*"
    val mimeTypes = arrayOf("image/jpeg", "image/png")
    intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
    startActivityForResult(intent, IMAGE_PICK_CODE)
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (resultCode == AppCompatActivity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
      val bundle = Bundle()
      bundle.putBoolean("isInstaMojoAccount", arguments?.getBoolean("isInstaMojoAccount") ?: false)
      bundle.putSerializable(IntentConstant.SESSION_DATA.name, session)
      bundle.putString(IntentConstant.PAN_CARD_IMAGE.name, data?.data?.toString())
      bundle.putBoolean(IntentConstant.IS_EDIT.name, isEdit)
      startFragmentPaymentActivity(FragmentType.CROP_IMAGE, bundle, isResult = isEdit)
    } else if (resultCode == AppCompatActivity.RESULT_OK && requestCode == 101) {
      val output = Intent()
      output.putExtra(IntentConstant.PAN_CARD_IMAGE.name, data?.getStringExtra(IntentConstant.PAN_CARD_IMAGE.name))
      baseActivity.setResult(AppCompatActivity.RESULT_OK, output)
      baseActivity.finish()
    }
  }

  override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    when (requestCode) {
      CAMERA_PERMISSIONS_CODE -> {
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          Log.d("CAMERA PERMISSION", "PERMISSION GRANTED")
        } else {
          Toast.makeText(context, getString(R.string.please_provide_the_camera_permission_to_proceed), Toast.LENGTH_LONG).show()
        }
      }
      STORAGE_PERMISSIONS_CODE -> {
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          Log.d(getString(R.string.storage_permission), "PERMISSION GRANTED")
          //startImagePickerIntent()
        } else {
          Toast.makeText(context, getString(R.string.please_provide_the_storage_permissions_to_proceed), Toast.LENGTH_LONG).show()
          checkAndAskPermissions()
        }
      }
    }
  }

}


