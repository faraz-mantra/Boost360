package com.nowfloats.NavigationDrawer

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.nowfloats.test.com.nowfloatsui.buisness.util.Util
import com.nowfloats.util.MixPanelController
import com.thinksity.R

class EditImageActivity : AppCompatActivity() {
    // Instance variables
    private var mAspectRatioX = DEFAULT_ASPECT_RATIO_VALUES
    private var mAspectRatioY = DEFAULT_ASPECT_RATIO_VALUES
    var croppedImage: Bitmap? = null

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
        val cropImageView = findViewById<View>(R.id.CropImageView) as com.theartofdev.edmodo.cropper.CropImageView
        if (intent.hasExtra("image")) {
            try {
                cropImageView.setImageBitmap(Util.getBitmap(intent.getStringExtra("image"), this@EditImageActivity))
                cropImageView.setFixedAspectRatio(intent.getBooleanExtra("isFixedAspectRatio", false))
            } catch (error: OutOfMemoryError) {
                error.printStackTrace()
                System.gc()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        //Sets the rotate button
        val rotateButton = findViewById<View>(R.id.Button_rotate) as Button
        rotateButton.setOnClickListener { cropImageView.rotateImage(ROTATE_NINETY_DEGREES) }

        // Sets initial aspect ratio to 10/10, for demonstration purposes
        cropImageView.setAspectRatio(DEFAULT_ASPECT_RATIO_VALUES, DEFAULT_ASPECT_RATIO_VALUES)
        val cropButton = findViewById<View>(R.id.Button_crop) as Button
        cropButton.setOnClickListener {
            try {
                croppedImage = cropImageView.croppedImage
                if (croppedImage != null) {
                    croppedImage?.let {
                        val rect = Rect(0, 0, it.width, it.height)
                        cropImageView.setImageBitmap(it)
                        cropImageView.cropRect = rect
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
                croppedImage = cropImageView.croppedImage
                if (croppedImage != null) {
                    val `in` = Intent()
                    val path = Util.saveCameraBitmap(croppedImage, this@EditImageActivity, "Edit" + System.currentTimeMillis())
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

    companion object {
        // Static final constants
        private const val DEFAULT_ASPECT_RATIO_VALUES = 10
        private const val ROTATE_NINETY_DEGREES = 90
        private const val ASPECT_RATIO_X = "ASPECT_RATIO_X"
        private const val ASPECT_RATIO_Y = "ASPECT_RATIO_Y"
    }
}