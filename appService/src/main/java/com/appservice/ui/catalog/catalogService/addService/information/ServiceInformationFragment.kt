package com.appservice.ui.catalog.catalogService.addService.information

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.constant.FragmentType
import com.appservice.constant.IntentConstant
import com.appservice.constant.PreferenceConstant
import com.appservice.constant.RecyclerViewActionType
import com.appservice.databinding.FragmentServiceInformationBinding
import com.appservice.model.FileModel
import com.appservice.model.KeySpecification
import com.appservice.model.serviceProduct.BuyOnlineLink
import com.appservice.model.serviceTiming.BusinessHourTiming
import com.appservice.model.serviceTiming.ServiceTime
import com.appservice.model.serviceTiming.ServiceTiming
import com.appservice.model.servicev1.DeleteSecondaryImageRequest
import com.appservice.model.servicev1.ImageModel
import com.appservice.model.servicev1.ServiceModelV1
import com.appservice.recyclerView.AppBaseRecyclerViewAdapter
import com.appservice.recyclerView.BaseRecyclerViewItem
import com.appservice.recyclerView.RecyclerItemClickListener
import com.appservice.ui.catalog.startFragmentActivity
import com.appservice.ui.catalog.widgets.ClickType
import com.appservice.ui.catalog.widgets.GstDetailsBottomSheet
import com.appservice.ui.catalog.widgets.ImagePickerBottomSheet
import com.appservice.utils.WebEngageController
import com.appservice.viewmodel.ServiceViewModelV1
import com.framework.extensions.gone
import com.framework.extensions.observeOnce
import com.framework.extensions.visible
import com.framework.imagepicker.ImagePicker
import com.framework.webengageconstant.*
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.*
import kotlin.collections.ArrayList

class ServiceInformationFragment : AppBaseFragment<FragmentServiceInformationBinding, ServiceViewModelV1>(), RecyclerItemClickListener {

  private var product: ServiceModelV1? = null
  private var isEdit: Boolean = false
  private var tagList = ArrayList<String>()
  private var specList: ArrayList<KeySpecification> = arrayListOf(KeySpecification())
  private var secondaryImage: ArrayList<FileModel> = ArrayList()
  private var adapterSpec: AppBaseRecyclerViewAdapter<KeySpecification>? = null
  private var adapterImage: AppBaseRecyclerViewAdapter<FileModel>? = null
  private var ordersQuantity: Int = 0
  private var secondaryDataImage: ArrayList<ImageModel>? = null
  private var serviceTimingList: ArrayList<ServiceTiming>? = null

  companion object {
    fun newInstance(): ServiceInformationFragment {
      return ServiceInformationFragment()
    }
  }

  override fun getLayout(): Int {
    return R.layout.fragment_service_information
  }

  override fun getViewModelClass(): Class<ServiceViewModelV1> {
    return ServiceViewModelV1::class.java
  }

  override fun onCreateView() {
    super.onCreateView()
    WebEngageController.trackEvent(SERVICE_INFORMATION_CATALOGUE_LOAD, PAGE_VIEW, NO_EVENT_VALUE)
    setOnClickListener(
      binding?.cbFacebookPage,
      binding?.cbGoogleMerchantCenter,
      binding?.cbTwitterPage,
      binding?.civIncreaseQuantityOrder,
      binding?.civDecreseQuantityOrder,
      binding?.btnAddTag,
      binding?.btnAddSpecification,
      binding?.btnConfirm,
      binding?.btnClickPhoto,
      binding?.edtGst,
      binding?.weeklyAppointmentSchedule
    )
    this.product = arguments?.getSerializable(IntentConstant.PRODUCT_DATA.name) as? ServiceModelV1
    isEdit = (product != null && product?.productId.isNullOrEmpty().not())
    this.serviceTimingList =
      arguments?.getSerializable(IntentConstant.SERVICE_TIMING_DATA.name) as? ArrayList<ServiceTiming>
    if (this.serviceTimingList.isNullOrEmpty()) this.serviceTimingList =
      ServiceTiming().getEmptyDataServiceTiming(isEdit)
    secondaryImage =
      (arguments?.getSerializable(IntentConstant.NEW_FILE_PRODUCT_IMAGE.name) as? ArrayList<FileModel>)
        ?: ArrayList()
    tagList = product?.tags ?: ArrayList()
    specList =
      if (product?.otherSpecification.isNullOrEmpty()) arrayListOf(KeySpecification()) else product?.otherSpecification!!
    if (isEdit) {
      secondaryDataImage = product?.secondaryImages
      if (secondaryImage.isNullOrEmpty()) secondaryDataImage?.forEach {
        secondaryImage.add(
          FileModel(
            pathUrl = it.ActualImage
          )
        )
      }
    }
    setUiText()
    serviceTagsSet()
    specificationAdapter()
  }

