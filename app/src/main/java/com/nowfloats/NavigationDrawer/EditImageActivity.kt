package com.nowfloats.NavigationDrawer

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.View
import android.view.Window
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.nowfloats.BusinessProfile.UI.API.Upload_Logo
import com.nowfloats.Login.UserSessionManager
import com.nowfloats.NavigationDrawer.floating_view.ImagePickerBottomSheetDialog
import com.nowfloats.NotificationCenter.AlertArchive
import com.nowfloats.test.com.nowfloatsui.buisness.util.Util
import com.nowfloats.util.Constants
import com.nowfloats.util.Methods
import com.nowfloats.util.MixPanelController
import com.theartofdev.edmodo.cropper.CropImageView
import com.thinksity.R

class EditImageActivity : AppCompatActivity() {
    // Instance variables
    private var mAspectRatioX = DEFAULT_ASPECT_RATIO_VALUES
    private var mAspectRatioY = DEFAULT_ASPECT_RATIO_VALUES
    var croppedImage: Bitmap? = null
    private val gallery_req_id = 0
    private val media_req_id = 1
    private val GALLERY_PHOTO = 2
    private val CAMERA_PHOTO = 1
    private  var cropImageView: CropImageView? = null

    // Saves the state upon rotating the screen/restarting the activity
    override fun onSaveInstanceState(bundle: Bundle) {
        super.onSaveInstanceState(bundle)
        bundle.putInt(ASPECT_RATIO_X, mAspectRatioX)
        bundle.putInt(ASPECT_RATIO_Y, mAspectRatioY)
    }

