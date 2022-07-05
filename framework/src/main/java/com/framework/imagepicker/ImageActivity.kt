package com.framework.imagepicker

import android.Manifest
import android.annotation.TargetApi
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.framework.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.io.Serializable
import java.lang.ref.WeakReference
import java.util.*
import kotlin.collections.ArrayList

open class ImageActivity : AppCompatActivity() {

  private var destination: File? = null
  private var mImageUri: Uri? = null
  private var mImgConfig: ImageConfig? = null
  private var listOfImgs: ArrayList<String?>? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    overridePendingTransition(0, 0)
    val intent: Intent = intent
    mImgConfig = intent.getSerializableExtra(ImageTags.Tags.IMG_CONFIG) as ImageConfig
    if (savedInstanceState == null) {
      pickImageWrapper()
      listOfImgs = ArrayList()
    }
    if (mImgConfig!!.debug) Log.d(ImageTags.Tags.TAG, mImgConfig.toString())
  }

  private fun pickImage() {
    Utility.createFolder(mImgConfig!!.directory)
    destination = File(mImgConfig!!.directory, Utility.randomString + ImagePicker.Extension.JPG.value)
    when (mImgConfig!!.mode) {
      ImagePicker.Mode.CAMERA -> startActivityFromCamera()
      ImagePicker.Mode.GALLERY -> if (mImgConfig!!.allowMultiple) startActivityFromGalleryMultiImg() else startActivityFromGallery()
      ImagePicker.Mode.CAMERA_AND_GALLERY -> showFromCameraOrGalleryAlert()
      else -> {
      }
    }
  }

  private fun showFromCameraOrGalleryAlert() {
    AlertDialog.Builder(this)
      .setTitle(getString(R.string.media_picker_select_from))
      .setPositiveButton(getString(R.string.media_picker_camera)) { _, _ ->
        if (mImgConfig!!.debug) Log.d(ImageTags.Tags.TAG, "Alert Dialog - Start From Camera")
        startActivityFromCamera()
      }
      .setNegativeButton(getString(R.string.media_picker_gallery)) { _, _ ->
        if (mImgConfig!!.debug) Log.d(ImageTags.Tags.TAG, "Alert Dialog - Start From Gallery")
        if (mImgConfig!!.allowMultiple) startActivityFromGalleryMultiImg() else startActivityFromGallery()
      }
      .setOnCancelListener {
        if (mImgConfig!!.debug) Log.d(ImageTags.Tags.TAG, "Alert Dialog - Canceled")
        finish()
      }.show()
  }

  private fun startActivityFromGallery() {
    mImgConfig!!.isImgFromCamera = false
    val photoPickerIntent = Intent(Intent.ACTION_OPEN_DOCUMENT)
    photoPickerIntent.addCategory(Intent.CATEGORY_OPENABLE)
    photoPickerIntent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
    photoPickerIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    photoPickerIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
    photoPickerIntent.type = "image/*"
    startActivityForResult(photoPickerIntent, ImageTags.IntentCode.REQUEST_CODE_SELECT_PHOTO)
    if (mImgConfig!!.debug) Log.d(ImageTags.Tags.TAG, "Gallery Start with Single Image mode")
  }

  private fun startActivityFromGalleryMultiImg() {
    mImgConfig!!.isImgFromCamera = false
    val photoPickerIntent = Intent(Intent.ACTION_OPEN_DOCUMENT)
    photoPickerIntent.addCategory(Intent.CATEGORY_OPENABLE)
    photoPickerIntent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
    photoPickerIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    photoPickerIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
    photoPickerIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
    photoPickerIntent.type = "image/*"
    startActivityForResult(photoPickerIntent, ImageTags.IntentCode.REQUEST_CODE_SELECT_MULTI_PHOTO)

    if (mImgConfig!!.debug) Log.d(ImageTags.Tags.TAG, "Gallery Start with Multiple Images mode")
  }

  private fun startActivityFromCamera() {
    mImgConfig!!.isImgFromCamera = true
    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    mImageUri = destination?.let {
      FileProvider.getUriForFile(
        this,
        this.applicationContext.packageName.toString() + ".provider",
        it
      )
    }
    intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri)
    //        startActivityForResult(Intent.createChooser(intent, "Select Picture"), ImageTags.IntentCode.CAMERA_REQUEST);
    startActivityForResult(intent, ImageTags.IntentCode.CAMERA_REQUEST)
    if (mImgConfig!!.debug) Log.d(ImageTags.Tags.TAG, "Camera Start")
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    if (mImageUri != null) {
      outState.putString(ImageTags.Tags.CAMERA_IMAGE_URI, mImageUri.toString())
      outState.putSerializable(ImageTags.Tags.IMG_CONFIG, mImgConfig)
    }
  }

  override fun onRestoreInstanceState(savedInstanceState: Bundle) {
    super.onRestoreInstanceState(savedInstanceState)
    if (savedInstanceState.containsKey(ImageTags.Tags.CAMERA_IMAGE_URI)) {
      mImageUri = Uri.parse(savedInstanceState.getString(ImageTags.Tags.CAMERA_IMAGE_URI))
      destination = File(mImageUri?.path)
      mImgConfig = savedInstanceState.getSerializable(ImageTags.Tags.IMG_CONFIG) as ImageConfig
    }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    Log.d(
      ImageTags.Tags.TAG,
      "onActivityResult() called with: requestCode = [$requestCode], resultCode = [$resultCode], data = [$data]"
    )
    if (resultCode == RESULT_OK) {
      when (requestCode) {
        ImageTags.IntentCode.CAMERA_REQUEST -> CompressImageTask(
          destination!!.absolutePath, mImgConfig = mImgConfig)
        ImageTags.IntentCode.REQUEST_CODE_SELECT_PHOTO -> processOneImage(data)
        ImageTags.IntentCode.REQUEST_CODE_SELECT_MULTI_PHOTO ->                     //Check if the intent contain only one image
          if (data?.clipData == null) {
            data?.let { processOneImage(it) }
          } else {
            //intent has multi images
            listOfImgs = ImageProcessing.processMultiImage(this, data)
            CompressImageTask(null,listOfImgs!!, mImgConfig)
          }
        else -> {
        }
      }
    } else {
      val intent = Intent()
      intent.action = "mediapicker.image.service"
      intent.putExtra(ImageTags.Tags.PICK_ERROR, "user did not select any image")
      sendBroadcast(intent)
      finish()
    }
  }

  private fun processOneImage(data: Intent?) {
    try {
      val selectedImage = data?.data
      val selectedImagePath = selectedImage?.let { FileProcessing.getPath( it) }
      CompressImageTask(selectedImagePath, mImgConfig = mImgConfig)
    } catch (ex: Exception) {
      ex.printStackTrace()
    }
  }

  private fun finishActivity(path: List<String>) {
    val resultIntent = Intent()
    resultIntent.putExtra(ImagePicker.EXTRA_IMAGE_PATH, path as Serializable)
    setResult(RESULT_OK, resultIntent)
    finish()
  }

  private fun pickImageWrapper() {
    if (Build.VERSION.SDK_INT >= 23) {
      val permissionsNeeded: MutableList<String> = ArrayList()
      val permissionsList: MutableList<String> = ArrayList()
      if ((mImgConfig!!.mode === ImagePicker.Mode.CAMERA || mImgConfig!!.mode === ImagePicker.Mode.CAMERA_AND_GALLERY) && !addPermission(
          permissionsList,
          Manifest.permission.CAMERA
        )
      ) permissionsNeeded.add(getString(R.string.media_picker_camera))
      if (!addPermission(
          permissionsList,
          Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
      ) permissionsNeeded.add(getString(R.string.media_picker_read_Write_external_storage))
      if (permissionsList.size > 0) {
        if (permissionsNeeded.size > 0) {
          // Need Rationale
          var message: String =
            getString(R.string.media_picker_you_need_to_grant_access_to) + " " + permissionsNeeded[0]
          for (i in 1 until permissionsNeeded.size) message = message + ", " + permissionsNeeded[i]
          showMessageOKCancel(message,
            DialogInterface.OnClickListener { _, _ ->
              ActivityCompat.requestPermissions(
                this@ImageActivity, permissionsList.toTypedArray(),
                ImageTags.IntentCode.REQUEST_CODE_ASK_PERMISSIONS
              )
            })
          return
        }
        ActivityCompat.requestPermissions(
          this@ImageActivity, permissionsList.toTypedArray(),
          ImageTags.IntentCode.REQUEST_CODE_ASK_PERMISSIONS
        )
        return
      }
      pickImage()
    } else {
      pickImage()
    }
  }

  private fun showMessageOKCancel(message: String, okListener: DialogInterface.OnClickListener) {
    AlertDialog.Builder(this@ImageActivity)
      .setMessage(message).setPositiveButton(getString(R.string.media_picker_ok), okListener)
      .setNegativeButton(getString(R.string.media_picker_cancel)) { dialog, _ ->
        run {
          dialog.cancel()
          this.onBackPressed()
        }
      }.create().show()
  }

  private fun addPermission(permissionsList: MutableList<String>, permission: String): Boolean {
    if (ActivityCompat.checkSelfPermission(
        this@ImageActivity,
        permission
      ) !== PackageManager.PERMISSION_GRANTED
    ) {
      permissionsList.add(permission)
      // Check for Rationale Option
      if (!ActivityCompat.shouldShowRequestPermissionRationale(
          this@ImageActivity,
          permission
        )
      ) return false
    }
    return true
  }

  override fun onRequestPermissionsResult(
    requestCode: Int,
    @NonNull permissions: Array<String>,
    @NonNull grantResults: IntArray
  ) {
    when (requestCode) {
      ImageTags.IntentCode.REQUEST_CODE_ASK_PERMISSIONS -> {
        val perms: MutableMap<String, Int> = HashMap()
        // Initial
        perms[Manifest.permission.CAMERA] = PackageManager.PERMISSION_GRANTED
        perms[Manifest.permission.WRITE_EXTERNAL_STORAGE] = PackageManager.PERMISSION_GRANTED
        // Fill with results
        var i = 0
        while (i < permissions.size) {
          perms[permissions[i]] = grantResults[i]
          i++
        }
        // Check for ACCESS_FINE_LOCATION
        if (perms[Manifest.permission.CAMERA] == PackageManager.PERMISSION_GRANTED
          && perms[Manifest.permission.WRITE_EXTERNAL_STORAGE] == PackageManager.PERMISSION_GRANTED
        ) {
          // All Permissions Granted
          pickImage()
        } else {
          // Permission Denied
          Toast.makeText(
            this@ImageActivity,
            getString(R.string.media_picker_some_permission_is_denied),
            Toast.LENGTH_SHORT
          ).show()
        }
      }
      else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
  }

  private fun CompressImageTask(absolutePath: String?, listOfImgs: ArrayList<String?>?=ArrayList(), mImgConfig: ImageConfig?) {
    lifecycleScope.launchWhenCreated {

      val pd = ProgressDialog(this@ImageActivity,R.style.MyAlertDialogStyle)
      pd.setMessage(getString(R.string.loading))
      pd.show()
      withContext(Dispatchers.IO){
        val destinationPaths: MutableList<String> = ArrayList()

        absolutePath?.let {
          listOfImgs?.add(absolutePath)
        }
        for (mPath in listOfImgs!!) {
          if (mPath!=null){
            try {
              val file = File(mPath)
              var destinationFile: File? = null
              destinationFile = if (mImgConfig!!.isImgFromCamera) {
                file
              } else {
                var ext = "."+MimeTypeMap.getFileExtensionFromUrl(file.toString())
                if (ext.isNullOrEmpty()){
                  ext = ImagePicker.Extension.JPG.value
                }
                File(mImgConfig.directory, Utility.randomString + ext)
              }
              destinationPaths.add(destinationFile.absolutePath)

              Utility.compressAndRotateIfNeeded(
                file, destinationFile, mImgConfig.compressLevel.value, mImgConfig.reqWidth, mImgConfig.reqHeight
              )
            } catch (e: IOException) {
              e.printStackTrace()
            }
          }

        }

        withContext(Dispatchers.Main){
          pd.dismiss()
          finishActivity(destinationPaths)
          val intent = Intent()
          intent.action = "mediapicker.image.service"
          intent.putExtra(ImageTags.Tags.IMAGE_PATH, destinationPaths as Serializable)
          sendBroadcast(intent)
        }


      }
    }


  }



  companion object {
    private const val TAG = "ImageActivity"
    fun getCallingIntent(activity: Context?, imageConfig: ImageConfig?): Intent {
      val intent = Intent(activity, ImageActivity::class.java)
      intent.putExtra(ImageTags.Tags.IMG_CONFIG, imageConfig)
      return intent
    }
  }

  override fun finish() {
    super.finish()
    overridePendingTransition(0, 0)
  }
}