  private fun setUiText() {
    bindTimingWithBusinessHour()
    ordersQuantity = product?.maxCodOrders ?: 0
    binding?.cetSpecKey?.setText(product?.keySpecification?.key ?: "")
    binding?.cetSpecValue?.setText(product?.keySpecification?.value ?: "")
    binding?.edtBrand?.setText(product?.brandName ?: "")
    binding?.cetWebsite?.setText(product?.BuyOnlineLink?.description ?: "")
    binding?.cetWebsiteValue?.setText(product?.BuyOnlineLink?.url ?: "")
    binding?.ctvQuantityOrderStatus?.text = ordersQuantity.toString()
    if (product?.isPriceToggleOn() == true) {
      binding?.edtGst?.visible()
      binding?.llGst?.visible()
    } else {
      binding?.edtGst?.gone()
      binding?.llGst?.gone()
    }
    if (product?.GstSlab != null) binding?.edtGst?.setText("${(product?.GstSlab ?: 0.0)} %")
    setAdapter()
    val listYesNo = mutableListOf(
      SpinnerImageModel("YES" to true, R.drawable.ic_dot_green),
      SpinnerImageModel("NO" to false, R.drawable.ic_dot_red)
    )
    binding?.spinnerOnlinePayment?.adapter = CustomDropDownAdapter(baseActivity, listYesNo)
    binding?.spinnerCod?.adapter = CustomDropDownAdapter(baseActivity, listYesNo)
    when (product?.codAvailable) {
      true -> binding?.spinnerCod?.setSelection(0)
      else -> binding?.spinnerCod?.setSelection(1)
    }
    when (product?.prepaidOnlineAvailable) {
      true -> binding?.spinnerOnlinePayment?.setSelection(0)
      else -> binding?.spinnerOnlinePayment?.setSelection(1)
    }
  }

