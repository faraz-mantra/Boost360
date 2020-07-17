package com.catlogservice.ui.service

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.catlogservice.R
import com.catlogservice.base.AppBaseFragment
import com.catlogservice.constant.FragmentType
import com.catlogservice.databinding.FragmentServiceDetailBinding
import com.catlogservice.ui.startFragmentActivity
import com.catlogservice.ui.widgets.*
import com.catlogservice.utils.getBitmap
import com.catlogservice.viewmodel.ServiceViewModel
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.framework.imagepicker.ImagePicker
import java.io.File

class ServiceDetailFragment : AppBaseFragment<FragmentServiceDetailBinding, ServiceViewModel>() {

  private var serviceImage: File? = null

  companion object {
    fun newInstance(): ServiceDetailFragment {
      return ServiceDetailFragment()
    }
  }

  override fun getLayout(): Int {
    return R.layout.fragment_service_detail
  }

  override fun getViewModelClass(): Class<ServiceViewModel> {
    return ServiceViewModel::class.java
  }

  override fun onCreateView() {
    super.onCreateView()
    binding?.vwChangeDeliverConfig?.paintFlags = Paint.UNDERLINE_TEXT_FLAG
    binding?.vwPaymentConfig?.paintFlags = Paint.UNDERLINE_TEXT_FLAG
    setOnClickListener(binding?.vwChangeDeliverConfig, binding?.vwChangeDeliverLocation, binding?.vwPaymentConfig, binding?.vwSavePublish, binding?.imageAddBtn, binding?.clearImage)
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.imageAddBtn -> openImagePicker()
      binding?.clearImage -> clearImage()
      binding?.vwChangeDeliverConfig -> showServiceDeliveryConfigBottomSheet()
      binding?.vwChangeDeliverLocation -> showServiceDeliveryLocationBottomSheet()
      binding?.vwPaymentConfig -> showPaymentConfigBottomSheet()
      binding?.vwSavePublish -> startFragmentActivity(FragmentType.SERVICE_INFORMATION, Bundle())
    }
  }

  private fun clearImage() {
    binding?.imageAddBtn?.visible()
    binding?.clearImage?.gone()
    binding?.serviceImageView?.gone()
    serviceImage = null
  }

  private fun openImagePicker() {
    val filterSheet = ImagePickerBottomSheet()
    filterSheet.onClicked = { openImagePicker(it) }
    filterSheet.show(this@ServiceDetailFragment.parentFragmentManager, ImagePickerBottomSheet::class.java.name)
  }


  private fun openImagePicker(it: ClickType) {
    val type = when (it) {
      ClickType.CAMERA -> ImagePicker.Mode.CAMERA
      ClickType.GALLERY -> ImagePicker.Mode.GALLERY
    }
    ImagePicker.Builder(baseActivity)
        .mode(type)
        .compressLevel(ImagePicker.ComperesLevel.SOFT).directory(ImagePicker.Directory.DEFAULT)
        .extension(ImagePicker.Extension.PNG).allowMultipleImages(false)
        .scale(800, 800)
        .enableDebuggingMode(true).build()
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == ImagePicker.IMAGE_PICKER_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK) {
      val mPaths = data?.getSerializableExtra(ImagePicker.EXTRA_IMAGE_PATH) as List<String>
      if (mPaths.isNotEmpty()) {
        serviceImage = File(mPaths[0])
        binding?.imageAddBtn?.gone()
        binding?.clearImage?.visible()
        binding?.serviceImageView?.visible()
        serviceImage?.getBitmap()?.let { binding?.serviceImageView?.setImageBitmap(it) }
      }
    }
  }

  private fun showServiceDeliveryConfigBottomSheet() {
    val dialog = ServiceDeliveryConfigBottomSheet()
    dialog.show(parentFragmentManager, ServiceDeliveryConfigBottomSheet::class.java.name)
  }

  private fun showServiceDeliveryLocationBottomSheet() {
    val dialog = ServiceDeliveryBottomSheet()
    dialog.show(parentFragmentManager, ServiceDeliveryBottomSheet::class.java.name)
  }

  private fun showPaymentConfigBottomSheet() {
    val dialog = PaymentConfigBottomSheet()
    dialog.show(parentFragmentManager, PaymentConfigBottomSheet::class.java.name)
  }

}