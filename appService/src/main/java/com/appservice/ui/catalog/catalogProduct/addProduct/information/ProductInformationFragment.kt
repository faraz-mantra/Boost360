package com.appservice.ui.catalog.catalogProduct.addProduct.information

import android.content.Intent
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.constant.IntentConstant
import com.appservice.constant.RecyclerViewActionType
import com.appservice.databinding.FragmentProductInformationBinding
import com.appservice.model.FileModel
import com.appservice.model.KeySpecification
import com.appservice.model.auth_3
import com.appservice.model.serviceProduct.CatalogProduct
import com.appservice.model.serviceProduct.addProductImage.deleteRequest.ProductImageDeleteRequest
import com.appservice.model.serviceProduct.addProductImage.response.DataImage
import com.appservice.model.serviceProduct.gstProduct.response.GstData
import com.appservice.recyclerView.AppBaseRecyclerViewAdapter
import com.appservice.recyclerView.BaseRecyclerViewItem
import com.appservice.recyclerView.RecyclerItemClickListener
import com.appservice.ui.catalog.catalogService.addService.information.CustomDropDownAdapter
import com.appservice.ui.catalog.catalogService.addService.information.SpinnerImageModel
import com.appservice.ui.catalog.widgets.ClickType
import com.appservice.ui.catalog.widgets.GstDetailsBottomSheet
import com.appservice.ui.catalog.widgets.ImagePickerBottomSheet
import com.appservice.utils.WebEngageController
import com.appservice.viewmodel.ServiceViewModel
import com.framework.extensions.observeOnce
import com.framework.imagepicker.ImagePicker
import com.framework.webengageconstant.*
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ProductInformationFragment : AppBaseFragment<FragmentProductInformationBinding, ServiceViewModel>(), RecyclerItemClickListener {

  private var product: CatalogProduct? = null
  private var isEdit: Boolean =false
  private var tagList = ArrayList<String>()
  private var specList: ArrayList<KeySpecification> = arrayListOf(KeySpecification())
  private var secondaryImage: ArrayList<FileModel> = ArrayList()
  private var adapterSpec: AppBaseRecyclerViewAdapter<KeySpecification>? = null
  private var adapterImage: AppBaseRecyclerViewAdapter<FileModel>? = null
  private var availableStock = 0;
  private var availableCODStock = 0
  private var availableOnlineStock = 0

  private var secondaryDataImage: ArrayList<DataImage>? = null
  private var gstProductData: GstData? = null

  companion object {
    fun newInstance(): ProductInformationFragment {
      return ProductInformationFragment()
    }
  }

  override fun getLayout(): Int {
    return R.layout.fragment_product_information
  }

  override fun getViewModelClass(): Class<ServiceViewModel> {
    return ServiceViewModel::class.java
  }

  override fun onCreateView() {
    super.onCreateView()
    WebEngageController.trackEvent(PRODUCT_INFORMATION_CATALOGUE_LOAD, PAGE_VIEW, NO_EVENT_VALUE)
    setOnClickListener(
      binding?.btnAddTag, binding?.btnAddSpecification, binding?.btnConfirm, binding?.btnClickPhoto,
      binding?.edtGst, binding?.civDecreseStock, binding?.civIncreaseStock, binding?.civDecreaseCodQty,
      binding?.civIncreaseCodQty, binding?.civDecreaseOnlineQty, binding?.civIncreaseOnlineQty
    )
    product = arguments?.getSerializable(IntentConstant.PRODUCT_DATA.name) as? CatalogProduct
    isEdit = (product != null && product?.productId.isNullOrEmpty().not())
    gstProductData = arguments?.getSerializable(IntentConstant.PRODUCT_GST_DETAIL.name) as? GstData
    secondaryImage = (arguments?.getSerializable(IntentConstant.NEW_FILE_PRODUCT_IMAGE.name) as? ArrayList<FileModel>) ?: ArrayList()
    tagList = product?.tags ?: ArrayList()
    specList = if (product?.otherSpecification.isNullOrEmpty()) arrayListOf(KeySpecification()) else product?.otherSpecification!!
    if (isEdit == true) {
      secondaryDataImage = arguments?.getSerializable(IntentConstant.PRODUCT_IMAGE.name) as? ArrayList<DataImage>
      if (secondaryImage.isNullOrEmpty()) secondaryDataImage?.forEach {
        secondaryImage.add(FileModel(pathUrl = it.image?.url))
      }
    }
    setUiText()
    serviceTagsSet()
    specificationAdapter()
  }

  private fun setUiText() {
    binding?.specKey?.setText(product?.keySpecification?.key)
    binding?.specValue?.setText(product?.keySpecification?.value)
    availableStock = product?.availableUnits ?: 0
    availableCODStock = product?.maxCodOrders ?: 0
    availableOnlineStock = product?.maxPrepaidOnlineAvailable ?: 0
    binding?.edtBrand?.setText(product?.brandName ?: "")
    binding?.ctvCurrentStock?.text = product?.availableUnits.toString()
    if (gstProductData != null) {
      binding?.edtGst?.setText("${(gstProductData?.gstSlab ?: 0.0).toInt()} %")
      binding?.cetLength?.setText("${gstProductData?.length ?: 0.0}")
      binding?.cetHeight?.setText("${gstProductData?.height ?: 0.0}")
      binding?.cetThickness?.setText("${gstProductData?.width ?: 0.0}")
      binding?.cetWeight?.setText("${gstProductData?.weight ?: 0.0}")
    }
    setAdapter()
    stockAdapter()
    stockCODAdapter()
    stockOnlineAdapter()
    binding?.specKey?.setText(product?.keySpecification?.key)
    binding?.specValue?.setText(product?.keySpecification?.value)
  }


  private fun stockAdapter() {
    val stockAdapter = mutableListOf(
      SpinnerImageModel("Limited stock" to false, R.drawable.ic_dot_green),
      SpinnerImageModel("Unlimited stock" to false, R.drawable.ic_infinite),
      SpinnerImageModel("Out of stock" to false, R.drawable.ic_dot_red)
    )
    binding?.spinnerStock?.adapter = CustomDropDownAdapter(baseActivity, stockAdapter)
    when (availableStock) {
      -1 -> binding?.spinnerStock?.setSelection(1)
      0 -> binding?.spinnerStock?.setSelection(2)
      else -> binding?.spinnerStock?.setSelection(0)
    }
    binding?.spinnerStock?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
      override fun onItemSelected(p0: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {
        when (pos) {
          0 -> {
            binding?.llStockChange?.visibility = View.VISIBLE
            availableStock = product?.availableUnits ?: 0
            if (availableStock < 0) availableStock++
            binding?.ctvCurrentStock?.text = availableStock.toString()
          }
          1 -> {
            binding?.llStockChange?.visibility = View.INVISIBLE
            availableStock = -1
          }
          2 -> {
            binding?.llStockChange?.visibility = View.INVISIBLE
            availableStock = 0
            binding?.spinnerCod?.setSelection(1)
            binding?.spinnerOnline?.setSelection(1)
          }
        }
      }

      override fun onNothingSelected(p0: AdapterView<*>?) {
      }

    }
  }

  private fun stockCODAdapter() {
    val list = mutableListOf(
      SpinnerImageModel("Yes" to true, R.drawable.ic_dot_green),
      SpinnerImageModel("No" to false, R.drawable.ic_dot_red)
    )
    binding?.spinnerCod?.adapter = CustomDropDownAdapter(baseActivity, list)
    if (product?.codAvailable == true) binding?.spinnerCod?.setSelection(0) else binding?.spinnerCod?.setSelection(1)
    binding?.spinnerCod?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
      override fun onItemSelected(p0: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {
        if (pos == 0) {
          binding?.llCodChange?.visibility = View.VISIBLE
          product?.codAvailable = true
          binding?.ctvCurrentCodQty?.text = availableCODStock.toString()
        } else {
          binding?.llCodChange?.visibility = View.INVISIBLE
          product?.codAvailable = false
        }
      }

      override fun onNothingSelected(p0: AdapterView<*>?) {
      }

    }
  }

  private fun stockOnlineAdapter() {
    val list = mutableListOf(
      SpinnerImageModel("Yes" to true, R.drawable.ic_dot_green),
      SpinnerImageModel("No" to false, R.drawable.ic_dot_red)
    )
    binding?.spinnerOnline?.adapter = CustomDropDownAdapter(baseActivity, list)
    if (product?.prepaidOnlineAvailable == true) binding?.spinnerOnline?.setSelection(0) else binding?.spinnerOnline?.setSelection(1)
    binding?.spinnerOnline?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
      override fun onItemSelected(p0: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {
        if (pos == 0) {
          binding?.llOnlineChange?.visibility = View.VISIBLE
          product?.prepaidOnlineAvailable = true
          binding?.ctvCurrentOnlineQty?.text = availableOnlineStock.toString()
        } else {
          binding?.llOnlineChange?.visibility = View.INVISIBLE
          product?.prepaidOnlineAvailable = false
        }
      }

      override fun onNothingSelected(p0: AdapterView<*>?) {
      }

    }
  }

//  private fun setSpinners() {
//    val lengthArray = mutableListOf("INCH", "METERS")
//    val weightArray = mutableListOf("KGS", "MGS")
//    binding?.spinnerHeight?.adapter = ArrayAdapter(baseActivity, R.layout.support_simple_spinner_dropdown_item, lengthArray)
//    binding?.spinnerHeight?.isSelected = false
//    binding?.spinnerThickness?.adapter = ArrayAdapter(baseActivity, R.layout.support_simple_spinner_dropdown_item, lengthArray)
//    binding?.spinnerThickness?.isSelected = false
//    binding?.spinnerWeight?.adapter = ArrayAdapter(baseActivity, R.layout.support_simple_spinner_dropdown_item, weightArray)
//    binding?.spinnerWeight?.isSelected = false
//    binding?.spinnerLength?.adapter = ArrayAdapter(baseActivity, R.layout.support_simple_spinner_dropdown_item, lengthArray)
//    binding?.spinnerLength?.isSelected = false
//    binding?.spinnerHeight?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//      override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
//        if (p0?.selectedItem.toString() == lengthArray[0]) {
//          binding?.cetHeight?.setText(convertInchToMeters(binding?.cetHeight!!.text.toString().toDoubleOrNull()
//                  ?: 0.0).toString())
//        } else {
//          binding?.cetHeight?.setText(convertMetersToInch(binding?.cetHeight!!.text.toString().toDoubleOrNull()
//                  ?: 0.0).toString())
//        }
//
//      }
//
//      override fun onNothingSelected(p0: AdapterView<*>?) {
//      }
//
//    }
//    binding?.spinnerLength?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//      override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
//        if (p0?.selectedItem.toString() == lengthArray[0]) {
//          binding?.cetLength?.setText(convertInchToMeters(binding?.cetLength!!.text.toString().toDoubleOrNull()
//                  ?: 0.0).toString())
//        } else {
//          binding?.cetLength?.setText(convertMetersToInch(binding?.cetLength!!.text.toString().toDoubleOrNull()
//                  ?: 0.0).toString())
//        }
//      }
//
//      override fun onNothingSelected(p0: AdapterView<*>?) {
//      }
//
//    }
//    binding?.spinnerWeight?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//      override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
//        if (p0?.selectedItem.toString() == weightArray[0]) {
//          binding?.cetWeight?.setText(convertMgToKilo(binding?.cetWeight!!.text.toString().toDoubleOrNull()
//                  ?: 0.0).toString())
//        } else {
//          binding?.cetWeight?.setText(convertKiloToMg(binding?.cetWeight!!.text.toString().toDoubleOrNull()
//                  ?: 0.0).toString())
//        }
//      }
//
//      override fun onNothingSelected(p0: AdapterView<*>?) {
//      }
//
//    }
//    binding?.spinnerThickness?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//      override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
//        if (p0?.selectedItem.toString() == lengthArray[0]) {
//          binding?.cetThickness?.setText(convertInchToMeters(binding?.cetThickness!!.text.toString().toDoubleOrNull()
//                  ?: 0.0).toString())
//        } else {
//          binding?.cetThickness?.setText(convertMetersToInch(binding?.cetThickness!!.text.toString().toDoubleOrNull()
//                  ?: 0.0).toString())
//        }
//      }
//
//      override fun onNothingSelected(p0: AdapterView<*>?) {
//      }
//
//    }
//  }
//
//  private fun convertMgToKilo(mg: Double?): Double? {
//    return mg?.div(1000)
//  }
//
//  private fun convertKiloToMg(kg: Double?): Double? {
//    return kg?.times(1000)
//  }
//
//  private fun convertMetersToInch(length: Double?): Double? {
//    return length?.times(39.3701)
//  }
//
//  private fun convertInchToMeters(length: Double?): Double? {
//    return length?.div(39.3701)
//  }

  private fun specificationAdapter() {
    binding?.rvSpecification?.apply {
      adapterSpec =
        AppBaseRecyclerViewAdapter(baseActivity, specList, this@ProductInformationFragment)
      adapter = adapterSpec
    }
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.edtGst -> openGStDetail()
      binding?.btnAddTag -> {
        val txtTag = binding?.edtProductTag?.text.toString()
        if (txtTag.isNotEmpty()) {
          tagList.add(txtTag)
          serviceTagsSet()
          binding?.edtProductTag?.setText("")
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
      binding?.civDecreseStock -> {
        if (availableStock > 0) {
          availableStock--
          binding?.ctvCurrentStock?.text = availableStock.toString()
        }
      }
      binding?.civIncreaseStock -> {
        availableStock++
        binding?.ctvCurrentStock?.text = availableStock.toString()
      }
      binding?.civDecreaseCodQty -> {
        if (availableCODStock > 0) {
          availableCODStock--
          binding?.ctvCurrentCodQty?.text = availableCODStock.toString()
        }
      }
      binding?.civIncreaseCodQty -> {
        availableCODStock++
        binding?.ctvCurrentCodQty?.text = availableCODStock.toString()
      }
      binding?.civDecreaseOnlineQty -> {
        if (availableOnlineStock > 0) {
          availableOnlineStock--
          binding?.ctvCurrentOnlineQty?.text = availableOnlineStock.toString()
        }
      }
      binding?.civIncreaseOnlineQty -> {
        availableOnlineStock++
        binding?.ctvCurrentOnlineQty?.text = availableOnlineStock.toString()
      }
    }
  }

  private fun openImagePicker() {
    val filterSheet = ImagePickerBottomSheet()
    filterSheet.isHidePdf(true)
    filterSheet.onClicked = { openImagePicker(it) }
    filterSheet.show(
      this@ProductInformationFragment.parentFragmentManager,
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
//    val serviceCategory = binding?.edtProductCategory?.text?.toString() ?: ""
//    val spinnerCod = binding?.spinnerCod?.selectedItem as SpinnerImageModel
//    val spinnerOnlinePayment = binding?.spinnerOnlinePayment?.selectedItem as SpinnerImageModel
    val specLength = binding?.cetLength?.text.toString().toDoubleOrNull() ?: 0.0
    val specHeight = binding?.cetHeight?.text.toString().toDoubleOrNull() ?: 0.0
    val specThickness = binding?.cetThickness?.text.toString().toDoubleOrNull() ?: 0.0
    val specWeight = binding?.cetWeight?.text.toString().toDoubleOrNull() ?: 0.0
    val brand = binding?.edtBrand?.text?.toString() ?: ""
    val keySpecification = binding?.specKey?.text?.toString() ?: ""
    val valueSpecification = binding?.specValue?.text?.toString() ?: ""

    val gst = (binding?.edtGst?.text?.toString() ?: "").replace("%", "").trim()
    val otherSpec = (specList.filter {
      it.key.isNullOrEmpty().not() && it.value.isNullOrEmpty().not()
    } as? ArrayList<KeySpecification>) ?: ArrayList()
    when {
//      secondaryImage.isNullOrEmpty() -> {
//        showLongToast("Please select at least one secondary image.")
//        return
//      }
//      serviceCategory.isNullOrEmpty() -> {
//        showLongToast("Product category field can't empty.")
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
        WebEngageController.trackEvent(PRODUCT_INFORMATION_CONFIRM, CLICK, NO_EVENT_VALUE)
//        product?.category = serviceCategory
        product?.brandName = brand
        if (product?.keySpecification == null) product?.keySpecification = KeySpecification()
        product?.keySpecification?.key = keySpecification
        product?.keySpecification?.value = valueSpecification
        product?.tags = tagList
        product?.availableUnits = availableStock
        product?.maxCodOrders = if (product?.codAvailable == true) availableCODStock else 0
        product?.maxPrepaidOnlineAvailable = if (product?.prepaidOnlineAvailable == true) availableOnlineStock else 0
        product?.otherSpecification = otherSpec
        if (gstProductData == null) gstProductData = GstData()
        gstProductData?.gstSlab = gst.toDoubleOrNull() ?: 0.0
        gstProductData?.height = specHeight
        gstProductData?.length = specLength
        gstProductData?.width = specThickness
        gstProductData?.weight = specWeight
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
    binding?.chipsproduct?.removeAllViews()
    tagList.forEach { tag ->
      val mChip: Chip = baseActivity.layoutInflater.inflate(R.layout.item_chip, binding?.chipsproduct, false) as Chip
      mChip.text = tag
      mChip.setOnCloseIconClickListener {
        binding?.chipsproduct?.removeView(mChip)
        tagList.remove(tag)
      }
      binding?.chipsproduct?.addView(mChip)
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
        adapterImage = AppBaseRecyclerViewAdapter(baseActivity, secondaryImage, this@ProductInformationFragment)
        adapter = adapterImage
      }
    } else adapterImage?.notifyDataSetChanged()
  }

  override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
    when (actionType) {
      RecyclerViewActionType.IMAGE_CLEAR_CLICK.ordinal -> {
        val data = item as? FileModel
        if (isEdit == true && data?.pathUrl.isNullOrEmpty().not()) {
          val dataImage = secondaryDataImage?.firstOrNull { it.image?.url == data?.pathUrl } ?: return
          showProgress(resources.getString(R.string.removing_image))
          val request = ProductImageDeleteRequest()
          request.setQueryData(dataImage.id)
          viewModel?.deleteProductImage(request)?.observeOnce(viewLifecycleOwner, Observer {
            if (it.isSuccess()) {
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
    gstSheet.show(this@ProductInformationFragment.parentFragmentManager, ImagePickerBottomSheet::class.java.name)
  }

  fun onNavPressed() {
    dialogLogout()
  }

  private fun dialogLogout() {
    MaterialAlertDialogBuilder(baseActivity, R.style.MaterialAlertDialogTheme)
      .setTitle(resources.getString(R.string.information_not_saved))
      .setMessage(resources.getString(R.string.you_have_unsaved_info))
      .setNegativeButton(getString(R.string.no)) { d, _ -> d.dismiss() }
      .setPositiveButton(getString(R.string.yes)) { d, _ ->
        baseActivity.finish()
        d.dismiss()
      }.show()
  }
}