package com.appservice.ui.updatesBusiness

import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.lifecycleScope
import com.appservice.R
import com.appservice.base.AppBaseActivity
import com.appservice.databinding.BsheetUpdateDraftBinding
import com.appservice.databinding.UpdateCropImageActivityBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.framework.base.BaseBottomSheetDialog
import com.framework.constants.Constants
import com.framework.models.BaseViewModel
import com.framework.utils.saveAsImageToAppFolder
import com.framework.utils.saveAsTempFile
import com.onboarding.nowfloats.bottomsheet.util.runOnUi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class UpdateCropImageActivity:AppBaseActivity<UpdateCropImageActivityBinding,BaseViewModel>() {

    private var path: String?=null
    private var rotateDegree =0

    companion object{
        val IK_IMAGE_PATH="IK_IMAGE_PATH"
        fun launchActivity(path:String,activity:Activity,launcher: ActivityResultLauncher<Intent>?){
            val intent = Intent(activity,UpdateCropImageActivity::class.java)
            intent.putExtra(IK_IMAGE_PATH,path)
            launcher?.launch(intent)
        }
    }
    override fun getLayout(): Int {
        return R.layout.update_crop_image_activity
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {
        setOnClickListener(binding!!.layoutChangeImage,binding!!.layoutTick,binding!!.layoutRotate,binding!!.ivDelete)

        path = intent.getStringExtra(IK_IMAGE_PATH)
        showImageInUi()

    }

    fun showImageInUi(){
        lifecycleScope.launch {
            withContext(Dispatchers.Default){
                val bitmap = Glide.with(this@UpdateCropImageActivity).asBitmap().load(
                    File(path)
                ).apply(RequestOptions.skipMemoryCacheOf(true))
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE)).submit().get()
                runOnUi {
                    binding!!.ivCrop.setImageBitmap(bitmap)

                }
            }




        }

    }
    override fun onClick(v: View?) {
        super.onClick(v)
        when(v){
            binding!!.layoutChangeImage->{
                UpdateImagePickerBSheet.newInstance(object :UpdateImagePickerBSheet.Callbacks{
                    override fun onImagePicked(path: String) {
                        this@UpdateCropImageActivity.path = path
                        showImageInUi()
                    }
                }).show(supportFragmentManager,UpdateImagePickerBSheet::class.java.name)
            }
            binding!!.layoutRotate->{
                rotateDegree+=90
                binding!!.ivCrop.rotateImage(rotateDegree)
            }

            binding!!.layoutTick->{
                binding!!.ivCrop.croppedImage.saveAsImageToAppFolder(getExternalFilesDir(null)?.path+File.separator
                + Constants.UPDATE_PIC_FILE_NAME)
                finish()
            }
            binding!!.ivDelete->{
                File(getExternalFilesDir(null)?.path+File.separator
                        + Constants.UPDATE_PIC_FILE_NAME).delete()
                finish()
            }

        }
    }
}