  private fun bindTimingWithBusinessHour() {
    this.serviceTimingList?.forEach {
      when (ServiceTiming.WeekdayValue.fromFullName(it.day)) {
        ServiceTiming.WeekdayValue.SUNDAY -> {
          val startSunday =
            pref?.getString(PreferenceConstant.GET_FP_DETAILS_SUNDAY_START_TIME, "") ?: ""
          val endSunday =
            pref?.getString(PreferenceConstant.GET_FP_DETAILS_SUNDAY_END_TIME, "") ?: ""
          it.businessTiming = BusinessHourTiming(startTime = startSunday, endTime = endSunday)
        }
        ServiceTiming.WeekdayValue.MONDAY -> {
          val startMonday =
            pref?.getString(PreferenceConstant.GET_FP_DETAILS_MONDAY_START_TIME, "") ?: ""
          val endMonday =
            pref?.getString(PreferenceConstant.GET_FP_DETAILS_MONDAY_END_TIME, "") ?: ""
          it.businessTiming = BusinessHourTiming(startTime = startMonday, endTime = endMonday)
        }
        ServiceTiming.WeekdayValue.TUESDAY -> {
          val startTuesday =
            pref?.getString(PreferenceConstant.GET_FP_DETAILS_TUESDAY_START_TIME, "") ?: ""
          val endTuesday =
            pref?.getString(PreferenceConstant.GET_FP_DETAILS_TUESDAY_END_TIME, "") ?: ""
          it.businessTiming = BusinessHourTiming(startTime = startTuesday, endTime = endTuesday)
        }
        ServiceTiming.WeekdayValue.WEDNESDAY -> {
          val startWednesday =
            pref?.getString(PreferenceConstant.GET_FP_DETAILS_WEDNESDAY_START_TIME, "") ?: ""
          val endWednesday =
            pref?.getString(PreferenceConstant.GET_FP_DETAILS_WEDNESDAY_END_TIME, "") ?: ""
          it.businessTiming = BusinessHourTiming(startTime = startWednesday, endTime = endWednesday)
        }
        ServiceTiming.WeekdayValue.THURSDAY -> {
          val startThursday =
            pref?.getString(PreferenceConstant.GET_FP_DETAILS_THURSDAY_START_TIME, "") ?: ""
          val endThursday =
            pref?.getString(PreferenceConstant.GET_FP_DETAILS_THURSDAY_END_TIME, "") ?: ""
          it.businessTiming = BusinessHourTiming(startTime = startThursday, endTime = endThursday)
        }
        ServiceTiming.WeekdayValue.FRIDAY -> {
          val startFriday =
            pref?.getString(PreferenceConstant.GET_FP_DETAILS_FRIDAY_START_TIME, "") ?: ""
          val endFriday =
            pref?.getString(PreferenceConstant.GET_FP_DETAILS_FRIDAY_END_TIME, "") ?: ""
          it.businessTiming = BusinessHourTiming(startTime = startFriday, endTime = endFriday)
        }
        ServiceTiming.WeekdayValue.SATURDAY -> {
          val startSaturday =
            pref?.getString(PreferenceConstant.GET_FP_DETAILS_SATURDAY_START_TIME, "") ?: ""
          val endSaturday =
            pref?.getString(PreferenceConstant.GET_FP_DETAILS_SATURDAY_END_TIME, "") ?: ""
          it.businessTiming = BusinessHourTiming(startTime = startSaturday, endTime = endSaturday)
        }
      }
      if (it.time?.from.isNullOrEmpty() || it.time?.to.isNullOrEmpty()) it.time =
        ServiceTime(it.businessTiming?.startTime, it.businessTiming?.endTime)
    }
    val serviceTimingTxt = ServiceTiming().getStringActive(this.serviceTimingList)
    if (serviceTimingTxt.isNotEmpty()) {
      binding?.txtDaysActive?.text = serviceTimingTxt
      binding?.txtDaysActive?.visible()
    } else binding?.txtDaysActive?.gone()
  }

