package com.appservice.ui.testimonial

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.appservice.R
import com.appservice.constant.IntentConstant
import com.appservice.databinding.FragmentTestimonialAddEditBinding
import com.appservice.model.testimonial.AddTestimonialImageRequest
import com.appservice.model.testimonial.AddUpdateTestimonialRequest
import com.appservice.model.testimonial.ProfileImage
import com.appservice.model.testimonial.TestimonialAddResponse
import com.appservice.model.testimonial.response.TestimonialData
import com.appservice.ui.catalog.widgets.ClickType
import com.appservice.ui.catalog.widgets.ImagePickerBottomSheet
import com.appservice.utils.WebEngageController
import com.framework.extensions.gone
import com.framework.extensions.observeOnce
import com.framework.extensions.visible
import com.framework.glide.util.glideLoad
import com.framework.imagepicker.ImagePicker
import com.framework.webengageconstant.*
import java.io.ByteArrayOutputStream

class TestimonialAddEditFragment : BaseTestimonialFragment<FragmentTestimonialAddEditBinding>() {

  private var menuDelete: MenuItem? = null
  private var testimonialData: TestimonialData? = null
  private var request: AddUpdateTestimonialRequest? = null
  private var isUpdate: Boolean = false
  private var imageFileUri: Uri? = null

  companion object {
    @JvmStatic
    fun newInstance(bundle: Bundle? = null): TestimonialAddEditFragment {
      val fragment = TestimonialAddEditFragment()
      fragment.arguments = bundle
      return fragment
    }
  }

  override fun onCreateView() {
    super.onCreateView()
    testimonialData = arguments?.getSerializable(IntentConstant.OBJECT_DATA.name) as? TestimonialData
    isUpdate = testimonialData != null && testimonialData?.testimonialId.isNullOrEmpty().not()
    setOnClickListener(binding?.btnSave, binding?.addImage, binding?.viewTestimonial)
    WebEngageController.trackEvent(TESTIMONIAL_ADD_EDIT_PAGE, PAGE_VIEW, sessionLocal.fpTag)
    if (isUpdate) uiUpdate()
  }

