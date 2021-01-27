package com.appservice.ui.catalog.catalogProduct.information

import android.content.Intent
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
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
import com.appservice.model.serviceProduct.Product
import com.appservice.model.serviceProduct.addProductImage.deleteRequest.ProductImageDeleteRequest
import com.appservice.model.serviceProduct.addProductImage.response.DataImage
import com.appservice.model.serviceProduct.gstProduct.response.DataG
import com.appservice.recyclerView.AppBaseRecyclerViewAdapter
import com.appservice.recyclerView.BaseRecyclerViewItem
import com.appservice.recyclerView.RecyclerItemClickListener
import com.appservice.ui.catalog.catalogService.information.CustomDropDownAdapter
import com.appservice.ui.catalog.catalogService.information.SpinnerImageModel
import com.appservice.ui.catalog.widgets.ClickType
import com.appservice.ui.catalog.widgets.GstDetailsBottomSheet
import com.appservice.ui.catalog.widgets.ImagePickerBottomSheet
import com.appservice.utils.WebEngageController
import com.appservice.viewmodel.ServiceViewModel
import com.framework.extensions.observeOnce
import com.framework.imagepicker.ImagePicker
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class ProductInformationFragment : AppBaseFragment<FragmentProductInformationBinding, ServiceViewModel>(), RecyclerItemClickListener {

  private var length: Double? = 0.0
  private var height: Double? = 0.0
  private var thickness: Double? = 0.0
  private var weight: Double? = 0.0
  private var product: Product? = null
  private var isEdit: Boolean? = null
  private var tagList = ArrayList<String>()
  private var specList: ArrayList<KeySpecification> = arrayListOf(KeySpecification())
  private var secondaryImage: ArrayList<FileModel> = ArrayList()
  private var adapterSpec: AppBaseRecyclerViewAdapter<KeySpecification>? = null
  private var adapterImage: AppBaseRecyclerViewAdapter<FileModel>? = null
  private var availableStock = 0;
//  private var maxOrder = 0;

  private var secondaryDataImage: ArrayList<DataImage>? = null
  private var gstProductData: DataG? = null

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
    WebEngageController.trackEvent("Service other information catalogue load", "SERVICE CATALOGUE ADD/UPDATE", "")

    setOnClickListener(
            binding?.btnAddTag, binding?.btnAddSpecification, binding?.btnConfirm, binding?.btnClickPhoto, binding?.edtGst, binding?.civDecreseStock,
            binding?.civIncreaseStock)
    product = arguments?.getSerializable(IntentConstant.PRODUCT_DATA.name) as? Product
    isEdit = (product != null && product?.productId.isNullOrEmpty().not())
    gstProductData = arguments?.getSerializable(IntentConstant.PRODUCT_GST_DETAIL.name) as? DataG
    secondaryImage = (arguments?.getSerializable(IntentConstant.NEW_FILE_PRODUCT_IMAGE.name) as? ArrayList<FileModel>) ?: ArrayList()
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
    length = gstProductData?.length.toString().toDoubleOrNull() ?: 0.0
    height = gstProductData?.height.toString().toDoubleOrNull() ?: 0.0
    thickness = gstProductData?.width.toString().toDoubleOrNull() ?: 0.0
    weight = gstProductData?.weight.toString().toDoubleOrNull() ?: 0.0
//    binding?.edtProductCategory?.setText(product?.category ?: "")
//    maxOrder = product?.maxCodOrders!!
    binding?.specKey?.setText(product?.keySpecification?.key)
    binding?.specValue?.setText(product?.keySpecification?.value)
    availableStock = product?.availableUnits!!
    binding?.edtBrand?.setText(product?.brandName ?: "")
    binding?.ctvCurrentStock?.text = product?.availableUnits.toString()
//    binding?.ctvQuantityOrderStatus?.text = product?.maxCodOrders.toString()
    if (gstProductData != null) {
      binding?.edtGst?.setText("${(gstProductData?.gstSlab ?: 0.0).toInt()} %")
    }
    binding?.cetLength?.setText(length.toString())
    binding?.cetThickness?.setText(thickness.toString())
    binding?.cetHeight?.setText(height.toString())
    binding?.cetWeight?.setText(weight.toString())
    setAdapter()
    val stockAdapter = mutableListOf(SpinnerImageModel("Limited Stock" to true, R.drawable.ic_dot_green), SpinnerImageModel("Unlimited Stock" to false, R.drawable.ic_infinite))
    binding?.spinnerStock?.adapter = CustomDropDownAdapter(baseActivity, stockAdapter)
    binding?.spinnerStock?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
      override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        return when (stockAdapter[p2] == stockAdapter[1]) {
          true -> {
            binding?.llStockChange?.visibility = View.INVISIBLE
          }
          else -> {
            binding?.llStockChange?.visibility = View.VISIBLE

          }
        }
      }

      override fun onNothingSelected(p0: AdapterView<*>?) {
        //implementation not required
      }

    }
    setSpinners()
    //    binding?.spinnerCod?.adapter = CustomDropDownAdapter(baseActivity, stockAdapter)
