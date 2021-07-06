package com.appservice.ui.testimonial.newflow.add

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.appservice.R
import com.appservice.constant.IntentConstant
import com.appservice.databinding.FragmentTestimonialAddEditBinding
import com.appservice.ui.catalog.widgets.ClickType
import com.appservice.ui.catalog.widgets.ImagePickerBottomSheet
import com.appservice.ui.testimonial.newflow.base.BaseTestimonialFragment
import com.appservice.ui.testimonial.newflow.model.*
import com.framework.extensions.gone
import com.framework.extensions.observeOnce
import com.framework.extensions.visible
import com.framework.glide.util.glideLoad
import com.framework.imagepicker.ImagePicker
import com.framework.pref.UserSessionManager
import java.io.ByteArrayOutputStream

class TestimonialAddEditFragment : BaseTestimonialFragment<FragmentTestimonialAddEditBinding>() {

  private var dataItem: DataItem? = null
  private var isEdit: Boolean = false
  private var menuDelete: MenuItem? = null
  private var profileImage: ProfileImageTestimonial? = null
  private var imageUri: Uri? = null
  private var isImageUpdated: Boolean? = null
  private var imageIsChange: Boolean? = null


  companion object {
    @JvmStatic
    fun newInstance(bundle: Bundle? = null): TestimonialAddEditFragment {
      val fragment = TestimonialAddEditFragment()
      fragment.arguments = bundle
      return fragment
    }
  }


  override fun onCreateView() {
    this.dataItem = arguments?.getSerializable(IntentConstant.TESTIMONIAL_DATA.name) as? DataItem
    sessionLocal = UserSessionManager(requireActivity())
    isEdit = dataItem != null
    if (isEdit) {
      setData(dataItem?.testimonialId)
    } else if (dataItem == null) {
      this.dataItem = DataItem()
      binding?.rivDeleteImage?.gone()
    }
    setOnClickListener(binding?.btnSave, binding?.rivDeleteImage, binding?.rivChangeImage)
  }

  private fun setData(testimonialId: String?) {
    showProgress()
    viewModel?.getTestimonialDetails(testimonialId)?.observeOnce(viewLifecycleOwner, {
      hideProgress()
      when (it.isSuccess()) {
        true -> {
          this.dataItem = (it as? TestimonialResponse)?.result
          setViews(dataItem)
        }
        else -> {
        }
      }

    })
  }

