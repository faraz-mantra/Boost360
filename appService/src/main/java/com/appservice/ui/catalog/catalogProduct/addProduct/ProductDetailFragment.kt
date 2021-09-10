package com.appservice.ui.catalog.catalogProduct.addProduct

import android.app.Activity
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.constant.FragmentType
import com.appservice.constant.IntentConstant
import com.appservice.databinding.FragmentProductDetailsBinding
import com.appservice.extension.afterTextChanged
import com.appservice.model.FileModel
import com.appservice.model.accountDetails.AccountDetailsResponse
import com.appservice.model.accountDetails.BankAccountDetails
import com.appservice.model.deviceId
import com.appservice.model.pickUpAddress.PickUpAddressResponse
import com.appservice.model.pickUpAddress.PickUpData
import com.appservice.model.product.ProductItemsResponseItem
import com.appservice.model.serviceProduct.BuyOnlineLink
import com.appservice.model.serviceProduct.CatalogProduct
import com.appservice.model.serviceProduct.addProductImage.ActionDataI
import com.appservice.model.serviceProduct.addProductImage.ImageI
import com.appservice.model.serviceProduct.addProductImage.ProductImageRequest
import com.appservice.model.serviceProduct.addProductImage.response.DataImage
import com.appservice.model.serviceProduct.addProductImage.response.ProductImageResponse
import com.appservice.model.serviceProduct.delete.DeleteProductRequest
import com.appservice.model.serviceProduct.gstProduct.ActionDataG
import com.appservice.model.serviceProduct.gstProduct.ProductGstDetailRequest
import com.appservice.model.serviceProduct.gstProduct.response.GstData
import com.appservice.model.serviceProduct.gstProduct.response.ProductGstResponse
import com.appservice.model.serviceProduct.gstProduct.update.ProductUpdateRequest
import com.appservice.model.serviceProduct.gstProduct.update.SetGST
import com.appservice.model.serviceProduct.gstProduct.update.UpdateValueU
import com.appservice.model.serviceProduct.update.ProductUpdate
import com.appservice.model.serviceProduct.update.UpdateValue
import com.appservice.ui.bankaccount.startFragmentAccountActivity
import com.appservice.ui.catalog.startFragmentActivity
import com.appservice.ui.catalog.widgets.*
import com.appservice.utils.WebEngageController
import com.appservice.utils.getBitmap
import com.appservice.viewmodel.ProductViewModel
import com.framework.exceptions.NoNetworkException
import com.framework.extensions.gone
import com.framework.extensions.observeOnce
import com.framework.extensions.visible
import com.framework.glide.util.glideLoad
import com.framework.imagepicker.ImagePicker
import com.framework.models.caplimit_feature.CapLimitFeatureResponseItem
import com.framework.models.caplimit_feature.PropertiesItem
import com.framework.models.caplimit_feature.filterFeature
import com.framework.models.caplimit_feature.getCapData
import com.framework.pref.Key_Preferences.GET_FP_DETAILS_TAG
import com.framework.pref.clientId
import com.framework.utils.hideKeyBoard
import com.framework.pref.clientId
import com.framework.pref.clientId1
import com.framework.webengageconstant.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import okio.Buffer
import okio.BufferedSource
import org.json.JSONObject
import java.io.File
import java.nio.charset.Charset
import java.util.*
import kotlin.collections.ArrayList

class ProductDetailFragment : AppBaseFragment<FragmentProductDetailsBinding, ProductViewModel>() {

  private var menuDelete: MenuItem? = null
  private var productImage: File? = null
  private var product: CatalogProduct? = null
  private var isNonPhysicalExperience: Boolean? = null
  private var currencyType: String? = null
  private var fpId: String? = null
  private var fpTag: String? = null
  private var externalSourceId: String? = null
  private var applicationId: String? = null
  private var userProfileId: String? = null
  private var pickUpDataAddress: ArrayList<PickUpData>? = null

  private var isEdit: Boolean? = null
  private var bankAccountDetail: BankAccountDetails? = null

  private var secondaryImage: ArrayList<FileModel> = ArrayList()

  private var secondaryDataImage: ArrayList<DataImage>? = null
  private var gstProductData: GstData? = null

  private var productIdAdd: String? = null
  private var errorType: String? = null

  companion object {
    fun newInstance(): ProductDetailFragment {
      return ProductDetailFragment()
    }
  }

  override fun getLayout(): Int {
    return R.layout.fragment_product_details
  }

  override fun getViewModelClass(): Class<ProductViewModel> {
    return ProductViewModel::class.java
  }