    // Restores the state upon rotating the screen/restarting the activity
    override fun onRestoreInstanceState(bundle: Bundle) {
        super.onRestoreInstanceState(bundle)
        mAspectRatioX = bundle.getInt(ASPECT_RATIO_X)
        mAspectRatioY = bundle.getInt(ASPECT_RATIO_Y)
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.edit_image)
        MixPanelController.track("EditPhoto", null)
        // Initialize components of the app
        this. cropImageView =
            findViewById<View>(R.id.CropImageView) as CropImageView
        if (intent.hasExtra("image")) {
            try {
                cropImageView!!.setImageBitmap(
                    Util.getBitmap(
                        intent.getStringExtra("image"),
                        this@EditImageActivity
                    )
                )
                cropImageView!!.setFixedAspectRatio(
                    intent.getBooleanExtra(
                        "isFixedAspectRatio",
                        false
                    )
                )
            } catch (error: OutOfMemoryError) {
                error.printStackTrace()
                System.gc()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        //Sets the choose image button
        val chooseImage = findViewById<View>(R.id.button_recapture) as Button
        chooseImage.setOnClickListener {
            val imagePickerBottomSheetDialog =
                ImagePickerBottomSheetDialog({
                    if (it.name == ImagePickerBottomSheetDialog.IMAGE_CLICK_TYPE.CAMERA.name) {
                        cameraIntent()
                    } else if (it.name == ImagePickerBottomSheetDialog.IMAGE_CLICK_TYPE.GALLERY.name) {
                        galleryIntent()
                    }

                }, false)
            imagePickerBottomSheetDialog.show(
                supportFragmentManager,
                ImagePickerBottomSheetDialog::class.java.name
            )
        }
        //Sets the rotate button
        val rotateButton = findViewById<View>(R.id.Button_rotate) as Button
        rotateButton.setOnClickListener { cropImageView!!.rotateImage(ROTATE_NINETY_DEGREES) }

        // Sets initial aspect ratio to 10/10, for demonstration purposes
        cropImageView!!.setAspectRatio(DEFAULT_ASPECT_RATIO_VALUES, DEFAULT_ASPECT_RATIO_VALUES)
        val cropButton = findViewById<View>(R.id.Button_crop) as Button
        cropButton.setOnClickListener {
            try {
                croppedImage = cropImageView!!.croppedImage
                if (croppedImage != null) {
                    croppedImage?.let {
                        val rect = Rect(0, 0, it.width, it.height)
                        cropImageView!!.setImageBitmap(it)
                        cropImageView!!.cropRect = rect
                    }
                }
            } catch (e: Exception) {
                System.gc()
                e.printStackTrace()
            }
        }
        val save = findViewById<View>(R.id.Button_save) as Button
        save.setOnClickListener {
            try {
                croppedImage = cropImageView!!.croppedImage
                if (croppedImage != null) {
                    val `in` = Intent()
                    val path = Util.saveCameraBitmap(
                        croppedImage,
                        this@EditImageActivity,
                        "Edit" + System.currentTimeMillis()
                    )
                    //Log.v("ggg","edit path "+path);
                    `in`.putExtra("edit_image", path)
                    setResult(RESULT_OK, `in`)
                    finish()
                }
            } catch (e: Exception) {
                System.gc()
                e.printStackTrace()
            }
        }
    }

    var imageUri: Uri? = null
    var CameraBitmap: Bitmap? = null
    var imageUrl = ""

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {
            if (resultCode == RESULT_OK && CAMERA_PHOTO == requestCode) {
                try {
                    CameraBitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
                    imageUrl = Methods.getRealPathFromURI(this, imageUri)
                    path = imageUrl
                    path = Util.saveBitmap(
                        path,
                        this@EditImageActivity,
                        "ImageFloat" + System.currentTimeMillis()
                    )
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                    //  Util.toast("Uh oh. Something went wrong. Please try again", this);
                } catch (E: OutOfMemoryError) {
                    //Log.d("ANDRO_ASYNC",String.format("catch Out Of Memory error"));
                    E.printStackTrace()
                    System.gc()
                    // Util.toast("Uh oh. Something went wrong. Please try again", this);
                }
                if (!Util.isNullOrEmpty(path)) {
                    cropImageView?.setImageBitmap(
                        Util.getBitmap(
                            path,
                            this@EditImageActivity
                        )
                    )
                } else Methods.showSnackBarNegative(
                    this@EditImageActivity,
                    resources.getString(R.string.select_image_upload)
                )
            } else if (resultCode == RESULT_OK && GALLERY_PHOTO == requestCode) {
                run {
                    val picUri = data?.data
                    if (picUri != null) {
                        path = Methods.getPath(this, picUri)
                        path = Util.saveBitmap(
                            path,
                            this@EditImageActivity,
                            "ImageFloat" + System.currentTimeMillis()
                        )
                        if (!Util.isNullOrEmpty(path)) {
                            cropImageView?.setImageBitmap(
                                Util.getBitmap(
                                    path,
                                    this@EditImageActivity
                                )
                            )

                        } else Methods.showSnackBarNegative(
                            this@EditImageActivity,
                            resources.getString(R.string.select_image_upload)
                        )
                    }
                }
            } else if (resultCode == RESULT_OK && requestCode == ACTION_REQUEST_IMAGE_EDIT) {
                val path = data?.getStringExtra("edit_image")?:data?.getStringExtra("")
                if (!TextUtils.isEmpty(path)) {
                    uploadPrimaryPicture(path)
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun uploadPrimaryPicture(path: String?) {
        val session = UserSessionManager(this, this)
        AlertArchive(Constants.alertInterface, "LOGO", session.fpid)
        val upload_logo = Upload_Logo(
            this@EditImageActivity, path, session.fpid, session
        ) { isSuccess: Boolean? ->
            Methods.showSnackBarPositive(
                this,
                getString(R.string.business_image_uploaded)
            )
        }
        upload_logo.execute()
    }

    private val ACTION_REQUEST_IMAGE_EDIT = 3
    var path: String? = null

    fun cameraIntent() {
        try {
            // use standard intent to capture an image
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) !=
                PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA),
                    media_req_id
                )
                return
            }
            val values = ContentValues()
            values.put(MediaStore.Images.Media.TITLE, "New Picture")
            values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera")
            val imageUri = contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values
            )
            val captureIntent = Intent(
                MediaStore.ACTION_IMAGE_CAPTURE
            )
            captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
            // we will handle the returned data in onActivityResult
            startActivityForResult(captureIntent, CAMERA_PHOTO)
        } catch (anfe: ActivityNotFoundException) {
            // display an error message
            val errorMessage = resources.getString(R.string.device_does_not_support_capturing_image)
            Methods.showSnackBarNegative(this@EditImageActivity, errorMessage)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun galleryIntent() {
        try {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    gallery_req_id
                )
                return
            }
            val i = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )
            startActivityForResult(i, GALLERY_PHOTO)
        } catch (anfe: ActivityNotFoundException) {
            // display an error message
            val errorMessage = resources.getString(R.string.device_does_not_support_capturing_image)
            Methods.showSnackBarNegative(this@EditImageActivity, errorMessage)
        }
    }

    companion object {
        // Static final constants
        private const val DEFAULT_ASPECT_RATIO_VALUES = 10
        private const val ROTATE_NINETY_DEGREES = 90
        private const val ASPECT_RATIO_X = "ASPECT_RATIO_X"
        private const val ASPECT_RATIO_Y = "ASPECT_RATIO_Y"
    }


}