  private fun setViews(dataItem: DataItem?) {
    if (dataItem?.profileImage?.imageId.isNullOrEmpty().not()) {
      binding?.rivDeleteImage?.visible()
      binding?.rivChangeImage?.visible()
      setImage(listOf(dataItem?.profileImage?.tileImage!!))
      }
     else {
      binding?.rivDeleteImage?.gone()
    }
    binding?.etvDesignation?.setText(dataItem?.reviewerTitle)
    binding?.etvName?.setText(dataItem?.reviewerName)
    binding?.etvTestimonialTitle?.setText(dataItem?.testimonialTitle)
    binding?.etvReview?.setText(dataItem?.message)
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.btnSave -> {
        if (isValid())
          updateCreateProfile()
      }
      binding?.rivDeleteImage -> {
        deleteImage()
      }
      binding?.rivChangeImage -> {
        openImagePicker()
      }
    }
  }
  private fun finishAndGoBack() {
    val intent = Intent()
    intent.putExtra(IntentConstant.IS_UPDATED.name, true)
    requireActivity().setResult(AppCompatActivity.RESULT_OK, intent)
    requireActivity().finish()
  }
  private fun updateCreateProfile() {
    showProgress()
    if (isEdit) {
      viewModel?.updateTestimonial(dataItem)?.observeOnce(viewLifecycleOwner, {
        hideProgress()
        when (it.isSuccess()) {
          true -> {
            showShortToast(getString(R.string.updated))
            finishAndGoBack()
          }
          else -> {
            showShortToast(getString(R.string.unable_to_update_testimonial))

          }
        }
      })
    } else {
      viewModel?.createTestimonial(dataItem)?.observeOnce(viewLifecycleOwner, {
        hideProgress()
        when (it.isSuccess()) {
          true -> {
            showShortToast(getString(R.string.testimonial_created))
            finishAndGoBack()
          }
          else -> {
    showShortToast(getString(R.string.unable_to_create_testimonial))
          }
        }
      })
    }
  }


  private fun openImagePicker() {
    val filterSheet = ImagePickerBottomSheet()
    filterSheet.isHidePdf(true)
    filterSheet.onClicked = { openImagePicker(it) }
    filterSheet.show(
      this@TestimonialAddEditFragment.parentFragmentManager,
      ImagePickerBottomSheet::class.java.name
    )
  }

  private fun openImagePicker(it: ClickType) {
    val type = if (it == ClickType.CAMERA) ImagePicker.Mode.CAMERA else ImagePicker.Mode.GALLERY
    ImagePicker.Builder(baseActivity)
      .mode(type)
      .compressLevel(ImagePicker.ComperesLevel.SOFT).directory(ImagePicker.Directory.DEFAULT)
      .extension(ImagePicker.Extension.PNG).allowMultipleImages(false)
      .enableDebuggingMode(true).build()
  }

  private fun updateImage() {
    showProgress(getString(R.string.uploading_image))
    viewModel?.updateTestimonialImage(UpdateTestimonialImage(profileImage,dataItem?.testimonialId))?.observeOnce(viewLifecycleOwner,{
      hideProgress()
      when(it.isSuccess()){
        true->{
          showShortToast(getString(R.string.image_uploaded_successfully))
        }
        else->{
          showShortToast(getString(R.string.error_while_uploading_image))
        }
      }
    })
  }

  private fun deleteImage() {
    showProgress()
    viewModel?.deleteTestimonialImage(DeleteTestimonialRequestNew(dataItem?.testimonialId))?.observeOnce(viewLifecycleOwner,{
      hideProgress()
      if (it.isSuccess()){
        showShortToast(getString(R.string.image_is_deleted))
      }else{
        showShortToast(getString(R.string.unable_to_delete_image))
      }
    })
    binding?.civTestimonialImage?.setImageDrawable(null)
    imageUri = null
    dataItem?.profileImage = null
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    when {
      requestCode == ImagePicker.IMAGE_PICKER_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK -> {
        val mPaths = data?.getSerializableExtra(ImagePicker.EXTRA_IMAGE_PATH) as List<String>
        setImage(mPaths)
        isImageUpdated = true
        if (isEdit)
          imageIsChange = true
      }


    }
    when (isEdit && imageIsChange != null && imageIsChange == true && isValid()) {
      true -> updateImage()
    }
  }

  fun setImage(mPaths: List<String>) {
    this.imageUri = Uri.parse(mPaths[0])
    binding?.civTestimonialImage?.let {
      activity?.glideLoad(
        it,
        imageUri.toString(),
        R.drawable.placeholder_image_n
      )
    }
    binding?.rivChangeImage?.visible()
    binding?.rivDeleteImage?.visible()
  }

  fun isValid(): Boolean {
    val designation = binding?.etvDesignation?.text
    val name = binding?.etvName?.text
    val testimonialTitle = binding?.etvTestimonialTitle?.text
    val review = binding?.etvReview?.text

    when {
      (imageUri.toString() == "null" || imageUri == null || imageUri.toString()
        .isEmpty() || imageUri.toString().isBlank()) -> {
        showLongToast(getString(R.string.please_choose_image))
        return false
      }
      name.isNullOrBlank() -> {
        showShortToast(getString(R.string.please_enter_name))
        return false
      }
      designation.isNullOrBlank() -> {
        showShortToast(getString(R.string.please_enter_designation))
        return false
      }
      testimonialTitle.isNullOrBlank() -> {
        showShortToast(getString(R.string.please_enter_title))
        return false
      }
      review.isNullOrBlank() -> {
        showShortToast(getString(R.string.please_enter_review))
        return false
      }
    }
    if (isImageUpdated == true) {
      val imageExtension: String? =
        imageUri?.toString()?.substring(imageUri.toString().lastIndexOf("."))
      val imageToByteArray: ByteArray = imageToByteArray()
      this.profileImage =
        ProfileImageTestimonial(
          image = "data:image/png;base64,${
            Base64.encodeToString(
              imageToByteArray,
              Base64.DEFAULT
            )
          }", fileName = "$name$imageExtension", imageFileType = imageExtension?.removePrefix(".")
        )
    }
    dataItem?.message = review.toString() ?: ""
    dataItem?.reviewerName = name?.toString() ?: ""
    dataItem?.testimonialTitle = testimonialTitle?.toString() ?: ""
    dataItem?.reviewerTitle = designation?.toString() ?: ""
    dataItem?.profileImage = profileImage
      dataItem?.floatingPointId = sessionLocal.fPID
      dataItem?.floatingPointTag = sessionLocal.fpTag

    return true
  }

  private fun imageToByteArray(): ByteArray {
    val bm: Bitmap = BitmapFactory.decodeFile(imageUri?.toString())
    val byteArrayOutStream = ByteArrayOutputStream()
    bm.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutStream) // bm is the bitmap object
    return byteArrayOutStream.toByteArray()
  }
}