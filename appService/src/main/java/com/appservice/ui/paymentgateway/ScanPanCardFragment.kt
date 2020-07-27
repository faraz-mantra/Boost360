package com.appservice.ui.paymentgateway


import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.YuvImage
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.constant.FragmentType
import com.appservice.databinding.FragmentScanPanCardBinding
import com.appservice.ui.catlogService.startFragmentActivity
import com.camerakit.CameraKitView
import com.framework.imagepicker.ImagePicker
import com.framework.models.BaseViewModel
import io.fotoapparat.Fotoapparat
import io.fotoapparat.configuration.CameraConfiguration
import io.fotoapparat.log.logcat
import io.fotoapparat.log.loggers
import io.fotoapparat.parameter.ScaleType
import io.fotoapparat.result.adapter.rxjava2.toSingle
import io.fotoapparat.selector.*
import java.io.File


class ScanPanCardFragment : AppBaseFragment<FragmentScanPanCardBinding, BaseViewModel>() {


    private var cameraPreview: Fotoapparat? = null
//    private var imageFile: File = File("/camera/image")
    private var activeCamera: Camera = Camera.Back

    private var IMAGE_PICK_CODE = 101
    private var PERMISSIONS_CODE = 100

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


        cameraPreview = binding?.panCardScanCameraView?.let {
            Fotoapparat(
                    context = this.baseActivity,
                    view = it,
                    scaleType = ScaleType.CenterCrop,
                    lensPosition = back(),
                    logger = logcat(),
//                    cameraConfiguration = activeCamera.configuration,
                    cameraErrorCallback = { error ->
                        Log.d("Fotoapparat error","Recorder errors: $error")
                    }
            )
        }

        setOnClickListener(
                binding?.btnUploadPanGallery,
                binding?.btnClickPhoto
        )
    }

    override fun getLayout(): Int {
        return R.layout.fragment_scan_pan_card
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onStart() {
        super.onStart()
        checkAndAskPermissions()
        this.cameraPreview?.start()
    }

    override fun onStop() {
        super.onStop()
        cameraPreview?.stop()
    }


    override fun onClick(v: View) {
        super.onClick(v)
        when(v){
            binding?.btnClickPhoto -> clickImage()
            binding?.btnUploadPanGallery -> openImagePicker()
        }
    }

    private fun clickImage(){

        val panCardImage = cameraPreview?.autoFocus()?.takePicture()

        panCardImage?.saveToFile(File(baseActivity.getExternalFilesDir("photos"), "pan_photo.jpg"))

        panCardImage?.toBitmap()?.whenAvailable { bitmapPhoto -> bitmapPhoto?.let{
            Log.i("New Photo Captured", it.toString())
            arguments?.let { startFragmentPaymentActivity(FragmentType.KYC_DETAILS, it)}
        } }

        Log.d("Pan Card Image", panCardImage.toString())

        arguments?.let { startFragmentPaymentActivity(FragmentType.KYC_DETAILS, it)}
        cameraPreview?.stop()

    }

    private fun openImagePicker(){
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    private fun checkAndAskPermissions(){
        if(this.context?.let { ContextCompat.checkSelfPermission(it, Manifest.permission.CAMERA) } != PackageManager.PERMISSION_GRANTED ) {
            this.activity?.let {
                ActivityCompat.requestPermissions(it, arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE),
                        PERMISSIONS_CODE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE){
            Log.d("Gallery Pick", data.toString())
        }
        if(resultCode == Activity.RESULT_OK && requestCode == PERMISSIONS_CODE){
            cameraPreview?.start()
        }
    }

}

private sealed class Camera(
        val lensPosition: LensPositionSelector,
        val configuration: CameraConfiguration
) {

    object Back : Camera(
            lensPosition = back(),
            configuration = CameraConfiguration(
                    previewResolution = firstAvailable(
                            wideRatio(highestResolution()),
                            standardRatio(highestResolution())
                    ),
                    previewFpsRange = highestFps(),
                    flashMode = off(),
                    focusMode = firstAvailable(
                            continuousFocusPicture(),
                            autoFocus()
                    ),
                    frameProcessor = {
                        // Do something with the preview frame
                    }
            )
    )

    object Front : Camera(
            lensPosition = front(),
            configuration = CameraConfiguration(
                    previewResolution = firstAvailable(
                            wideRatio(highestResolution()),
                            standardRatio(highestResolution())
                    ),
                    previewFpsRange = highestFps(),
                    flashMode = off(),
                    focusMode = firstAvailable(
                            fixed(),
                            autoFocus()
                    )
            )
    )
}