  private fun specificationAdapter() {
    binding?.rvSpecification?.apply {
      adapterSpec =
        AppBaseRecyclerViewAdapter(baseActivity, specList, this@ServiceInformationFragment)
      adapter = adapterSpec
    }
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.edtGst -> {
      } //openGStDetail()
      binding?.btnAddTag -> {
        val txtTag = binding?.edtServiceTag?.text.toString()
        if (txtTag.isNotEmpty()) {
          tagList.add(txtTag)
          serviceTagsSet()
          binding?.edtServiceTag?.setText("")
        }
      }
      binding?.btnAddSpecification -> {
        specList.add(KeySpecification())
        binding?.rvSpecification?.removeAllViewsInLayout()
        binding?.rvSpecification?.adapter = null
        specificationAdapter()
      }
      binding?.btnConfirm -> validateAnnGoBack()
      binding?.btnClickPhoto -> openImagePicker()
      binding?.civIncreaseQuantityOrder -> {
        ++ordersQuantity
        binding?.ctvQuantityOrderStatus?.text = ordersQuantity.toString()

      }
      binding?.civDecreseQuantityOrder -> {
        when {
          ordersQuantity > 0 -> --ordersQuantity
        }
        binding?.ctvQuantityOrderStatus?.text = ordersQuantity.toString()

      }
      binding?.weeklyAppointmentSchedule -> {
        val bundle = Bundle()
        bundle.putSerializable(IntentConstant.SERVICE_TIMING_DATA.name, this.serviceTimingList)
        bundle.putBoolean(IntentConstant.IS_EDIT.name, this.isEdit)
        startFragmentActivity(
          FragmentType.SERVICE_TIMING_FRAGMENT,
          bundle = bundle,
          isResult = true
        )
      }
    }
  }

  private fun openImagePicker() {
    val filterSheet = ImagePickerBottomSheet()
    filterSheet.isHidePdf(true)
    filterSheet.onClicked = { openImagePicker(it) }
    filterSheet.show(
      this@ServiceInformationFragment.parentFragmentManager,
      ImagePickerBottomSheet::class.java.name
    )
  }

  private fun openImagePicker(it: ClickType) {
    val type = if (it == ClickType.CAMERA) ImagePicker.Mode.CAMERA else ImagePicker.Mode.GALLERY
    ImagePicker.Builder(baseActivity)
      .mode(type)
      .compressLevel(ImagePicker.ComperesLevel.SOFT).directory(ImagePicker.Directory.DEFAULT)
      .extension(ImagePicker.Extension.PNG).allowMultipleImages(true)
      .scale(800, 800)
      .enableDebuggingMode(true).build()
  }

  private fun validateAnnGoBack() {
    val spinnerCod = binding?.spinnerCod?.selectedItem as SpinnerImageModel
    val spinnerOnlinePayment = binding?.spinnerOnlinePayment?.selectedItem as SpinnerImageModel
    val brand = binding?.edtBrand?.text?.toString() ?: ""
    val websiteName = binding?.cetWebsite?.text?.toString() ?: ""
    val websiteValue = binding?.cetWebsiteValue?.text?.toString() ?: ""
    val keySpecification = binding?.cetSpecKey?.text?.toString() ?: ""
    val valSpecification = binding?.cetSpecValue?.text?.toString() ?: ""
    val gst = (binding?.edtGst?.text?.toString() ?: "").replace("%", "").trim()
    val otherSpec = (specList.filter {
      it.key.isNullOrEmpty().not() && it.value.isNullOrEmpty().not()
    } as? ArrayList<KeySpecification>)
      ?: ArrayList()
    when {
      else -> {
        WebEngageController.trackEvent(SERVICE_INFORMATION_CONFIRM, CLICK, NO_EVENT_VALUE)
        product?.brandName = brand
        product?.tags = tagList
        when (spinnerCod.state.first) {
          "YES" -> product?.codAvailable = true
          "NO" -> product?.codAvailable = false
        }
        when (spinnerOnlinePayment.state.first) {
          "YES" -> product?.prepaidOnlineAvailable = true
          "NO" -> product?.prepaidOnlineAvailable = false
        }
        if (product?.keySpecification == null) product?.keySpecification = KeySpecification()
        product?.keySpecification?.key = keySpecification
        product?.keySpecification?.value = valSpecification
        product?.maxCodOrders = ordersQuantity
        product?.otherSpecification = otherSpec
        product?.BuyOnlineLink = BuyOnlineLink(description = websiteName, url = websiteValue)
        product?.GstSlab = 18//gst.toIntOrNull() ?: 0;
        val output = Intent()
        output.putExtra(IntentConstant.PRODUCT_DATA.name, product)
        output.putExtra(IntentConstant.NEW_FILE_PRODUCT_IMAGE.name, secondaryImage)
        output.putExtra(IntentConstant.SERVICE_TIMING_DATA.name, serviceTimingList)
        baseActivity.setResult(AppCompatActivity.RESULT_OK, output)
        baseActivity.finish()
      }
    }
  }

  private fun serviceTagsSet() {
    binding?.chipsService?.removeAllViews()
    tagList.forEach { tag ->
      val mChip: Chip = baseActivity.layoutInflater.inflate(
        R.layout.item_chip,
        binding?.chipsService,
        false
      ) as Chip
      mChip.text = tag
      mChip.setOnCloseIconClickListener {
        binding?.chipsService?.removeView(mChip)
        tagList.remove(tag)
      }
      binding?.chipsService?.addView(mChip)
    }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == ImagePicker.IMAGE_PICKER_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK) {
      val mPaths = data?.getSerializableExtra(ImagePicker.EXTRA_IMAGE_PATH) as ArrayList<String>
      secondaryImage(mPaths)
    } else if (resultCode == AppCompatActivity.RESULT_OK && requestCode == 101) {
      this.serviceTimingList =
        data?.getSerializableExtra(IntentConstant.SERVICE_TIMING_DATA.name) as? ArrayList<ServiceTiming>
      val serviceTimingTxt = ServiceTiming().getStringActive(this.serviceTimingList)
      if (serviceTimingTxt.isNotEmpty()) {
        binding?.txtDaysActive?.text = serviceTimingTxt
        binding?.txtDaysActive?.visible()
      } else binding?.txtDaysActive?.gone()
    }
  }


  private fun secondaryImage(mPaths: ArrayList<String>) {
    if (secondaryImage.size < 8) {
      if (mPaths.size + secondaryImage.size > 8) showLongToast(resources.getString(R.string.only_eight_files_are_allowed_discarding))
      var index: Int = secondaryImage.size
      while (index < 8 && mPaths.isNotEmpty()) {
        secondaryImage.add(FileModel(path = mPaths[0]))
        mPaths.removeAt(0)
        index++
      }
      setAdapter()
    } else showLongToast(resources.getString(R.string.only_eight_files_allowed))
  }

  private fun setAdapter() {
    if (adapterImage == null) {
      binding?.rvAdditionalDocs?.apply {
        adapterImage =
          AppBaseRecyclerViewAdapter(baseActivity, secondaryImage, this@ServiceInformationFragment)
        adapter = adapterImage
      }
    } else adapterImage?.notifyDataSetChanged()
  }

  override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
    when (actionType) {
      RecyclerViewActionType.IMAGE_CLEAR_CLICK.ordinal -> {
        val data = item as? FileModel
        if (isEdit && data?.pathUrl.isNullOrEmpty().not()) {
          val dataImage = secondaryDataImage?.firstOrNull { it.ActualImage == data?.pathUrl }
            ?: return
          showProgress(resources.getString(R.string.removing_image))
          val request = DeleteSecondaryImageRequest(product?.productId, dataImage.ImageId)
          viewModel?.deleteSecondaryImage(request)?.observeOnce(viewLifecycleOwner, Observer {
            if (it.status == 200 || it.status == 201 || it.status == 202) {
              secondaryDataImage?.remove(dataImage)
              secondaryImage.remove(data)
              setAdapter()
            } else showLongToast(resources.getString(R.string.removing_image_failed))
            hideProgress()
          })
        } else {
          secondaryImage.remove(data)
          setAdapter()
        }

      }
      RecyclerViewActionType.IMAGE_CHANGE.ordinal -> {
        val data = item as? FileModel
        if (isEdit && data?.pathUrl.isNullOrEmpty().not()) {
          val dataImage = secondaryDataImage?.firstOrNull { it.ActualImage == data?.pathUrl }
            ?: return
          showProgress(resources.getString(R.string.removing_image))
          val request = DeleteSecondaryImageRequest(product?.productId, dataImage.ImageId)
          viewModel?.deleteSecondaryImage(request)?.observeOnce(viewLifecycleOwner, {
            if (it.status == 200 || it.status == 201 || it.status == 202) {
              secondaryDataImage?.remove(dataImage)
              secondaryImage.remove(data)
              setAdapter()
            } else showLongToast(resources.getString(R.string.removing_image_failed))
            hideProgress()
          })
        } else {
          secondaryImage.remove(data)
          setAdapter()
        }
        openImagePicker()

      }
      RecyclerViewActionType.CLEAR_SPECIFICATION_CLICK.ordinal -> {
        if (specList.size > 1) {
          val element = item as? KeySpecification ?: return
          adapterSpec?.notifyItemRemoved(position)
          specList.remove(element)
        }
      }
      RecyclerViewActionType.UPDATE_SPECIFICATION_VALUE.ordinal -> {
        val element = item as? KeySpecification ?: return
        if (specList.size > position) {
          val data = specList[position]
          data.key = element.key
          data.value = element.value
        }
      }
    }
  }

  private fun openGStDetail() {
    val gstSheet = GstDetailsBottomSheet()
    gstSheet.onClicked = { binding?.edtGst?.setText("$it %") }
    gstSheet.show(
      this@ServiceInformationFragment.parentFragmentManager,
      ImagePickerBottomSheet::class.java.name
    )
  }

  fun onNavPressed() {
    dialogLogout()
  }

  private fun dialogLogout() {
    MaterialAlertDialogBuilder(baseActivity, R.style.MaterialAlertDialogTheme)
      .setTitle(resources.getString(R.string.information_not_saved))
      .setMessage(getString(R.string.you_have_unsaved_information_do_you_still_want_to_close))
      .setNegativeButton(getString(R.string.no)) { d, _ -> d.dismiss() }
      .setPositiveButton(getString(R.string.yes)) { d, _ ->
        baseActivity.finish()
        d.dismiss()
      }.show()
  }

}

data class SpinnerImageModel(var state: Pair<String, Boolean>, var resId: Int, var disable: Boolean = false) {

}