  override fun onCreateView() {
    super.onCreateView()
    WebEngageController.trackEvent(PRODUCT_CATALOGUE_ADD_PAGE, ADDED, NO_EVENT_VALUE)
    getBundleData()
    getPickUpAddress()
    binding?.vwChangeDeliverConfig?.paintFlags = Paint.UNDERLINE_TEXT_FLAG
    binding?.vwPaymentConfig?.paintFlags = Paint.UNDERLINE_TEXT_FLAG
    setOnClickListener(
      binding?.vwChangeDeliverConfig, binding?.vwChangeDeliverLocation,
      binding?.vwPaymentConfig, binding?.vwSavePublish, binding?.imageAddBtn,
      binding?.clearImage, binding?.btnOtherInfo, binding?.bankAccountView
    )
    binding?.toggleProduct?.isOn = product?.isPriceToggleOn()?:false
    binding?.payProductView?.visibility = View.GONE
    binding?.toggleProduct?.setOnToggledListener { _, _ -> initProductToggleView() }
    initProductToggleView()
    listenerEditText()
    capLimitCheck()
  }

  private fun capLimitCheck() {
    val featureProduct = getCapData().filterFeature(CapLimitFeatureResponseItem.FeatureType.PRODUCTCATALOGUE)
    val capLimitProduct = featureProduct?.filterProperty(PropertiesItem.KeyType.LIMIT)
    if (isEdit?.not() == true && capLimitProduct != null && capLimitProduct.getValueN() != null) {
      viewModel?.getAllProducts(getRequestProduct(capLimitProduct.getValueN()!!))?.observeOnce(viewLifecycleOwner, {
        val data = it.arrayResponse as? Array<ProductItemsResponseItem>
        if (data.isNullOrEmpty().not()) {
          baseActivity.hideKeyBoard()
          showAlertCapLimit("Can't add the product catalogue, please activate your premium Add-ons plan.",CapLimitFeatureResponseItem.FeatureType.PRODUCTCATALOGUE.name)
        }
      })
    }
  }

  private fun initProductToggleView() {
    binding?.payProductView?.visibility = if (binding?.toggleProduct?.isOn!!) View.VISIBLE else View.GONE
    binding?.freeProductView?.visibility = if (binding?.toggleProduct?.isOn!!) View.GONE else View.VISIBLE
  }

  private fun listenerEditText() {
    binding?.amountEdt?.afterTextChanged {
      calculate(binding?.amountEdt?.text.toString(), binding?.discountEdt?.text.toString())
    }
    binding?.discountEdt?.afterTextChanged {
      calculate(binding?.amountEdt?.text.toString(), binding?.discountEdt?.text.toString())
    }
  }

  private fun calculate(amount: String, dist: String) {
    val amountD = amount.toFloatOrNull() ?: 0F
    val distD = dist.toFloatOrNull() ?: 0F
    if (distD > amountD) {
      showLongToast(resources.getString(R.string.discount_amount_not_greater_than_price))
      binding?.discountEdt?.setText("")
      return
    }
//    val finalAmount = String.format("%.1f", (amountD - ((amountD * distD) / 100))).toFloatOrNull() ?: 0F
    val finalAmount = String.format("%.1f", (amountD - distD)).toFloatOrNull() ?: 0F
    binding?.finalPriceTxt?.setText("$currencyType $finalAmount")
  }

  private fun getPickUpAddress() {
    showProgress()
    viewModel?.getPickUpAddress(fpId)?.observeOnce(viewLifecycleOwner, Observer {
      if ((it.error is NoNetworkException).not()) {
        val response = it as? PickUpAddressResponse
        pickUpDataAddress =
          if ((it.status == 200 || it.status == 201 || it.status == 202) && response?.data.isNullOrEmpty()
              .not()
          ) {
            response?.data
          } else ArrayList()
        getPaymentGatewayKyc()
      } else {
        showError(resources.getString(R.string.internet_connection_not_available))
        baseActivity.finish()
      }
    })
  }

  private fun getPaymentGatewayKyc() {
    showProgress()
    viewModel?.userAccountDetails(fpId, clientId)?.observeOnce(viewLifecycleOwner, Observer {
      if ((it.error is NoNetworkException).not()) {
        val response = it as? AccountDetailsResponse
        if ((it.status == 200 || it.status == 201 || it.status == 202) && response?.result?.bankAccountDetails != null) {
          bankAccountDetail = response.result?.bankAccountDetails
        }
      }
      if (isEdit == true) getAddPreviousData()
      else {
        setBankAccountData()
        hideProgress()
      }
    })
  }

