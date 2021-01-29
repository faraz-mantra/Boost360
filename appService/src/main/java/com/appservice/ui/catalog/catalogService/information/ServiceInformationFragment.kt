package com.appservice.ui.catalog.catalogService.information

import android.content.Intent
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.constant.FragmentType
import com.appservice.constant.IntentConstant
import com.appservice.constant.RecyclerViewActionType
import com.appservice.databinding.FragmentServiceInformationBinding
import com.appservice.model.FileModel
import com.appservice.model.KeySpecification
import com.appservice.model.auth_3
import com.appservice.model.serviceProduct.Product
import com.appservice.model.serviceProduct.addProductImage.deleteRequest.ProductImageDeleteRequest
import com.appservice.model.serviceProduct.addProductImage.response.DataImage
import com.appservice.model.serviceProduct.gstProduct.response.DataG
import com.appservice.recyclerView.AppBaseRecyclerViewAdapter
import com.appservice.recyclerView.BaseRecyclerViewItem
import com.appservice.recyclerView.RecyclerItemClickListener
import com.appservice.ui.catalog.startFragmentActivity
import com.appservice.ui.catalog.widgets.ClickType
import com.appservice.ui.catalog.widgets.GstDetailsBottomSheet
import com.appservice.ui.catalog.widgets.ImagePickerBottomSheet
import com.appservice.utils.WebEngageController
import com.appservice.viewmodel.ServiceViewModel
import com.framework.extensions.observeOnce
import com.framework.imagepicker.ImagePicker
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class ServiceInformationFragment : AppBaseFragment<FragmentServiceInformationBinding, ServiceViewModel>(), RecyclerItemClickListener {

  private var product: Product? = null
  private var isEdit: Boolean? = null
  private var tagList = ArrayList<String>()
  private var specList: ArrayList<KeySpecification> = arrayListOf(KeySpecification())
  private var secondaryImage: ArrayList<FileModel> = ArrayList()
  private var adapterSpec: AppBaseRecyclerViewAdapter<KeySpecification>? = null
  private var adapterImage: AppBaseRecyclerViewAdapter<FileModel>? = null
  private var ordersQuantity: Int = 0

  private var secondaryDataImage: ArrayList<DataImage>? = null
  private var gstProductData: DataG? = null

  companion object {
    fun newInstance(): ServiceInformationFragment {
      return ServiceInformationFragment()
    }
  }

  override fun getLayout(): Int {
    return R.layout.fragment_service_information
  }

  override fun getViewModelClass(): Class<ServiceViewModel> {
    return ServiceViewModel::class.java
  }

  override fun onCreateView() {
    super.onCreateView()
    WebEngageController.trackEvent("Service other information catalogue load", "SERVICE CATALOGUE ADD/UPDATE", "")

    setOnClickListener(binding?.cbFacebookPage, binding?.cbGoogleMerchantCenter, binding?.cbTwitterPage,
            binding?.civIncreaseQuantityOrder, binding?.civDecreseQuantityOrder, binding?.btnAddTag, binding?.btnAddSpecification,
            binding?.btnConfirm, binding?.btnClickPhoto, binding?.edtGst)
    product = arguments?.getSerializable(IntentConstant.PRODUCT_DATA.name) as? Product
    isEdit = (product != null && product?.productId.isNullOrEmpty().not())
    gstProductData = arguments?.getSerializable(IntentConstant.PRODUCT_GST_DETAIL.name) as? DataG
    secondaryImage = (arguments?.getSerializable(IntentConstant.NEW_FILE_PRODUCT_IMAGE.name) as? ArrayList<FileModel>)
            ?: ArrayList()
    tagList = product?.tags ?: ArrayList()
    specList = if (product?.otherSpecification.isNullOrEmpty()) arrayListOf(KeySpecification()) else product?.otherSpecification!!
    if (isEdit == true) {
      secondaryDataImage = arguments?.getSerializable(IntentConstant.PRODUCT_IMAGE.name) as? ArrayList<DataImage>
      if (secondaryImage.isNullOrEmpty()) secondaryDataImage?.forEach { secondaryImage.add(FileModel(pathUrl = it.image?.url)) }
    }
    setUiText()
    serviceTagsSet()
    specificationAdapter()
//    val rangeFilter = InputFilterIntRange(0, 100)
//    binding?.edtGst?.filters = arrayOf<InputFilter>(rangeFilter)
//    binding?.edtGst?.onFocusChangeListener = rangeFilter
  }

  private fun setUiText() {
    ordersQuantity = product?.maxCodOrders!!
//    binding?.edtServiceCategory?.setText(product?.category ?: "")
    binding?.cetSpecKey?.setText(product?.keySpecification?.key)
    binding?.cetSpecValue?.setText(product?.keySpecification?.value)
    binding?.edtBrand?.setText(product?.brandName ?: "")
    binding?.ctvQuantityOrderStatus?.text = ordersQuantity.toString()
    if (gstProductData != null) binding?.edtGst?.setText("${(gstProductData?.gstSlab ?: 0.0).toInt()} %")
    setAdapter()
    val listYesNo = mutableListOf(SpinnerImageModel("YES" to true, R.drawable.ic_dot_green), SpinnerImageModel("NO" to false, R.drawable.ic_dot_red))
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

  private fun specificationAdapter() {
    binding?.rvSpecification?.apply {
      adapterSpec = AppBaseRecyclerViewAdapter(baseActivity, specList, this@ServiceInformationFragment)
      adapter = adapterSpec
    }
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    super.onCreateOptionsMenu(menu, inflater)
//    inflater.inflate(R.menu.menu_product_info, menu)
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.edtGst -> openGStDetail()
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
          ordersQuantity > 0 -> {
            --ordersQuantity
          }
        }
        binding?.ctvQuantityOrderStatus?.text = ordersQuantity.toString()

      }
//      binding?.weeklyAppointmentSchedule -> {
//        startFragmentActivity(FragmentType.TIMING_STAFF, isResult = true)
//
//      }
    }
  }

  private fun openImagePicker() {
    val filterSheet = ImagePickerBottomSheet()
    filterSheet.isHidePdf(true)
    filterSheet.onClicked = { openImagePicker(it) }
    filterSheet.show(this@ServiceInformationFragment.parentFragmentManager, ImagePickerBottomSheet::class.java.name)
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
//    val serviceCategory = binding?.edtServiceCategory?.text?.toString() ?: ""
    val spinnerCod = binding?.spinnerCod?.selectedItem as SpinnerImageModel
    val spinnerOnlinePayment = binding?.spinnerOnlinePayment?.selectedItem as SpinnerImageModel
    val brand = binding?.edtBrand?.text?.toString() ?: ""
    val keySpecification = binding?.cetSpecKey?.text?.toString() ?: ""
    val valSpecification = binding?.cetSpecValue?.text?.toString() ?: ""
    val gst = (binding?.edtGst?.text?.toString() ?: "").replace("%", "").trim()
    val otherSpec = (specList.filter { it.key.isNullOrEmpty().not() && it.value.isNullOrEmpty().not() } as? ArrayList<KeySpecification>)
            ?: ArrayList()
    when {
//      secondaryImage.isNullOrEmpty() -> {
//        showLongToast("Please select at least one secondary image.")
//        return
//      }
//      serviceCategory.isNullOrEmpty() -> {
//        showLongToast("Service category field can't empty.")
//        return
//      }
//      brand.isNullOrEmpty() -> {
//        showLongToast("Brand name field can't empty.")
//        return
//      }
//      tagList.isNullOrEmpty() -> {
//        showLongToast("Please enter at least one service tag.")
//        return
//      }
//      specList.isNullOrEmpty() -> {
//        showLongToast("Please enter at least one service specification.")
//        return
//      }
      else -> {
        WebEngageController.trackEvent("Other information confirm", "SERVICE CATALOGUE ADD/UPDATE", "")
//        product?.category = serviceCategory
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
        product?.keySpecification?.key = keySpecification
        product?.keySpecification?.value = valSpecification
        product?.maxCodOrders = ordersQuantity
        product?.otherSpecification = otherSpec
        if (gstProductData == null) gstProductData = DataG()
        gstProductData?.gstSlab = gst.toDoubleOrNull() ?: 0.0
        val output = Intent()
        output.putExtra(IntentConstant.PRODUCT_DATA.name, product)
        output.putExtra(IntentConstant.NEW_FILE_PRODUCT_IMAGE.name, secondaryImage)
        output.putExtra(IntentConstant.PRODUCT_GST_DETAIL.name, gstProductData)
        baseActivity.setResult(AppCompatActivity.RESULT_OK, output)
        baseActivity.finish()
      }
    }

  }

  private fun serviceTagsSet() {
    binding?.chipsService?.removeAllViews()
    tagList.forEach { tag ->
      val mChip: Chip = baseActivity.layoutInflater.inflate(R.layout.item_chip, binding?.chipsService, false) as Chip
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
    }
  }

  private fun secondaryImage(mPaths: ArrayList<String>) {
    if (secondaryImage.size < 8) {
      if (mPaths.size + secondaryImage.size > 8) showLongToast(getString(R.string.only_8_files_are_allowed_discarding_the_rest))
      var index: Int = secondaryImage.size
      while (index < 8 && mPaths.isNotEmpty()) {
        secondaryImage.add(FileModel(path = mPaths[0]))
        mPaths.removeAt(0)
        index++
      }
      setAdapter()
    } else showLongToast(getString(R.string.only_8_files_are_allowed))
  }

  private fun setAdapter() {
    if (adapterImage == null) {
      binding?.rvAdditionalDocs?.apply {
        adapterImage = AppBaseRecyclerViewAdapter(baseActivity, secondaryImage, this@ServiceInformationFragment)
        adapter = adapterImage
      }
    } else adapterImage?.notifyDataSetChanged()
  }

  override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
    when (actionType) {
      RecyclerViewActionType.IMAGE_CLEAR_CLICK.ordinal -> {
        val data = item as? FileModel
        if (isEdit == true && data?.pathUrl.isNullOrEmpty().not()) {
          val dataImage = secondaryDataImage?.firstOrNull { it.image?.url == data?.pathUrl }
                  ?: return
          showProgress(getString(R.string.removing_image))
          val request = ProductImageDeleteRequest()
          request.setQueryData(dataImage.id)
          viewModel?.deleteProductImage(auth_3, request)?.observeOnce(viewLifecycleOwner, Observer {
            if (it.status == 200 || it.status == 201 || it.status == 202) {
              secondaryDataImage?.remove(dataImage)
              secondaryImage.remove(data)
              setAdapter()
            } else showLongToast(getString(R.string.removing_image_failed_please_try_again))
            hideProgress()
          })
        } else {
          secondaryImage.remove(data)
          setAdapter()
        }

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
    gstSheet.show(this@ServiceInformationFragment.parentFragmentManager, ImagePickerBottomSheet::class.java.name)
  }

  fun onNavPressed() {
    dialogLogout()
  }

  private fun dialogLogout() {
    MaterialAlertDialogBuilder(baseActivity, R.style.MaterialAlertDialogTheme)
            .setTitle(getString(R.string.information_not_saved)).setMessage(getString(R.string.you_have_unsaved_information_do_you_still_want_to_close))
            .setNegativeButton(getString(R.string.no)) { d, _ -> d.dismiss() }.setPositiveButton(getString(R.string.yes)) { d, _ ->
              baseActivity.finish()
              d.dismiss()
            }.show()
  }

}

data class SpinnerImageModel(var state: Pair<String, Boolean>, var resId: Int) {

}
