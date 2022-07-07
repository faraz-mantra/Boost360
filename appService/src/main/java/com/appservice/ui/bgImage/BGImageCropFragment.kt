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
import com.appservice.utils.openImagePickerSheet
import com.bumptech.glide.Glide
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.framework.glide.util.loadGifGlide
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
import com.framework.webengageconstant.START_VIEW
import java.io.File

class BGImageCropFragment : AppBaseFragment<FragmentCropZoomBinding, BaseViewModel>() {

  private var imagePath: String? = null
  private var bitmap: Bitmap? = null
  private var validationStat = false

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

  lateinit var cropAdapter:CropAdapter
  override fun onCreateView() {
    super.onCreateView()
    WebEngageController.trackEvent(BACKGROUND_IMAGE_CROP_LOAD, START_VIEW, sessionLocal.fpTag)
    imagePath = arguments?.getString(BK_IMAGE_PATH)

   // setImageOnUi()
    viewListeners()
    setOnClickListener(binding?.btnDone)
    initData()
  }

  fun initData(){
    bitmap = BitmapFactory.decodeFile(imagePath)
    if (bitmap == null || imagePath.isNullOrEmpty()) {
      showShortToast("File not created, please try again!")
      baseActivity.finish()
      return
    }
    cropAdapter = if (imagePath?.endsWith(".gif") == true){
      GifImage()
    }else{
      NormalImage()
    }

    cropAdapter.showInUI()
    cropAdapter.validate()

  }


  private fun imageErrorView(errorText: SpannableString) {
    validationStat = false
    binding?.layoutImageMisConfig?.visible()
    binding?.tvSliderSugg?.gone()
    binding?.tvImgDesc?.text = errorText
    binding?.btnDone?.backgroundTintList = ContextCompat.getColorStateList(baseActivity, R.color.red_E39595)
    binding?.btnDone?.text = resources.getString(
      R.string.change_image
    )
    binding?.layoutSeek?.gone()
  }

  private fun imageSuccessView() {
    validationStat = true
    binding?.layoutImageMisConfig?.gone()
    binding?.tvSliderSugg?.visible()
    binding?.layoutSeek?.visible()
    binding?.btnDone?.backgroundTintList = ContextCompat.getColorStateList(baseActivity, R.color.colorPrimary)
    binding?.btnDone?.text = resources.getString(
      R.string.crop_picture
    )
  }

  private fun viewListeners() {
    binding?.slider?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
      override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
        if (p1 != 100) {
          binding?.cropImg?.setImageBitmap(bitmap?.zoom(p1.toFloat() / 100))
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
    return (bitmap.width.div(gcd) == 16 && bitmap.height.div(gcd) == 7)
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.btnDone -> {
        val imgFile = cropAdapter.getFile()
        if (imgFile?.exists() == true) {
          if (validationStat){
            startBackgroundActivity(
              FragmentType.BACKGROUND_IMAGE_PREVIEW,
              Bundle().apply { putString(BGImagePreviewFragment.BK_IMAGE_PATH, imgFile.absolutePath) },
              isResult = true
            )
          }else{
            openImagePickerSheet(requireActivity(),parentFragmentManager)
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
        imagePath = mPaths?.get(0)
        initData()
      }
    }
  }

  interface CropAdapter{
      fun showInUI()

      fun validate()

      fun getFile():File?
  }

  inner class NormalImage(): CropAdapter{

    override fun showInUI() {
      binding?.defaultImg?.gone()
      binding?.cropImg?.visible()
      binding?.layoutSeek?.visible()
      binding?.tvImgSuggestion?.visible()
      binding?.cropImg?.setImageBitmap(Utility.rotateImageIfRequired(bitmap!!, imagePath))
      val options = BitmapFactory.Options()
      options.inScaled = false
      binding?.cropImg?.setImageBitmap(bitmap)
      binding?.slider?.progress=0
    }

    override fun validate() {
      if ((bitmap?.width ?: 0) >= 1600 && (bitmap?.height ?: 0) >= 700) {
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

    override fun getFile(): File? {
      val imgFile = binding?.cropImg?.croppedImage?.saveBitmap()
      return imgFile
    }

  }

  inner class GifImage(): CropAdapter{

    override fun showInUI() {
      binding?.defaultImg?.visible()
      binding?.cropImg?.gone()
      binding?.layoutSeek?.gone()
      binding?.tvImgSuggestion?.gone()
      Glide.with(this@BGImageCropFragment)
        .asGif()
        .load(File(imagePath))
        .into(binding?.defaultImg!!)
    }

    override fun validate() {
      validationStat=true
      binding?.btnDone?.text = resources.getString(
        R.string.crop_picture
      )
      binding?.btnDone?.backgroundTintList = ContextCompat.getColorStateList(baseActivity, R.color.colorPrimary)
      if ((bitmap?.width ?: 0) >= 1600 && (bitmap?.height ?: 0) >= 700) {
        if (checkAspectRatio()) {
          imageSuccessView()
        } else {
          binding?.tvImgDesc?.text= getString(R.string.recomended_aspect_ratio_16_7)
        }
      } else {
        binding?.tvImgDesc?.text= getString(R.string.recomended_res_is_1600_700)
      }
    }

    override fun getFile(): File? {
      return File(imagePath)
    }

  }

}