  private fun uiUpdate() {
    setToolbarTitle(getString(R.string.testimonial_edit))
    menuDelete?.isVisible = true
    binding?.edtTitleName?.setText(testimonialData?.getTitleName())
    binding?.edtTitleDec?.setText(testimonialData?.getTitleDesc())
    binding?.edtTestimonialTitle?.setText(testimonialData?.getTestimonialName())
    binding?.edtTestimonialDesc?.setText(testimonialData?.getTestimonialDesc())
    val image = testimonialData?.getImageUrl()
    binding?.addImage?.visibility = if (image.isNullOrEmpty().not()) View.GONE else View.VISIBLE
    binding?.viewTestimonial?.visibility = if (image.isNullOrEmpty()) View.GONE else View.VISIBLE
    binding?.imageTestimonial?.let { baseActivity.glideLoad(it, image ?: "", R.drawable.placeholder_image_n) }
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.btnSave -> if (isValid()) apiSaveUpdate()
      binding?.addImage, binding?.viewTestimonial -> openImagePicker()
    }
  }

  private fun openImagePicker() {
    val filterSheet = ImagePickerBottomSheet()
    filterSheet.isHidePdf(true)
    filterSheet.onClicked = { openImagePicker(it) }
    filterSheet.show(this@TestimonialAddEditFragment.parentFragmentManager, ImagePickerBottomSheet::class.java.name)
  }

  private fun openImagePicker(it: ClickType) {
    val type = if (it == ClickType.CAMERA) ImagePicker.Mode.CAMERA else ImagePicker.Mode.GALLERY
    ImagePicker.Builder(baseActivity)
      .mode(type)
      .compressLevel(ImagePicker.ComperesLevel.SOFT).directory(ImagePicker.Directory.DEFAULT)
      .extension(ImagePicker.Extension.PNG).allowMultipleImages(false)
      .scale(800, 800)
      .enableDebuggingMode(true).build()
  }

  private fun apiSaveUpdate() {
    showProgress()
    val imageRequest = createImage()
    val requestService = if (isUpdate.not()) viewModel?.addTestimonial(request) else viewModel?.updateTestimonial(request.apply { this?.profileImage = imageRequest })
    requestService?.observeOnce(viewLifecycleOwner) {
      val data = it as? TestimonialAddResponse
      if (data?.isSuccess() == true && data.result.isNullOrEmpty().not()) {
        if (isUpdate.not()) addImageTestimonial(imageRequest, data.result!!) else {
          WebEngageController.trackEvent(TESTIMONIAL_UPDATED, EVENT_LABEL_UPDATE, sessionLocal.fpTag)
          setResult(getString(R.string.testimonial_update_success))
          hideProgress()
        }
      } else {
        showShortToast(it.errorFlowMessage() ?: getString(R.string.something_went_wrong))
        hideProgress()
      }
    }
  }

  private fun addImageTestimonial(imageRequest: ProfileImage?, id: String) {
    viewModel?.updateTestimonialImage(AddTestimonialImageRequest(imageRequest, id))?.observeOnce(viewLifecycleOwner) {
      WebEngageController.trackEvent(TESTIMONIAL_ADDED, ADDED, sessionLocal.fpTag)
      setResult(getString(R.string.testimonial_add_success))
      hideProgress()
    }
  }

  private fun setResult(msg: String) {
    showLongToast(msg)
    baseActivity.setResult(Activity.RESULT_OK, Intent().apply { putExtra(IntentConstant.IS_UPDATED.name, true) })
    baseActivity.finish()
  }

  private fun isValid(): Boolean {
    val titleName = binding?.edtTitleName?.text?.toString()
    val titleDec = binding?.edtTitleDec?.text?.toString()
    val testimonialTitle = binding?.edtTestimonialTitle?.text?.toString()
    val testimonialDesc = binding?.edtTestimonialDesc?.text?.toString()

    if (titleName.isNullOrEmpty()) {
      showShortToast(getString(R.string.name_field_can_not_empty))
      return false
    }
    /*if (titleDec.isNullOrEmpty()) {
      showShortToast(getString(R.string.company_field_can_not_empty))
      return false
    }*/
    if (testimonialTitle.isNullOrEmpty()) {
      showShortToast(getString(R.string.testimonial_title_field_can_not_empty))
      return false
    }
    if (testimonialDesc.isNullOrEmpty()) {
      showShortToast(getString(R.string.testimonial_description_field_can_not_empty))
      return false
    }
    request = AddUpdateTestimonialRequest(
      floatingPointId = sessionLocal.fPID, floatingPointTag = sessionLocal.fpTag,
      profileImage = null, reviewerCity = testimonialData?.reviewerCity ?: "",
      reviewerName = titleName, reviewerTitle = titleDec, testimonialTitle = testimonialTitle,
      testimonialBody = testimonialDesc, testimonialId = testimonialData?.testimonialId
    )
    return true
  }

  private fun createImage(): ProfileImage? {
    return if (imageFileUri != null) {
      val imageExtension: String? = imageFileUri?.toString()?.substring(imageFileUri.toString().lastIndexOf("."))
      val imageToByteArray: ByteArray = imageToByteArray(imageFileUri)
      ProfileImage(
        image = "data:image/png;base64,${Base64.encodeToString(imageToByteArray, Base64.DEFAULT)}",
        fileName = "testimonial_${System.currentTimeMillis()}.png",
        imageFileType = "png"//imageExtension?.removePrefix(".")
      )
    } else null
  }

  private fun imageToByteArray(uri: Uri?): ByteArray {
    val bm: Bitmap = BitmapFactory.decodeFile(uri?.toString())
    val byteArrayOutStream = ByteArrayOutputStream()
    bm.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutStream)
    return byteArrayOutStream.toByteArray()
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    super.onCreateOptionsMenu(menu, inflater)
    inflater.inflate(R.menu.ic_menu_delete_new, menu)
    menuDelete = menu.findItem(R.id.id_delete)
    menuDelete?.isVisible = isUpdate
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == ImagePicker.IMAGE_PICKER_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK) {
      val mPaths = data?.getSerializableExtra(ImagePicker.EXTRA_IMAGE_PATH) as List<String>
      if (mPaths.isNotEmpty()) {
        imageFileUri = Uri.parse(mPaths[0])
        if (imageFileUri?.path != "null" || imageFileUri?.path != null || imageFileUri != null) {
          binding?.addImage?.gone()
          binding?.viewTestimonial?.visible()
          binding?.imageTestimonial?.let { baseActivity.glideLoad(it, imageFileUri.toString(), R.drawable.placeholder_image_n) }
        } else {
          imageFileUri = null
          binding?.addImage?.visible()
          binding?.viewTestimonial?.gone()
        }
      }
    }
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return when (item.itemId) {
      R.id.id_delete -> {
        deleteTestimonialApi()
        true
      }
      else -> super.onOptionsItemSelected(item)
    }
  }

  private fun deleteTestimonialApi() {
    showProgress()
    viewModel?.deleteTestimonial(testimonialData?.testimonialId)?.observeOnce(viewLifecycleOwner) {
      if (it.isSuccess()) {
        WebEngageController.trackEvent(TESTIMONIAL_DELETED, DELETE, sessionLocal.fpTag)
        setResult(getString(R.string.testimonial_delete_success))
      } else showShortToast(it.errorFlowMessage() ?: getString(R.string.something_went_wrong))
      hideProgress()
    }
  }
}