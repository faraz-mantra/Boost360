package com.framework.utils

import android.app.Activity
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.framework.BaseApplication
import com.framework.R
import com.framework.constants.PermissionConstants.STORAGE_PERMISSION
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object ImagePickerUtil {

  private var galleryPickerLauncher: ActivityResultLauncher<Intent>? = null
  private var storagePermGalleryPickerLauncher: ActivityResultLauncher<String>? = null

  interface Listener {
    fun onFilePicked(filePath: String)
  }

  var listener: Listener? = null

  fun openPicker(activity: AppCompatActivity, listener: Listener?) {
    this.listener = listener
    if (activity.checkPermission(STORAGE_PERMISSION)) {
      launchPicker()
    } else {
      storagePermGalleryPickerLauncher?.launch(STORAGE_PERMISSION)
    }

  }

  fun openPicker(fragment: Fragment, listener: Listener?) {
    this.listener = listener
    if (fragment.checkPermission(STORAGE_PERMISSION)) {
      launchPicker()
    } else {
      storagePermGalleryPickerLauncher?.launch(STORAGE_PERMISSION)
    }

  }

  private fun launchPicker() {
    val intent = Intent()
    intent.type = "image/*"
    intent.action = Intent.ACTION_OPEN_DOCUMENT
    galleryPickerLauncher?.launch(intent)
  }


  private fun initLauncher(activity: AppCompatActivity) {

    storagePermGalleryPickerLauncher = activity.registerForActivityResult(ActivityResultContracts.RequestPermission()) {
      if (it) {
        openPicker(activity, listener)
      }

    }
    galleryPickerLauncher = activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
      if (result.resultCode == Activity.RESULT_OK) {
        // There are no request codes
        val data: Intent? = result.data

        saveUri(data)

      }
    }
  }

  fun initLauncher(fragment: Fragment) {

    storagePermGalleryPickerLauncher = fragment.registerForActivityResult(ActivityResultContracts.RequestPermission()) {
      if (it) {
        openPicker(fragment, listener)
      }

    }
    galleryPickerLauncher = fragment.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
      if (result.resultCode == Activity.RESULT_OK) {
        // There are no request codes
        val data: Intent? = result.data

        saveUri(data)

      }
    }
  }

  private fun saveUri(data: Intent?) {
    data?.data?.let {
      CoroutineScope(Dispatchers.Default).launch {
        val file = FileUtils.saveFile(it, BaseApplication.instance.externalCacheDir?.path, "temp_img.jpg")
        if (file != null) {
          runOnUi { listener?.onFilePicked(file.path) }
        } else {
          showToast(fetchString(R.string.something_went_wrong))
        }
      }
    }
  }
}