  private fun setBankAccountData() {
    if (bankAccountDetail != null) {
      product?.paymentType = CatalogProduct.PaymentType.ASSURED_PURCHASE.value
      binding?.txtPaymentType?.text = resources.getString(R.string.boost_payment_gateway)
      binding?.bankAccountView?.visible()
      binding?.externalUrlView?.gone()
      binding?.txtPaymentType?.text = resources.getString(R.string.boost_payment_gateway)
      binding?.bankAccountName?.visible()
      binding?.bankAccountName?.text =
        "${bankAccountDetail?.accountName} - ${bankAccountDetail?.accountNumber}"
      binding?.titleBankAdded?.setCompoundDrawablesWithIntrinsicBounds(
        R.drawable.ic_ok_green,
        0,
        0,
        0
      )
      binding?.titleBankAdded?.text =
        "${resources.getString(R.string.bank_account_added)} (${bankAccountDetail?.getVerifyText()})"
    }
  }

  private fun getAddPreviousData() {
    viewModel?.getProductImage(String.format("{'_pid':'%s'}", product?.productId))
      ?.observeOnce(viewLifecycleOwner, Observer {
        if ((it.error is NoNetworkException).not()) {
          val response = it as? ProductImageResponse
          if (response?.status == 200 && response.data.isNullOrEmpty().not()) {
            secondaryDataImage = response.data
          }
          viewModel?.getProductGstDetail(String.format("{'product_id':'%s'}", product?.productId))
            ?.observeOnce(viewLifecycleOwner, Observer { it1 ->
              if ((it1.error is NoNetworkException).not()) {
                val response2 = it1 as? ProductGstResponse
                if (response2?.status == 200 && response2.data.isNullOrEmpty().not()) {
                  gstProductData = response2.data?.first()
                }
              } else showError(resources.getString(R.string.internet_connection_not_available))
              hideProgress()
              updateUiPreviousDat()
            })
        } else {
          hideProgress()
          showError(resources.getString(R.string.internet_connection_not_available))
        }
      })
  }

  private fun updateUiPreviousDat() {
    binding?.tvProductName?.setText(product?.Name)
    binding?.tvDesc?.setText(product?.Description)
    binding?.edtProductCategory?.setText(product?.category)
    when {
      product?.paymentType == CatalogProduct.PaymentType.ASSURED_PURCHASE.value && bankAccountDetail != null || !bankAccountDetail?.iFSC.isNullOrEmpty() || !bankAccountDetail?.accountNumber.isNullOrEmpty() -> {
        binding?.txtPaymentType?.text = resources.getString(R.string.boost_payment_gateway)
        binding?.bankAccountName?.visible()
        binding?.bankAccountName?.text =
          "${bankAccountDetail?.accountName} - ${bankAccountDetail?.accountNumber}"
        binding?.titleBankAdded?.setCompoundDrawablesWithIntrinsicBounds(
          R.drawable.ic_ok_green,
          0,
          0,
          0
        )
        binding?.titleBankAdded?.text =
          "${resources.getString(R.string.bank_account_added)} (${bankAccountDetail?.getVerifyText()})"
      }
    }
    when (product?.paymentType) {
      CatalogProduct.PaymentType.UNIQUE_PAYMENT_URL.value -> {
        binding?.txtPaymentType?.text = resources.getString(R.string.external_url)
        binding?.edtUrl?.setText(product?.BuyOnlineLink?.url ?: "")
        binding?.edtNameDesc?.setText(product?.BuyOnlineLink?.description ?: "")
        binding?.bankAccountView?.gone()
        binding?.externalUrlView?.visible()
      }
      else -> {
        binding?.txtPaymentType?.text = resources.getString(R.string.boost_payment_gateway)
        binding?.bankAccountName?.gone()
        binding?.titleBankAdded?.setCompoundDrawablesWithIntrinsicBounds(
          R.drawable.ic_info_circular_orange,
          0,
          0,
          0
        )
        binding?.titleBankAdded?.text = resources.getString(R.string.bank_account_not_added)
      }
    }
    when {
      product?.Price ?: 0.0 <= 0.0 -> {
        binding?.toggleProduct?.isOn = false
        binding?.payProductView?.gone()
        binding?.freeProductView?.visible()
      }
    }
    binding?.amountEdt?.setText("${product?.Price ?: 0}")
    binding?.discountEdt?.setText("${product?.DiscountAmount ?: 0.0}")
    when {
      product?.ImageUri.isNullOrEmpty().not() -> {
        binding?.imageAddBtn?.gone()
        binding?.clearImage?.visible()
        binding?.productImageView?.visible()
        binding?.productImageView?.let {
          activity?.glideLoad(
            it,
            product?.ImageUri!!,
            R.drawable.placeholder_image
          )
        }
      }
    }
  }

