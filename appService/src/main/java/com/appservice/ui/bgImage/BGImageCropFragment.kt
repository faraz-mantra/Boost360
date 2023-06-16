package com.appservice.ui.bgImage

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.SpannableString
import android.view.View
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.constant.FragmentType
import com.appservice.constant.IntentConstant
import com.appservice.databinding.FragmentCropZoomBinding
import com.appservice.utils.WebEngageController
import com.appservice.utils.openImagePicker
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.framework.imagepicker.ImagePicker
import com.framework.imagepicker.Utility
import com.framework.models.BaseViewModel
import com.framework.utils.FileUtils.saveBitmap
import com.framework.utils.gcd
import com.framework.utils.spanBold
import com.framework.utils.zoom
import com.framework.webengageconstant.ADDED
import com.framework.webengageconstant.BACKGROUND_IMAGE_CROP_LOAD
import com.framework.webengageconstant.GALLERY_IMAGE_ADDED
import com.framework.webengageconstant.GALLERY_IMAGE_UPLOADED
import com.framework.webengageconstant.START_VIEW

class BGImageCropFragment : AppBaseFragment<FragmentCropZoomBinding, BaseViewModel>() {

  private var imagePath: String? = null
  private var bitmap: Bitmap? = null
  private var validationStat = false
  private var cropCheck = false

  companion object {
    @JvmStatic
    fun newInstance(bundle: Bundle? = null): BGImageCropFragment {
      val fragment = BGImageCropFragment()
      fragment.arguments = bundle
      return fragment
    }

    val BK_IMAGE_PATH = "BK_IMAGE_PATH"
  }

  override fun getLayout(): Int {
    return R.layout.fragment_crop_zoom
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun onCreateView() {
    super.onCreateView()
    WebEngageController.trackEvent(BACKGROUND_IMAGE_CROP_LOAD, START_VIEW, sessionLocal.fpTag)
    imagePath = arguments?.getString(BK_IMAGE_PATH)
    cropCheck = arguments?.getBoolean("CropCheck",false)!!
    setImageOnUi()
    viewListeners()
    setOnClickListener(binding.btnDone)
  }

  private fun setImageOnUi() {
    bitmap = BitmapFactory.decodeFile(imagePath)
    if (bitmap == null || imagePath.isNullOrEmpty()) {
      showShortToast("File not created, please try again!")
      baseActivity.finish()
      return
    }
    binding.cropImg.setImageBitmap(Utility.rotateImageIfRequired(bitmap!!, imagePath))
    val options = BitmapFactory.Options()
    options.inScaled = false
    binding.cropImg.setImageBitmap(bitmap)
    checkImageDim()
    binding.slider.progress =0
  }

  private fun checkImageDim() {
    if ((bitmap?.width ?: 0) >= 1500 && (bitmap?.height ?: 0) >= 500) {
      if (checkAspectRatio()) {
        imageSuccessView()
      } else {
        imageErrorView(SpannableString(spanBold(getString(R.string.aspect_ration_not_matched), getString(R.string.aspect_ration_not_matched))))
      }
    } else {
      val spanBold = spanBold(getString(R.string.smaller_img_detected) + " (" + bitmap?.width.toString() + "x" + bitmap?.height.toString() + ").", getString(R.string.smaller_img_detected))
      imageErrorView(spanBold)
    }
  }

  private fun imageErrorView(errorText: SpannableString) {
    validationStat = true
    binding.layoutImageMisConfig.visible()
    binding.tvSliderSugg.gone()
    binding.tvImgDesc.text = errorText
    binding.btnDone.backgroundTintList = ContextCompat.getColorStateList(baseActivity, R.color.colorPrimary)
    binding.btnDone.text = resources.getString(R.string.crop_picture)
//    binding.btnDone.backgroundTintList = ContextCompat.getColorStateList(baseActivity, R.color.red_E39595)
//    binding.btnDone.text = resources.getString(R.string.change_image)
//    binding.layoutSeek.gone()
  }

  private fun imageSuccessView() {
    validationStat = true
    binding.layoutImageMisConfig.gone()
    binding.tvSliderSugg.visible()
//    binding.layoutSeek.visible()
    binding.btnDone.backgroundTintList = ContextCompat.getColorStateList(baseActivity, R.color.colorPrimary)
    binding.btnDone.text = resources.getString(R.string.crop_picture)
  }

  private fun viewListeners() {
    binding.slider.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
      override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
        if (p1 != 100) {
          binding.cropImg.setImageBitmap(bitmap?.zoom(p1.toFloat() / 100))
        }
      }

      override fun onStartTrackingTouch(p0: SeekBar?) {
      }

      override fun onStopTrackingTouch(p0: SeekBar?) {
      }
    })
  }

  fun checkAspectRatio(): Boolean {
    val options = BitmapFactory.Options()
    options.inScaled = false
    val bitmap = BitmapFactory.decodeFile(imagePath, options)
    val gcd = gcd(bitmap!!.width, bitmap.height)
    return (bitmap.width.div(gcd) == 12 && bitmap.height.div(gcd) == 5)
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding.btnDone -> {
        val imgFile = binding.cropImg.croppedImage?.saveBitmap()
        if (imgFile?.exists() == true) {
          if (validationStat){
            if (cropCheck){
              var imageCropCheckStatus = false
              if (binding.cropImg.croppedImage.width>=1200&&binding.cropImg.croppedImage.height>=525){
                imageCropCheckStatus=true
              }
              if (imageCropCheckStatus){
                startBackgroundActivity(
                  FragmentType.BACKGROUND_IMAGE_PREVIEW,
                  Bundle().apply { putString(BGImagePreviewFragment.BK_IMAGE_PATH, imgFile.absolutePath) },
                  isResult = true
                )
              }else{
                showLongToast("Cropped image resolution is lesser than the required!")
              }
            }else{
              startBackgroundActivity(
                FragmentType.BACKGROUND_IMAGE_PREVIEW,
                Bundle().apply { putString(BGImagePreviewFragment.BK_IMAGE_PATH, imgFile.absolutePath) },
                isResult = true
              )
            }
          }else{
            openImagePicker(requireActivity(),parentFragmentManager)
          }

        } else showLongToast("Unable to store image, please try again!")
      }
    }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (resultCode == AppCompatActivity.RESULT_OK && requestCode == 101) {
      if (data?.getBooleanExtra(IntentConstant.IS_BACK_PRESS.name, false) == true) {
        val output = Intent()
        output.putExtra(IntentConstant.IS_UPDATED.name, true)
        baseActivity.setResult(AppCompatActivity.RESULT_OK, output)
        baseActivity.finish()
      }


    }
    if (requestCode == ImagePicker.IMAGE_PICKER_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK) {
      val mPaths = data?.getSerializableExtra(ImagePicker.EXTRA_IMAGE_PATH) as? List<String>
      if (mPaths.isNullOrEmpty().not()) {
        WebEngageController.trackEvent(GALLERY_IMAGE_ADDED, ADDED, sessionLocal.fpTag)
        WebEngageController.trackEvent(GALLERY_IMAGE_UPLOADED, ADDED, sessionLocal.fpTag)
        imagePath = mPaths?.get(0)
        setImageOnUi()
      }
    }
  }
}