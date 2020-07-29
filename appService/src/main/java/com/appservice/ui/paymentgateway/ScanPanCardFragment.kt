package com.appservice.ui.paymentgateway


import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.constant.FragmentType
import com.appservice.databinding.FragmentScanPanCardBinding
import com.framework.models.BaseViewModel
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.CameraView
import com.otaliastudios.cameraview.PictureResult
import com.otaliastudios.cameraview.VideoResult
import java.io.*


class ScanPanCardFragment : AppBaseFragment<FragmentScanPanCardBinding, BaseViewModel>() {

    private var camera: CameraView? = null
    private var CAMERA_PERMISSIONS_CODE = 100
    private var STORAGE_PERMISSIONS_CODE = 101
    private var IMAGE_PICK_CODE = 102

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
//
        checkAndAskPermissions()
        camera = binding?.panCardScanCameraView
        camera?.setLifecycleOwner(this)

        camera?.addCameraListener(object: CameraListener() {

            @SuppressLint("WrongThread")
            override fun onPictureTaken(result: PictureResult) {
                super.onPictureTaken(result)
                var cacheDir: File? = context?.cacheDir
                var file: File? = File(cacheDir, "image")
                var bitmapImage = BitmapFactory.decodeByteArray(result.data, 0, result.data.size)
                try{
                    var outputStream = FileOutputStream(file)
                    bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                    outputStream.flush()
                    outputStream.close()
                    startFragmentPaymentActivity(FragmentType.CROP_IMAGE)
                }catch(e: FileNotFoundException){
                    e.printStackTrace()
                }catch (e: IOException){
                    e.printStackTrace()
                }
            }
        })

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

    override fun onClick(v: View) {
        super.onClick(v)
        when(v){
            binding?.btnClickPhoto -> camera?.takePicture()
            binding?.btnUploadPanGallery -> openImagePicker()
        }
    }

    private fun checkAndAskPermissions(){
        if(this.context?.let{ActivityCompat.checkSelfPermission(it, Manifest.permission.WRITE_EXTERNAL_STORAGE)}
                != PackageManager.PERMISSION_GRANTED && this.context?.let { ActivityCompat.checkSelfPermission(
                        it, Manifest.permission.READ_EXTERNAL_STORAGE) } != PackageManager.PERMISSION_GRANTED){
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE), STORAGE_PERMISSIONS_CODE)
        }
    }


    private fun openImagePicker(){
        if(this.context?.let { ActivityCompat.checkSelfPermission(it, Manifest.permission.READ_EXTERNAL_STORAGE) } != PackageManager.PERMISSION_GRANTED ) {
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    STORAGE_PERMISSIONS_CODE)
        }else{
            startImagePickerIntent()
        }
    }

    private fun startImagePickerIntent(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val mimeTypes = arrayOf("image/jpeg", "image/png")
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        }

        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE){
            startFragmentPaymentActivity(FragmentType.CROP_IMAGE, Bundle().apply { putString("PAN CARD IMAGE", data?.data?.toString()) })

        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            CAMERA_PERMISSIONS_CODE ->{
                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Log.d("CAMERA PERMISSION", "PERMISSION GRANTED")
                }else{
                    Toast.makeText(context, "Please provide the camera permission to proceed", Toast.LENGTH_LONG).show()
                }
            }

            STORAGE_PERMISSIONS_CODE ->{
                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Log.d("STORAGE PERMISSION", "PERMISSION GRANTED")
//                    startImagePickerIntent()
                }else{
                    Toast.makeText(context, "Please provide the storage permissions to proceed", Toast.LENGTH_LONG).show()
                    checkAndAskPermissions()
                }
            }
        }
    }
}


