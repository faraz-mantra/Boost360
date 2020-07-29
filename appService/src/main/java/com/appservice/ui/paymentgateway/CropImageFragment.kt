package com.appservice.ui.paymentgateway

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.constant.FragmentType
import com.appservice.databinding.FragmentImageCropBinding
import com.framework.models.BaseViewModel
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.lang.Exception

class CropImageFragment : AppBaseFragment<FragmentImageCropBinding, BaseViewModel>(),
        CropImageView.OnSetImageUriCompleteListener, CropImageView.OnCropImageCompleteListener{

    var croppedImageFile: File = File(Environment.getExternalStorageDirectory(), "photo.jpg")

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
        setOnClickListener(
                binding?.btnCropDone
        )

        binding?.cropImageView?.setOnCropImageCompleteListener(this)

        if(arguments?.getString("PAN CARD IMAGE") != null) {
            var panImageUriString = arguments?.getString("PAN CARD IMAGE")
            var panImageUri = Uri.parse(panImageUriString)
            binding?.cropImageView?.setImageUriAsync(panImageUri)
        }else{
            var cacheDir = context?.cacheDir
            var file = File(cacheDir, "image")
            var fileInputStream: FileInputStream? = null
            try{
                fileInputStream = FileInputStream(file)
            }catch(e: FileNotFoundException){
                e.printStackTrace()
            }

            var panImageBitmap: Bitmap? = BitmapFactory.decodeStream(fileInputStream)
            binding?.cropImageView?.setImageBitmap(panImageBitmap)

        }
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when(v){
            binding?.btnCropDone -> binding?.cropImageView?.saveCroppedImageAsync(Uri.fromFile(croppedImageFile))
        }
    }

    override fun onSetImageUriComplete(view: CropImageView?, uri: Uri?, error: Exception?) {
    }

    override fun onCropImageComplete(view: CropImageView?, result: CropImageView.CropResult?) {
        handleCropResult(view, result)
    }

    private fun handleCropResult(view: CropImageView?, result: CropImageView.CropResult?){
        if(result?.error == null){
            Log.d("Cropped Image", result?.toString())
            if(result?.uri != null){
                Log.d("Cropped Image URI", result.uri.toString())
                startFragmentPaymentActivity(FragmentType.KYC_DETAILS, Bundle().apply { putString("PAN CARD IMAGE", result.uri.toString()) })
            }
        }else{
            result?.error.printStackTrace()
//            Log.d("Crop Error", result?.error.toString())
        }
    }
}