  private fun showError(string: String) {
    hideProgress()
    showLongToast(string)
  }

  private fun getBundleData() {
    product = arguments?.getSerializable(IntentConstant.PRODUCT_DATA.name) as? CatalogProduct
    isEdit = (product != null && product?.productId.isNullOrEmpty().not())
    isNonPhysicalExperience = arguments?.getBoolean(IntentConstant.NON_PHYSICAL_EXP_CODE.name)
    currencyType = arguments?.getString(IntentConstant.CURRENCY_TYPE.name)?:"₹"
    fpId = arguments?.getString(IntentConstant.FP_ID.name)
    fpTag = arguments?.getString(IntentConstant.FP_TAG.name)
    externalSourceId = arguments?.getString(IntentConstant.EXTERNAL_SOURCE_ID.name)
    applicationId = arguments?.getString(IntentConstant.APPLICATION_ID.name)
    userProfileId = arguments?.getString(IntentConstant.USER_PROFILE_ID.name)
    if (isEdit == true) menuDelete?.isVisible = true
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.bankAccountView -> {
        if (binding?.bankAccountView?.visibility == View.VISIBLE) goAddBankView()
      }
      binding?.imageAddBtn -> openImagePicker()
      binding?.clearImage -> clearImage()
      binding?.vwChangeDeliverConfig -> showServiceDeliveryConfigBottomSheet()
//      binding?.vwChangeDeliverLocation -> showServiceDeliveryLocationBottomSheet()
      binding?.vwPaymentConfig -> showPaymentConfigBottomSheet()
      binding?.btnOtherInfo -> {
        WebEngageController.trackEvent(PRODUCT_OTHER_INFORMATION, CLICK, NO_EVENT_VALUE)
        val bundle = Bundle()
        bundle.putSerializable(IntentConstant.PRODUCT_DATA.name, product)
        bundle.putSerializable(IntentConstant.NEW_FILE_PRODUCT_IMAGE.name, secondaryImage)
        bundle.putSerializable(IntentConstant.PRODUCT_IMAGE.name, secondaryDataImage)
        bundle.putSerializable(IntentConstant.PRODUCT_GST_DETAIL.name, gstProductData)
        startFragmentActivity(FragmentType.PRODUCT_INFORMATION, bundle, isResult = true)
      }
      binding?.vwSavePublish -> if (isValid()) createUpdateApi()
    }
  }

  private fun createUpdateApi() {
    showProgress()
    if (isEdit == false) {
      if (productIdAdd.isNullOrEmpty().not() && errorType == "addGstService") {
        addGstService(productIdAdd)
      } else if (productIdAdd.isNullOrEmpty().not() && errorType == "uploadImageSingle") {
        uploadImageSingle(productIdAdd)
      } else {
        viewModel?.createProduct(product)?.observeOnce(viewLifecycleOwner, {
          if ((it.error is NoNetworkException).not()) {
            val productId = it.stringResponse
            if (it.isSuccess() && productId.isNullOrEmpty().not()) {
              WebEngageController.trackEvent(PRODUCT_CATALOGUE_CREATED, ADDED, NO_EVENT_VALUE)
              productIdAdd = productId
              addGstService(productId)
            } else showError(getString(R.string.product_adding_error_try_again))
          } else showError(resources.getString(R.string.internet_connection_not_available))
        })
      }
    } else {
      val updates = ArrayList<UpdateValue>()
      val json = JSONObject(Gson().toJson(product))
      val keys = json.keys()
      while (keys.hasNext()) {
        val key = keys.next()
        updates.add(UpdateValue(key, json[key].toString()))
      }
      val request = ProductUpdate(
        clientId,
        productId = product?.productId,
        productType = product?.productType,
        updates = updates
      )
      viewModel?.updateProduct(request)?.observeOnce(viewLifecycleOwner, {
        if ((it.error is NoNetworkException).not()) {
          if ((it.isSuccess())) {
            WebEngageController.trackEvent(PRODUCT_CATALOGUE_UPDATED, ADDED, NO_EVENT_VALUE)
            updateGstService(product?.productId)
          } else showError(getString(R.string.product_updating_error_try_again))
        } else showError(resources.getString(R.string.internet_connection_not_available))
      })
    }
  }

  private fun updateGstService(productId: String?) {
    val gstData = gstProductData ?: GstData()
    val request =
      ProductUpdateRequest(false, query = String.format("{'product_id':'%s'}", productId))
    val setGST = SetGST(
      gstData.gstSlab?.toString() ?: "0.0", gstData.height?.toString()
        ?: "0.0",
      gstData.length?.toString() ?: "0.0", gstData.weight?.toString()
        ?: "0.0", gstData.width?.toString() ?: "0.0"
    )
    request.updateValueSet(UpdateValueU(setGST))
    viewModel?.updateProductGstDetail(request)?.observeOnce(viewLifecycleOwner, Observer {
      if ((it.error is NoNetworkException).not()) {
        if ((it.status == 200 || it.status == 201 || it.status == 202)) {
          hideProgress()
          uploadImageSingle(productId)
        } else showError(getString(R.string.product_updating_error_try_again))
      } else showError(resources.getString(R.string.internet_connection_not_available))
    })
  }

  private fun addGstService(productId: String?) {
    val gstData = gstProductData ?: GstData()
    val request = ProductGstDetailRequest(
      ActionDataG(
        gstData.gstSlab ?: 0.0, gstData.height
          ?: 0.0, gstData.length ?: 0.0,
        merchantId = fpId, productId = productId, gstData.weight ?: 0.0, gstData.height
          ?: 0.0
      ), fpId
    )
    viewModel?.addProductGstDetail(request)?.observeOnce(viewLifecycleOwner, Observer {
      if ((it.error is NoNetworkException).not()) {
        if ((it.status == 200 || it.status == 201 || it.status == 202)) {
          hideProgress()
          uploadImageSingle(productId)
        } else {
          if (isEdit == false) errorType = "addGstService"
          showError(getString(R.string.product_adding_error_try_again))
        }
      } else {
        if (isEdit == false) errorType = "addGstService"
        showError(resources.getString(R.string.internet_connection_not_available))
      }
    })
  }

  private fun uploadImageSingle(productId: String?) {
    showProgress(getString(R.string.uploading_product_image))
    if (isEdit == true && productImage == null) {
      uploadSecondaryImage(productId)
      return
    }
    viewModel?.addUpdateProductImage(
      clientId, "sequential", deviceId,
      1, 1, productId, getRequestServiceImage(productImage)
    )?.observeOnce(viewLifecycleOwner, Observer {
      if ((it.error is NoNetworkException).not()) {
        if (it.status == 200 || it.status == 201 || it.status == 202) {
          uploadSecondaryImage(productId)
        } else {
          if (isEdit == false) errorType = "uploadImageSingle"
          showError(getString(R.string.product_image_uploading_error))
        }
      } else {
        if (isEdit == false) errorType = "uploadImageSingle"
        showError(resources.getString(R.string.internet_connection_not_available))
      }
    })
  }

  private fun getRequestServiceImage(serviceImage: File?): RequestBody {
    val responseBody = serviceImage?.readBytes()
      ?.let { it.toRequestBody("image/png".toMediaTypeOrNull(), 0, it.size) }
    val fileName = takeIf { serviceImage?.name.isNullOrEmpty().not() }?.let { serviceImage?.name }
      ?: "service_${Date().time}.png"
    return responseBody!!
  }

  private fun uploadSecondaryImage(productId: String?) {
    val images = secondaryImage.filter { it.path.isNullOrEmpty().not() }
    if (images.isNullOrEmpty().not()) {
      var checkPosition = 0
      val secondaryImageList = ArrayList<String>()
      images.forEach { fileData ->
        val secondaryFile = fileData.getFile()
        val fileNew =
          takeIf { secondaryFile?.name.isNullOrEmpty().not() }?.let { secondaryFile?.name }
            ?: "service_${Date()}.jpg"
        val requestProfile = secondaryFile?.let { it.asRequestBody("image/*".toMediaTypeOrNull()) }
        val body = requestProfile?.let { MultipartBody.Part.createFormData("file", fileNew, it) }
        viewModel?.uploadImageProfile(fileNew, body)?.observeOnce(viewLifecycleOwner, Observer {
          checkPosition += 1
          if ((it.error is NoNetworkException).not()) {
            if (it.status == 200 || it.status == 201 || it.status == 202) {
              val response = getResponse(it.responseBody) ?: ""
              if (response.isNotEmpty()) secondaryImageList.add(response)
            } else showError(getString(R.string.secondary_product_image_uploading_error_please))
          } else showError(resources.getString(R.string.internet_connection_not_available))
          if (checkPosition == images.size) {
            addImageToProduct(productId, secondaryImageList)
          }
        })
      }
    } else addImageToProduct(productId, ArrayList())
  }

  private fun addImageToProduct(productId: String?, secondaryImageList: ArrayList<String>) {
    if (secondaryImageList.isNullOrEmpty().not()) {
      var checkPosition = 0
      secondaryImageList.forEach { image ->
        val request =
          ProductImageRequest(ActionDataI(ImageI(url = image, description = ""), productId), fpId)
        viewModel?.addProductImage(request)?.observeOnce(viewLifecycleOwner, Observer {
          checkPosition += 1
          if ((it.error is NoNetworkException).not()) {
            if (it.status == 200 || it.status == 201 || it.status == 202) {
              Log.d(ProductDetailFragment::class.java.name, "$it")
            } else showLongToast(getString(R.string.add_secondary_image_data_error_please_try_again))
          } else showError(resources.getString(R.string.internet_connection_not_available))
          if (checkPosition == secondaryImageList.size) {
            showLongToast(
              if (isEdit == true) getString(R.string.product_updated_successfully) else getString(
                R.string.product_saved_successfully
              )
            )
            goBack()
          }
        })
      }
    } else {
      showLongToast(
        if (isEdit == true) getString(R.string.product_updated_successfully) else getString(
          R.string.product_saved_successfully
        )
      )
      goBack()
    }
  }

  private fun goBack() {
    hideProgress()
    val data = Intent()
    data.putExtra(IntentConstant.IS_UPDATED.name, true)
    baseActivity?.setResult(Activity.RESULT_OK, data)
    baseActivity?.finish()
  }

  private fun getResponse(responseBody: ResponseBody?): String? {
    val source: BufferedSource? = responseBody?.source()
    source?.request(Long.MAX_VALUE)
    val buffer: Buffer? = source?.buffer
    return buffer?.clone()?.readString(Charset.forName("UTF-8"))
  }


  private fun isValid(): Boolean {
    if (product==null) product = CatalogProduct()
    val productName = binding?.tvProductName?.text.toString()
    val productCategory = binding?.edtProductCategory?.text.toString()
    val productDesc = binding?.tvDesc?.text.toString()
    val amount = binding?.amountEdt?.text.toString().toDoubleOrNull() ?: 0.0
    val discount = binding?.discountEdt?.text.toString().toDoubleOrNull() ?: 0.0
    val toggle = binding?.toggleProduct?.isOn ?: false
    val externalUrlName = binding?.edtNameDesc?.text?.toString() ?: ""
    val externalUrl = binding?.edtUrl?.text?.toString() ?: ""

    if (productImage == null && product?.ImageUri.isNullOrEmpty()) {
      showLongToast(resources.getString(R.string.error_add_product_image))
      return false
    } else if (productName.isEmpty()) {
      showLongToast(resources.getString(R.string.enter_product_name))
      return false
    } else if (productCategory.isEmpty()) {
      showLongToast(resources.getString(R.string.enter_category_catalog_product))
      return false
    } else if (productDesc.isEmpty()) {
      showLongToast(resources.getString(R.string.enter_product_desc))
      return false
    } else if (toggle && amount <= 0.0) {
      showLongToast(resources.getString(R.string.enter_valid_price))
      return false
    } else if (toggle && (discount > amount)) {
      showLongToast(resources.getString(R.string.discount_amount_not_greater_than_price))
      return false
    } else if (toggle && (product?.paymentType.isNullOrEmpty() || (product?.paymentType == CatalogProduct.PaymentType.ASSURED_PURCHASE.value && bankAccountDetail == null))) {
      showLongToast(resources.getString(R.string.please_add_bank_detail))
      return false
    } else if (toggle && (product?.paymentType == CatalogProduct.PaymentType.UNIQUE_PAYMENT_URL.value && (externalUrlName.isNullOrEmpty() || externalUrl.isNullOrEmpty()))) {
      showLongToast(resources.getString(R.string.please_enter_valid_url_name))
      return false
    }
    product?.ClientId = clientId
    product?.FPTag = fpTag
    product?.CurrencyCode = currencyType
    product?.Name = productName
    product?.category = productCategory
    product?.Description = productDesc
    product?.Price = if (toggle) amount else 0.0
    product?.DiscountAmount = if (toggle) discount else 0.0
    if (toggle && (product?.paymentType == CatalogProduct.PaymentType.UNIQUE_PAYMENT_URL.value)) {
      product?.BuyOnlineLink = BuyOnlineLink(url = externalUrl, description = externalUrlName)
    } else product?.BuyOnlineLink = BuyOnlineLink()

    if (isEdit == false) {
      product?.category = product?.category ?: ""
      product?.brandName = product?.brandName ?: ""
      product?.tags = product?.tags ?: ArrayList()
      product?.otherSpecification = product?.otherSpecification ?: ArrayList()
      product?.IsAvailable = true
      product?.prepaidOnlineAvailable = true
      product?.variants = false
      product?.isProductSelected = false
      product?.codAvailable = true
    }
    return true
  }

  private fun clearImage() {
    binding?.imageAddBtn?.visible()
    binding?.clearImage?.gone()
    binding?.productImageView?.gone()
    product?.ImageUri = null
    productImage = null
  }

  private fun openImagePicker() {
    val filterSheet = ImagePickerBottomSheet()
    filterSheet.isHidePdf(true)
    filterSheet.onClicked = { openImagePicker(it) }
    filterSheet.show(
      this@ProductDetailFragment.parentFragmentManager,
      ImagePickerBottomSheet::class.java.name
    )
  }


  private fun openImagePicker(it: ClickType) {
    val type = when (it) {
      ClickType.CAMERA -> ImagePicker.Mode.CAMERA
      else -> ImagePicker.Mode.GALLERY
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
        productImage = File(mPaths[0])
        binding?.imageAddBtn?.gone()
        binding?.clearImage?.visible()
        binding?.productImageView?.visible()
        productImage?.getBitmap()?.let { binding?.productImageView?.setImageBitmap(it) }
      }
    } else if (resultCode == AppCompatActivity.RESULT_OK && requestCode == 101) {
      product = data?.getSerializableExtra(IntentConstant.PRODUCT_DATA.name) as? CatalogProduct
      secondaryImage =
        (data?.getSerializableExtra(IntentConstant.NEW_FILE_PRODUCT_IMAGE.name) as? ArrayList<FileModel>)
          ?: ArrayList()
      gstProductData =
        data?.getSerializableExtra(IntentConstant.PRODUCT_GST_DETAIL.name) as? GstData
    } else if (resultCode == AppCompatActivity.RESULT_OK && requestCode == 202) {
      bankAccountDetail =
        data?.getSerializableExtra(IntentConstant.USER_BANK_DETAIL.name) as? BankAccountDetails
      if (bankAccountDetail != null) {
        product?.paymentType = CatalogProduct.PaymentType.ASSURED_PURCHASE.value
        binding?.bankAccountView?.visible()
        binding?.externalUrlView?.gone()
        binding?.bankAccountName?.visible()
        binding?.bankAccountName?.text =
          "${bankAccountDetail?.accountName} - ${bankAccountDetail?.accountNumber}"
        binding?.titleBankAdded?.setCompoundDrawablesWithIntrinsicBounds(
          R.drawable.ic_ok_green,
          0,
          0,
          0
        )
        binding?.titleBankAdded?.text = resources.getString(R.string.bank_account_added)
      }
    }
  }

  private fun showServiceDeliveryConfigBottomSheet() {
    val dialog = ServiceDeliveryConfigBottomSheet()
    dialog.onClicked = { product?.prepaidOnlineAvailable = true }
    if (product?.prepaidOnlineAvailable != null) dialog.isUpdate(
      product?.prepaidOnlineAvailable
        ?: true
    )
    dialog.show(parentFragmentManager, ServiceDeliveryConfigBottomSheet::class.java.name)
  }

  private fun showServiceDeliveryLocationBottomSheet() {
    val dialog = ServiceDeliveryBottomSheet()
    dialog.setList(pickUpDataAddress, product?.pickupAddressReferenceId)
    dialog.onClicked = {
      if (it != null && it.id.isNullOrEmpty().not()) {
        product?.pickupAddressReferenceId = it.id
      }
    }
    dialog.show(parentFragmentManager, ServiceDeliveryBottomSheet::class.java.name)
  }

  private fun showPaymentConfigBottomSheet() {
    val dialog = PaymentConfigBottomSheet()
    dialog.onClicked = {
      product?.paymentType = it
      binding?.bankAccountView?.visible()
      binding?.externalUrlView?.gone()
      when (it) {
        CatalogProduct.PaymentType.ASSURED_PURCHASE.value -> {
          binding?.txtPaymentType?.text = resources.getString(R.string.boost_payment_gateway)
          binding?.bankAccountName?.visible()
          binding?.bankAccountName?.text =
            "${bankAccountDetail?.accountName} - ${bankAccountDetail?.accountNumber}"
          when {
            bankAccountDetail?.accountNumber.isNullOrBlank() || bankAccountDetail?.accountName.isNullOrBlank() -> {
              binding?.titleBankAdded?.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_info_circular_orange,
                0,
                0,
                0
              )
              binding?.titleBankAdded?.text = resources.getString(R.string.bank_account_not_added)
            }
            else -> {
              binding?.titleBankAdded?.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_ok_green,
                0,
                0,
                0
              )
              binding?.titleBankAdded?.text =
                "${resources.getString(R.string.bank_account_added)} (${bankAccountDetail?.getVerifyText()})"
            }
          }


        }
        CatalogProduct.PaymentType.UNIQUE_PAYMENT_URL.value -> {
          binding?.txtPaymentType?.text = resources.getString(R.string.external_url)
          binding?.bankAccountView?.gone()
          binding?.externalUrlView?.visible()
        }
        else -> {
          binding?.txtPaymentType?.text = resources.getString(R.string.boost_payment_gateway)
          binding?.bankAccountName?.gone()
          binding?.titleBankAdded?.setCompoundDrawablesWithIntrinsicBounds(
            R.drawable.ic_info_circular_orange,
            0,
            0,
            0
          )
          binding?.titleBankAdded?.text = resources.getString(R.string.bank_account_not_added)
        }
      }
    }
    dialog.onListenerChange = { goAddBankView() }
    if (((product?.paymentType == CatalogProduct.PaymentType.ASSURED_PURCHASE.value && bankAccountDetail != null) ||
          (product?.paymentType == CatalogProduct.PaymentType.UNIQUE_PAYMENT_URL.value)).not()
    ) {
      product?.paymentType = ""
    }
    dialog.setDataPaymentGateway(bankAccountDetail, product?.paymentType)
    dialog.show(parentFragmentManager, PaymentConfigBottomSheet::class.java.name)
  }

  private fun goAddBankView() {
    WebEngageController.trackEvent(ADD_UPDATE_BANK_ACCOUNT, CLICK, NO_EVENT_VALUE)
    val bundle = Bundle()
    bundle.putString(IntentConstant.CLIENT_ID.name, clientId)
    bundle.putString(IntentConstant.USER_PROFILE_ID.name, userProfileId)
    bundle.putString(IntentConstant.FP_ID.name, fpId)
    bundle.putBoolean(IntentConstant.IS_SERVICE_CREATION.name, true)
    val fragment = when {
      bankAccountDetail != null && bankAccountDetail?.accountNumber.isNullOrEmpty()
        .not() && bankAccountDetail?.iFSC.isNullOrEmpty().not() -> FragmentType.BANK_ACCOUNT_DETAILS
      else -> FragmentType.ADD_BANK_ACCOUNT_START
    }
    startFragmentAccountActivity(fragment, bundle, isResult = true, requestCode = 202)
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    super.onCreateOptionsMenu(menu, inflater)
//    inflater.inflate(R.menu.menu_help, menu)
////    menuDelete = menu.findItem(R.id.id_delete)
////    menuDelete?.isVisible = isEdit ?: false
    inflater.inflate(R.menu.ic_menu_delete_new, menu)
    menuDelete = menu.findItem(R.id.id_delete)
    menuDelete?.isVisible = isEdit ?: false
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return when (item.itemId) {
      R.id.id_delete -> {
        MaterialAlertDialogBuilder(baseActivity, R.style.MaterialAlertDialogTheme).setTitle(
          resources.getString(R.string.are_you_sure)
        )
          .setMessage(resources.getString(R.string.delete_record_not_undone))
          .setNegativeButton(resources.getString(R.string.cancel)) { d, _ -> d.dismiss() }
          .setPositiveButton(resources.getString(R.string.delete_)) { d, _ ->
            d.dismiss()
            showProgress()
            WebEngageController.trackEvent(DELETE_PRODUCT_CATALOGUE, DELETE, NO_EVENT_VALUE)
            val request =
              DeleteProductRequest(clientId, "SINGLE", product?.productId, product?.productType)
            viewModel?.deleteService(request)?.observeOnce(viewLifecycleOwner, Observer {
              hideProgress()
              if ((it.error is NoNetworkException).not()) {
                if ((it.status == 200 || it.status == 201 || it.status == 202)) {
                  showLongToast(resources.getString(R.string.product_removed_success))
                  goBack()
                } else showError(resources.getString(R.string.remove_product_failed))
              } else showError(resources.getString(R.string.internet_connection_not_available))
            })
          }.show()
        true
      }
      else -> super.onOptionsItemSelected(item)
    }
  }

  private fun getRequestProduct(value: Int): HashMap<String, String> {
    val values = HashMap<String, String>()
    values["clientId"] = clientId
    values["skipBy"] = "$value"
    values["fpTag"] = sessionLocal.getFPDetails(GET_FP_DETAILS_TAG) ?: ""
    return values
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