//    when (product?.codAvailable) {
//      true -> binding?.spinnerCod?.setSelection(0)
//      else -> binding?.spinnerCod?.setSelection(1)
//    }
//    when (product?.prepaidOnlineAvailable) {
//      true -> binding?.spinnerOnlinePayment?.setSelection(0)
//      else -> binding?.spinnerOnlinePayment?.setSelection(1)
//    }

    binding?.specKey?.setText(product?.keySpecification?.key)
    binding?.specValue?.setText(product?.keySpecification?.value)
  }

  private fun setSpinners() {
    val lengthArray = mutableListOf("INCH", "METERS")
    val weightArray = mutableListOf("KGS", "MGS")
    binding?.spinnerHeight?.adapter = ArrayAdapter(baseActivity, R.layout.support_simple_spinner_dropdown_item, lengthArray)
    binding?.spinnerThickness?.adapter = ArrayAdapter(baseActivity, R.layout.support_simple_spinner_dropdown_item, lengthArray)
    binding?.spinnerWeight?.adapter = ArrayAdapter(baseActivity, R.layout.support_simple_spinner_dropdown_item, weightArray)
    binding?.spinnerLength?.adapter = ArrayAdapter(baseActivity, R.layout.support_simple_spinner_dropdown_item, lengthArray)
    binding?.spinnerHeight?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
      override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        if (p0?.selectedItem.toString() == lengthArray[0]) {
          height = binding?.cetHeight?.text.toString().toDoubleOrNull() ?: 0.0
          binding?.cetHeight?.setText(convertInchToMeters(height).toString())
        } else {
          binding?.cetHeight?.setText(convertMetersToInch(height).toString())
        }

      }

      override fun onNothingSelected(p0: AdapterView<*>?) {
      }

    }
    binding?.spinnerLength?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
      override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        if (p0?.selectedItem.toString() == lengthArray[0]) {
          length = binding?.cetLength?.text.toString().toDoubleOrNull() ?: 0.0
          binding?.cetLength?.setText(convertInchToMeters(length).toString())
        } else {
          binding?.cetLength?.setText(convertMetersToInch(length).toString())
        }
      }

      override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
      }

    }
    binding?.spinnerWeight?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
      override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        if (p0?.selectedItem.toString() == weightArray[0]) {
          weight = binding?.cetWeight?.text.toString().toDoubleOrNull() ?: 0.0
          binding?.cetWeight?.setText(convertMgToKilo(weight).toString())
        } else {
          binding?.cetWeight?.setText(convertKiloToMg(weight).toString())
        }
      }

      override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
      }

    }
    binding?.spinnerThickness?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
      override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        if (p0?.selectedItem.toString() == lengthArray[0]) {
          thickness = binding?.cetThickness?.text.toString().toDoubleOrNull() ?: 0.0
          binding?.cetThickness?.setText(convertInchToMeters(thickness).toString())
        } else {
          binding?.cetThickness?.setText(convertMetersToInch(thickness).toString())
        }
      }

      override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
      }

    }
  }

  private fun convertMgToKilo(mg: Double?): Double? {
    return mg?.div(1000)
  }

  private fun convertKiloToMg(kg: Double?): Double? {
    return kg?.times(1000)
  }

  private fun convertMetersToInch(length: Double?): Double? {
    return length?.times(39.3701)
  }

  private fun convertInchToMeters(length: Double?): Double? {
    return length?.div(39.3701)
  }

  private fun specificationAdapter() {
    binding?.rvSpecification?.apply {
      adapterSpec = AppBaseRecyclerViewAdapter(baseActivity, specList, this@ProductInformationFragment)
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
      binding?.civDecreseStock -> when{
        availableStock>0 ->{
          availableStock--
          binding?.ctvCurrentStock?.text = availableStock.toString()

        }
      }
      binding?.civIncreaseStock -> {
        availableStock++
        binding?.ctvCurrentStock?.text = availableStock.toString()

      }
    }
  }

  private fun openImagePicker() {
    val filterSheet = ImagePickerBottomSheet()
    filterSheet.isHidePdf(true)
    filterSheet.onClicked = { openImagePicker(it) }
    filterSheet.show(this@ProductInformationFragment.parentFragmentManager, ImagePickerBottomSheet::class.java.name)
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
    val brand = binding?.edtBrand?.text?.toString() ?: ""
    val keySpecification = binding?.specKey?.text?.toString() ?: ""
    val valueSpecification = binding?.specValue?.text?.toString() ?: ""

    val gst = (binding?.edtGst?.text?.toString() ?: "").replace("%", "").trim()
    val otherSpec = (specList.filter { it.key.isNullOrEmpty().not() && it.value.isNullOrEmpty().not() } as? ArrayList<KeySpecification>)
            ?: ArrayList()
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
        WebEngageController.trackEvent("Other information confirm", "SERVICE CATALOGUE ADD/UPDATE", "")
//        product?.category = serviceCategory
        product?.brandName = brand
//        product?.tags = tagList
//        when (spinnerCod.state.first) {
//          "YES" -> product?.codAvailable = true
//          "NO" -> product?.codAvailable = false
//        }
//        when (spinnerOnlinePayment.state.first) {
//          "YES" -> product?.prepaidOnlineAvailable = true
//          "NO" -> product?.prepaidOnlineAvailable = false
//        }
        product?.keySpecification?.key = keySpecification
        product?.keySpecification?.value = valueSpecification
        product?.tags = tagList
//        product?.maxCodOrders = maxOrder
        product?.availableUnits = availableStock
        product?.otherSpecification = otherSpec
        if (gstProductData == null) gstProductData = DataG()
        gstProductData?.gstSlab = gst.toDoubleOrNull() ?: 0.0
        gstProductData?.height = height ?: 0.0
        gstProductData?.length = length ?: 0.0
        gstProductData?.width = thickness ?: 0.0
        gstProductData?.weight = weight ?: 0.0
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
      if (mPaths.size + secondaryImage.size > 8) showLongToast("Only 8 files are allowed. Discarding the rest.")
      var index: Int = secondaryImage.size
      while (index < 8 && mPaths.isNotEmpty()) {
        secondaryImage.add(FileModel(path = mPaths[0]))
        mPaths.removeAt(0)
        index++
      }
      setAdapter()
    } else showLongToast("Only 8 files are allowed.")
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
          showProgress("Removing image...")
          val request = ProductImageDeleteRequest()
          request.setQueryData(dataImage.id)
          viewModel?.deleteProductImage(auth_3, request)?.observeOnce(viewLifecycleOwner, Observer {
            if (it.status == 200 || it.status == 201 || it.status == 202) {
              secondaryDataImage?.remove(dataImage)
              secondaryImage.remove(data)
              setAdapter()
            } else showLongToast("Removing image failed, please try again.")
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

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    super.onCreateOptionsMenu(menu, inflater)
//    inflater.inflate(R.menu.menu_product_info, menu)
  }

  private fun dialogLogout() {
    MaterialAlertDialogBuilder(baseActivity, R.style.MaterialAlertDialogTheme)
            .setTitle("Information not saved!").setMessage("You have unsaved information. Do you still want to close?")
            .setNegativeButton("No") { d, _ -> d.dismiss() }.setPositiveButton("Yes") { d, _ ->
              baseActivity.finish()
              d.dismiss()
            }.show()
  }
}