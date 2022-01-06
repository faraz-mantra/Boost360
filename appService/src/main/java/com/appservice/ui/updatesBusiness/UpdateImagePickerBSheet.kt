package com.appservice.ui.updatesBusiness

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.appservice.R
import com.appservice.databinding.BsheetUpdateDraftBinding
import com.appservice.databinding.BsheetUpdateImagePickerBinding
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel
import com.framework.utils.FileUtils

class UpdateImagePickerBSheet:BaseBottomSheetDialog<BsheetUpdateImagePickerBinding,BaseViewModel>() {


    private var startForGalleryImageResult: ActivityResultLauncher<Intent>?=null
    private var startForCameraImageResult: ActivityResultLauncher<Intent>?=null

    companion object{
        val fileName = "update_temp.jpg"
        fun newInstance(callbacks: Callbacks): UpdateImagePickerBSheet {
            val fragment = UpdateImagePickerBSheet()
            fragment.callbacks = callbacks
            return fragment
        }
    }

    interface Callbacks{
        fun onImagePicked(path:String)
    }
    private var callbacks:Callbacks?=null
    override fun getLayout(): Int {
        return R.layout.bsheet_update_image_picker
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {
        setOnClickListener(binding!!.layoutTakePhoto,binding!!.layoutGallery,binding!!.layoutOther,binding!!.rivCloseBottomSheet)
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when(v){
            binding!!.layoutGallery->{
                com.github.dhaval2404.imagepicker.ImagePicker.with(this).galleryOnly()
                    .createIntent {
                        startForGalleryImageResult?.launch(it)
                    }
            }
            binding!!.layoutTakePhoto->{
                com.github.dhaval2404.imagepicker.ImagePicker.with(this).cameraOnly()
                    .createIntent {
                        startForCameraImageResult?.launch(it)
                    }
            }
            binding!!.rivCloseBottomSheet->{
                dismiss()
            }
        }


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startForGalleryImageResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                    result: ActivityResult ->
                val resultCode = result.resultCode
                val data = result.data

                if (resultCode == Activity.RESULT_OK) {
                    //Image Uri will not be null for RESULT_OK
                    val fileUri = data?.data!!

                    val file = FileUtils.saveFile(fileUri,requireActivity().getExternalFilesDir(null)?.path,
                        fileName)

                    if (file?.exists() == true){
                        callbacks?.onImagePicked(file.path)
                        dismiss()
                    }

                }
            }

        startForCameraImageResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                    result: ActivityResult ->
                val resultCode = result.resultCode
                val data = result.data

                if (resultCode == Activity.RESULT_OK) {
                    //Image Uri will not be null for RESULT_OK
                    val fileUri = data?.data!!

                    val file = FileUtils.saveFile(fileUri,requireActivity().getExternalFilesDir(null)?.path,
                        fileName)

                    if (file?.exists() == true){
                        callbacks?.onImagePicked(file.path)
                        dismiss()
                    }

                }
            }
    }
}