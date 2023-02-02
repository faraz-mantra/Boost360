package com.appservice.ui.updatesBusiness

import android.app.Activity
import android.content.Intent
import android.graphics.Rect
import android.view.MotionEvent
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
import com.framework.constants.UPDATE_PIC_FILE_NAME
import com.framework.models.BaseViewModel
import com.framework.utils.saveAsImageToAppFolder
import com.framework.utils.saveAsTempFile
import com.framework.utils.setClickableRipple
import com.framework.utils.setStatusBarColor
import com.onboarding.nowfloats.bottomsheet.util.runOnUi
import com.theartofdev.edmodo.cropper.CropImageView
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.*
import kotlin.concurrent.schedule
import kotlin.math.absoluteValue

class UpdateCropImageActivity:AppBaseActivity<UpdateCropImageActivityBinding,BaseViewModel>() {

    private var currentRotation =0.0F
    private var path: String?=null

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
        setOnClickListener(binding!!.layoutChangeImage,binding!!.layoutTick,binding!!.layoutRotate,binding!!.ivDelete,binding?.ivCropUpdateClose)

        path = intent.getStringExtra(IK_IMAGE_PATH)

        showImageInUi()

    }

    override fun onResume() {
        super.onResume()
        setStatusBarColor(R.color.black_3333)
    }
    fun showImageInUi(){
        lifecycleScope.launch {
            withContext(Dispatchers.Default){
                val bitmap = Glide.with(this@UpdateCropImageActivity).asBitmap().load(
                    path
                ).apply(RequestOptions.skipMemoryCacheOf(true))
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE)).submit().get()
                runOnUi {
                    if(bitmap.width <= 400 || bitmap.height <= 400 ){
                        Toasty.warning(this@UpdateCropImageActivity,"Max Cropping allowed 400px for width/height", Toasty.LENGTH_LONG).show()
                        this@UpdateCropImageActivity.finish()
                        return@runOnUi
                    }
                    binding!!.ivCrop.setImageBitmap(bitmap)
                    binding!!.ivCrop.setMinCropResultSize(400,400)
                    binding!!.ivCrop.setCropRect(Rect(400, 400, 600, 600))
//                    binding!!.ivCrop.setAspectRatio(600,600)
//                    binding!!.ivCrop.setFixedAspectRatio(false)
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
                val fromRotation = if (currentRotation.absoluteValue == 360f) 0f else currentRotation
                val rotateDegrees = 90f
                val toRotation = (fromRotation + rotateDegrees) % 450f
                binding!!.ivCrop.rotateImage(toRotation.toInt())
            }

            binding!!.layoutTick->{
                binding!!.ivCrop.croppedImage.saveAsImageToAppFolder(getExternalFilesDir(null)?.path+File.separator
                + UPDATE_PIC_FILE_NAME)
                setResult(Activity.RESULT_OK)

                finish()
            }
            binding!!.ivDelete->{
                File(getExternalFilesDir(null)?.path+File.separator
                        + UPDATE_PIC_FILE_NAME).delete()
                setResult(Activity.RESULT_OK)
                finish()
            }

            binding?.ivCropUpdateClose ->{
                finish()
            }

        }
    }
}