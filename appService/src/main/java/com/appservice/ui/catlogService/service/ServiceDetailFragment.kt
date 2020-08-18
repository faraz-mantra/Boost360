package com.appservice.ui.catlogService.service

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
import com.appservice.databinding.FragmentServiceDetailBinding
import com.appservice.extension.afterTextChanged
import com.appservice.model.FileModel
import com.appservice.model.accountDetails.AccountDetailsResponse
import com.appservice.model.accountDetails.BankAccountDetails
import com.appservice.model.auth_3
import com.appservice.model.deviceId
import com.appservice.model.pickUpAddress.PickUpAddressResponse
import com.appservice.model.pickUpAddress.PickUpData
import com.appservice.model.serviceProduct.BuyOnlineLink
import com.appservice.model.serviceProduct.Product
import com.appservice.model.serviceProduct.addProductImage.ActionDataI
import com.appservice.model.serviceProduct.addProductImage.ImageI
import com.appservice.model.serviceProduct.addProductImage.ProductImageRequest
import com.appservice.model.serviceProduct.addProductImage.response.DataImage
import com.appservice.model.serviceProduct.addProductImage.response.ProductImageResponse
import com.appservice.model.serviceProduct.delete.DeleteProductRequest
import com.appservice.model.serviceProduct.gstProduct.ActionDataG
import com.appservice.model.serviceProduct.gstProduct.ProductGstDetailRequest
import com.appservice.model.serviceProduct.gstProduct.response.DataG
import com.appservice.model.serviceProduct.gstProduct.response.ProductGstResponse
import com.appservice.model.serviceProduct.gstProduct.update.ProductUpdateRequest
import com.appservice.model.serviceProduct.gstProduct.update.SetGST
import com.appservice.model.serviceProduct.gstProduct.update.UpdateValueU
import com.appservice.model.serviceProduct.update.ProductUpdate
import com.appservice.model.serviceProduct.update.UpdateValue
import com.appservice.ui.bankaccount.startFragmentAccountActivity
import com.appservice.ui.catlogService.startFragmentActivity
import com.appservice.ui.catlogService.widgets.*
import com.appservice.utils.getBitmap
import com.appservice.viewmodel.ServiceViewModel
import com.framework.exceptions.NoNetworkException
import com.framework.extensions.gone
import com.framework.extensions.observeOnce
import com.framework.extensions.visible
import com.framework.glide.util.glideLoad
import com.framework.imagepicker.ImagePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import okio.Buffer
import okio.BufferedSource
import org.json.JSONObject
import java.io.File
import java.nio.charset.Charset
import java.util.*
import kotlin.collections.ArrayList

class ServiceDetailFragment : AppBaseFragment<FragmentServiceDetailBinding, ServiceViewModel>() {

  private var menuDelete: MenuItem? = null
  private var serviceImage: File? = null
  private var product: Product? = null
  private var isNonPhysicalExperience: Boolean? = null
  private var currencyType: String? = null
  private var fpId: String? = null
  private var fpTag: String? = null
  private var clientId: String? = null
  private var externalSourceId: String? = null
  private var applicationId: String? = null
  private var userProfileId: String? = null
  private var pickUpDataAddress: ArrayList<PickUpData>? = null

  private var isEdit: Boolean? = null
  private var bankAccountDetail: BankAccountDetails? = null

  private var secondaryImage: ArrayList<FileModel> = ArrayList()

  private var secondaryDataImage: ArrayList<DataImage>? = null
  private var gstProductData: DataG? = null

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
    getBundleData()
    getPickUpAddress()
    binding?.vwChangeDeliverConfig?.paintFlags = Paint.UNDERLINE_TEXT_FLAG
    binding?.vwPaymentConfig?.paintFlags = Paint.UNDERLINE_TEXT_FLAG
    setOnClickListener(binding?.vwChangeDeliverConfig, binding?.vwChangeDeliverLocation, binding?.vwPaymentConfig,
        binding?.vwSavePublish, binding?.imageAddBtn, binding?.clearImage, binding?.btnOtherInfo, binding?.bankAccountView)
    binding?.toggleService?.setOnToggledListener { _, isOn ->
      binding?.payServiceView?.visibility = if (isOn) View.VISIBLE else View.GONE
      binding?.freeServiceView?.visibility = if (isOn) View.GONE else View.VISIBLE
    }
    listenerEditText()
  }

  private fun listenerEditText() {
    binding?.amountEdt?.afterTextChanged { calculate(binding?.amountEdt?.text.toString(), binding?.discountEdt?.text.toString()) }
    binding?.discountEdt?.afterTextChanged { calculate(binding?.amountEdt?.text.toString(), binding?.discountEdt?.text.toString()) }
  }

  private fun calculate(amount: String, dist: String) {
    val amountD = amount.toFloatOrNull() ?: 0F
    val distD = dist.toFloatOrNull() ?: 0F
    if (distD > amountD) {
      showLongToast("Discount amount can't be greater than price")
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
        pickUpDataAddress = if ((it.status == 200 || it.status == 201 || it.status == 202) && response?.data.isNullOrEmpty().not()) {
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
      if (isEdit == true) getAddPreviousData() else hideProgress()
    })
  }

  private fun getAddPreviousData() {
    viewModel?.getProductImage(auth_3, String.format("{'_pid':'%s'}", product?.productId))?.observeOnce(viewLifecycleOwner, Observer {
      if ((it.error is NoNetworkException).not()) {
        val response = it as? ProductImageResponse
        if (response?.status == 200 && response.data.isNullOrEmpty().not()) {
          secondaryDataImage = response.data
        }
        viewModel?.getProductGstDetail(auth_3, String.format("{'product_id':'%s'}", product?.productId))?.observeOnce(viewLifecycleOwner, Observer { it1 ->
          if ((it1.error is NoNetworkException).not()) {
            val response2 = it1 as? ProductGstResponse
            if (response2?.status == 200 && response2.data.isNullOrEmpty().not()) {
              gstProductData = response2.data?.get(0)
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
    binding?.tvServiceName?.setText(product?.Name)
    binding?.tvDesc?.setText(product?.Description)
    binding?.tvDesc?.setText(product?.Description)
    if (product?.paymentType == Product.PaymentType.ASSURED_PURCHASE.value && bankAccountDetail != null) {
      binding?.txtPaymentType?.text = resources.getString(R.string.boost_payment_gateway)
      binding?.bankAccountName?.visible()
      binding?.bankAccountName?.text = "${bankAccountDetail?.accountName} - ${bankAccountDetail?.accountNumber}"
      binding?.titleBankAdded?.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_ok_green, 0, 0, 0)
      binding?.titleBankAdded?.text = resources.getString(R.string.bank_account_added)
    } else if (product?.paymentType == Product.PaymentType.UNIQUE_PAYMENT_URL.value) {
      binding?.txtPaymentType?.text = resources.getString(R.string.external_url)
      binding?.edtUrl?.setText(product?.BuyOnlineLink?.url ?: "")
      binding?.edtNameDesc?.setText(product?.BuyOnlineLink?.description ?: "")
      binding?.bankAccountView?.gone()
      binding?.externalUrlView?.visible()
    } else {
      binding?.txtPaymentType?.text = resources.getString(R.string.boost_payment_gateway)
      binding?.bankAccountName?.gone()
      binding?.titleBankAdded?.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_info_circular_orange, 0, 0, 0)
      binding?.titleBankAdded?.text = resources.getString(R.string.bank_account_not_added)
    }
    if (product?.Price ?: 0.0 <= 0.0) {
      binding?.toggleService?.isOn = false
      binding?.payServiceView?.gone()
      binding?.freeServiceView?.visible()
    }
    binding?.amountEdt?.setText("${product?.Price ?: 0}")
    binding?.discountEdt?.setText("${product?.DiscountAmount ?: 0.0}")
    if (product?.ImageUri.isNullOrEmpty().not()) {
      binding?.imageAddBtn?.gone()
      binding?.clearImage?.visible()
      binding?.serviceImageView?.visible()
      binding?.serviceImageView?.let { activity?.glideLoad(it, product?.ImageUri!!, R.drawable.placeholder_image) }
    }
  }

  private fun showError(string: String) {
    hideProgress()
    showLongToast(string)
  }

  private fun getBundleData() {
    product = arguments?.getSerializable(IntentConstant.PRODUCT_DATA.name) as? Product
    isEdit = (product != null && product?.productId.isNullOrEmpty().not())
    isNonPhysicalExperience = arguments?.getBoolean(IntentConstant.NON_PHYSICAL_EXP_CODE.name)
    currencyType = arguments?.getString(IntentConstant.CURRENCY_TYPE.name)
    fpId = arguments?.getString(IntentConstant.FP_ID.name)
    fpTag = arguments?.getString(IntentConstant.FP_TAG.name)
    clientId = arguments?.getString(IntentConstant.CLIENT_ID.name)
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
      binding?.vwChangeDeliverLocation -> showServiceDeliveryLocationBottomSheet()
      binding?.vwPaymentConfig -> showPaymentConfigBottomSheet()
      binding?.btnOtherInfo -> {
        val bundle = Bundle()
        bundle.putSerializable(IntentConstant.PRODUCT_DATA.name, product)
        bundle.putSerializable(IntentConstant.NEW_FILE_PRODUCT_IMAGE.name, secondaryImage)
        bundle.putSerializable(IntentConstant.PRODUCT_IMAGE.name, secondaryDataImage)
        bundle.putSerializable(IntentConstant.PRODUCT_GST_DETAIL.name, gstProductData)
        startFragmentActivity(FragmentType.SERVICE_INFORMATION, bundle, isResult = true)
      }
      binding?.vwSavePublish -> if (isValid()) createUpdateApi()
    }
  }

  private fun createUpdateApi() {
    showProgress()
    if (isEdit == false) {
      viewModel?.createService(product)?.observeOnce(viewLifecycleOwner, Observer {
        if ((it.error is NoNetworkException).not()) {
          val productId = it.stringResponse
          if ((it.status == 200 || it.status == 201 || it.status == 202) && productId.isNullOrEmpty().not()) {
            addGstService(productId)
          } else showError("Service adding error, please try again.")
        } else showError(resources.getString(R.string.internet_connection_not_available))
      })
    } else {
      val updates = ArrayList<UpdateValue>()
      val json = JSONObject(Gson().toJson(product))
      val keys = json.keys()
      while (keys.hasNext()) {
        val key = keys.next()
        updates.add(UpdateValue(key, json[key].toString()))
      }
      val request = ProductUpdate(clientId, productId = product?.productId, productType = product?.productType, updates = updates)
      viewModel?.updateService(request)?.observeOnce(viewLifecycleOwner, Observer {
        if ((it.error is NoNetworkException).not()) {
          if ((it.status == 200 || it.status == 201 || it.status == 202)) {
            updateGstService(product?.productId)
          } else showError("Service updating error, please try again.")
        } else showError(resources.getString(R.string.internet_connection_not_available))
      })
    }
  }

  private fun updateGstService(productId: String?) {
    val gstData = gstProductData ?: DataG()
    val request = ProductUpdateRequest(false, query = String.format("{'product_id':'%s'}", productId))
    val setGST = SetGST(gstData.gstSlab?.toString() ?: "0.0", gstData.height?.toString() ?: "0.0",
        gstData.length?.toString() ?: "0.0", gstData.weight?.toString() ?: "0.0", gstData.width?.toString() ?: "0.0")
    request.updateValueSet(UpdateValueU(setGST))
    viewModel?.updateProductGstDetail(auth_3, request)?.observeOnce(viewLifecycleOwner, Observer {
      if ((it.error is NoNetworkException).not()) {
        if ((it.status == 200 || it.status == 201 || it.status == 202)) {
          hideProgress()
          uploadImageSingle(productId)
        } else showError("Service updating error, please try again.")
      } else showError(resources.getString(R.string.internet_connection_not_available))
    })
  }

  private fun addGstService(productId: String?) {
    val gstData = gstProductData ?: DataG()
    val request = ProductGstDetailRequest(ActionDataG(gstData.gstSlab ?: 0.0, 0.0, 0.0,
        merchantId = fpId, productId = productId, weight = 0.0, width = 0.0), fpId)
    viewModel?.addProductGstDetail(auth_3, request)?.observeOnce(viewLifecycleOwner, Observer {
      if ((it.error is NoNetworkException).not()) {
        if ((it.status == 200 || it.status == 201 || it.status == 202)) {
          hideProgress()
          uploadImageSingle(productId)
        } else showError("Service adding error, please try again.")
      } else showError(resources.getString(R.string.internet_connection_not_available))
    })
  }

  private fun uploadImageSingle(productId: String?) {
    showProgress("Uploading service image, please wait...")
    if (isEdit == true && serviceImage == null) {
      uploadSecondaryImage(productId)
      return
    }
    viewModel?.addUpdateImageProductService(clientId, "sequential", deviceId,
        1, 1, productId, getRequestServiceImage(serviceImage))?.observeOnce(viewLifecycleOwner, Observer {
      if ((it.error is NoNetworkException).not()) {
        if (it.status == 200 || it.status == 201 || it.status == 202) {
          uploadSecondaryImage(productId)
        } else showError("Service image uploading error, please try again.")
      } else showError(resources.getString(R.string.internet_connection_not_available))
    })
  }

  private fun getRequestServiceImage(serviceImage: File?): RequestBody {
    val responseBody = RequestBody.create(MediaType.parse("image/png"), serviceImage?.readBytes())
    val fileName = takeIf { serviceImage?.name.isNullOrEmpty().not() }?.let { serviceImage?.name }
        ?: "service_${Date().time}.png"
    return responseBody
  }

  private fun uploadSecondaryImage(productId: String?) {
    val images = secondaryImage.filter { it.path.isNullOrEmpty().not() }
    if (images.isNullOrEmpty().not()) {
      var checkPosition = 0
      val secondaryImageList = ArrayList<String>()
      images.forEach { fileData ->
        val secondaryFile = fileData.getFile()
        val fileNew = takeIf { secondaryFile?.name.isNullOrEmpty().not() }?.let { secondaryFile?.name } ?: "service_${Date()}.jpg"
        val requestProfile = RequestBody.create(MediaType.parse("image/*"), secondaryFile)
        val body = MultipartBody.Part.createFormData("file", fileNew, requestProfile)
        viewModel?.uploadImageProfile(auth_3, fileNew, body)?.observeOnce(viewLifecycleOwner, Observer {
          checkPosition += 1
          if ((it.error is NoNetworkException).not()) {
            if (it.status == 200 || it.status == 201 || it.status == 202) {
              val response = getResponse(it.responseBody) ?: ""
              if (response.isNotEmpty()) secondaryImageList.add(response)
            } else showError("Secondary Service image uploading error, please try again.")
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
        val request = ProductImageRequest(ActionDataI(ImageI(url = image, description = ""), productId), fpId)
        viewModel?.addProductImage(auth_3, request)?.observeOnce(viewLifecycleOwner, Observer {
          checkPosition += 1
          if ((it.error is NoNetworkException).not()) {
            if (it.status == 200 || it.status == 201 || it.status == 202) {
              Log.d(ServiceDetailFragment::class.java.name, "$it")
            } else showLongToast("Add secondary image data error, please try again.")
          } else showError(resources.getString(R.string.internet_connection_not_available))
          if (checkPosition == secondaryImageList.size) {
            showLongToast(if (isEdit == true) "Updated Service." else "Created Service.")
            goBack()
          }
        })
      }
    } else {
      showLongToast(if (isEdit == true) "Updated Service." else "Created Service.")
      goBack()
    }
  }

  private fun goBack() {
    hideProgress()
    val data = Intent()
    data.putExtra("LOAD", true)
    appBaseActivity?.setResult(Activity.RESULT_OK, data)
    appBaseActivity?.finish()
  }

  private fun getResponse(responseBody: ResponseBody?): String? {
    val source: BufferedSource? = responseBody?.source()
    source?.request(Long.MAX_VALUE)
    val buffer: Buffer? = source?.buffer
    return buffer?.clone()?.readString(Charset.forName("UTF-8"))
  }


  private fun isValid(): Boolean {
    val serviceName = binding?.tvServiceName?.text.toString()
    val serviceDesc = binding?.tvDesc?.text.toString()
    val amount = binding?.amountEdt?.text.toString().toDoubleOrNull() ?: 0.0
    val discount = binding?.discountEdt?.text.toString().toDoubleOrNull() ?: 0.0
    val tongle = binding?.toggleService?.isOn ?: false
    val externalUrlName = binding?.edtNameDesc?.text?.toString() ?: ""
    val externalUrl = binding?.edtUrl?.text?.toString() ?: ""

    if (serviceImage == null && product?.ImageUri.isNullOrEmpty()) {
      showLongToast(resources.getString(R.string.add_service_image))
      return false
    } else if (serviceName.isEmpty()) {
      showLongToast(resources.getString(R.string.enter_service_name))
      return false
    } else if (serviceDesc.isEmpty()) {
      showLongToast(resources.getString(R.string.enter_service_desc))
      return false
    } else if (tongle && amount <= 0.0) {
      showLongToast(resources.getString(R.string.enter_valid_price))
      return false
    } else if (tongle && (discount > amount)) {
      showLongToast(resources.getString(R.string.discount_amount_not_greater_than_price))
      return false
    } else if (tongle && (product?.paymentType.isNullOrEmpty() || (product?.paymentType == Product.PaymentType.ASSURED_PURCHASE.value && bankAccountDetail == null))) {
      showLongToast(resources.getString(R.string.please_add_bank_detail))
      return false
    } else if (tongle && (product?.paymentType == Product.PaymentType.UNIQUE_PAYMENT_URL.value && (externalUrlName.isNullOrEmpty() || externalUrl.isNullOrEmpty()))) {
      showLongToast(resources.getString(R.string.please_enter_valid_url_name))
      return false
    } else if (product?.category.isNullOrEmpty()) {
      showLongToast(resources.getString(R.string.please_fill_other_info))
      return false
    }
    product?.ClientId = clientId
    product?.FPTag = fpTag
    product?.CurrencyCode = currencyType
    product?.Name = serviceName
    product?.Description = serviceDesc
    product?.Price = if (tongle) amount else 0.0
    product?.DiscountAmount = if (tongle) discount else 0.0
    if (tongle && (product?.paymentType == Product.PaymentType.UNIQUE_PAYMENT_URL.value)) {
      product?.BuyOnlineLink = BuyOnlineLink(externalUrl, externalUrlName)
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
    binding?.serviceImageView?.gone()
    product?.ImageUri = null
    serviceImage = null
  }

  private fun openImagePicker() {
    val filterSheet = ImagePickerBottomSheet()
    filterSheet.isHidePdf(true)
    filterSheet.onClicked = { openImagePicker(it) }
    filterSheet.show(this@ServiceDetailFragment.parentFragmentManager, ImagePickerBottomSheet::class.java.name)
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
    } else if (resultCode == AppCompatActivity.RESULT_OK && requestCode == 101) {
      product = data?.getSerializableExtra(IntentConstant.PRODUCT_DATA.name) as? Product
      secondaryImage = (data?.getSerializableExtra(IntentConstant.NEW_FILE_PRODUCT_IMAGE.name) as? ArrayList<FileModel>) ?: ArrayList()
      gstProductData = data?.getSerializableExtra(IntentConstant.PRODUCT_GST_DETAIL.name) as? DataG
    } else if (resultCode == AppCompatActivity.RESULT_OK && requestCode == 202) {
      bankAccountDetail = data?.getSerializableExtra(IntentConstant.USER_BANK_DETAIL.name) as? BankAccountDetails
      if (bankAccountDetail != null) {
        product?.paymentType = Product.PaymentType.ASSURED_PURCHASE.value
        binding?.bankAccountView?.visible()
        binding?.externalUrlView?.gone()
        binding?.bankAccountName?.visible()
        binding?.bankAccountName?.text = "${bankAccountDetail?.accountName} - ${bankAccountDetail?.accountNumber}"
        binding?.titleBankAdded?.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_ok_green, 0, 0, 0)
        binding?.titleBankAdded?.text = resources.getString(R.string.bank_account_added)
      }
    }
  }

  private fun showServiceDeliveryConfigBottomSheet() {
    val dialog = ServiceDeliveryConfigBottomSheet()
    dialog.onClicked = { product?.prepaidOnlineAvailable = true }
    if (product?.prepaidOnlineAvailable != null) dialog.isUpdate(product?.prepaidOnlineAvailable ?: true)
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
        Product.PaymentType.ASSURED_PURCHASE.value -> {
          binding?.txtPaymentType?.text = resources.getString(R.string.boost_payment_gateway)
          binding?.bankAccountName?.visible()
          binding?.bankAccountName?.text = "${bankAccountDetail?.accountName} - ${bankAccountDetail?.accountNumber}"
          binding?.titleBankAdded?.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_ok_green, 0, 0, 0)
          binding?.titleBankAdded?.text = resources.getString(R.string.bank_account_added)
        }
        Product.PaymentType.UNIQUE_PAYMENT_URL.value -> {
          binding?.txtPaymentType?.text = resources.getString(R.string.external_url)
          binding?.bankAccountView?.gone()
          binding?.externalUrlView?.visible()
        }
        else -> {
          binding?.txtPaymentType?.text = resources.getString(R.string.boost_payment_gateway)
          binding?.bankAccountName?.gone()
          binding?.titleBankAdded?.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_info_circular_orange, 0, 0, 0)
          binding?.titleBankAdded?.text = resources.getString(R.string.bank_account_not_added)
        }
      }
    }
    dialog.onListenerChange = { goAddBankView() }
    if (((product?.paymentType == Product.PaymentType.ASSURED_PURCHASE.value && bankAccountDetail != null) ||
            (product?.paymentType == Product.PaymentType.UNIQUE_PAYMENT_URL.value)).not()) {
      product?.paymentType = ""
    }
    dialog.setDataPaymentGateway(bankAccountDetail, product?.paymentType)
    dialog.show(parentFragmentManager, PaymentConfigBottomSheet::class.java.name)
  }

  private fun goAddBankView() {
    val bundle = Bundle()
    bundle.putString(IntentConstant.CLIENT_ID.name, clientId)
    bundle.putString(IntentConstant.USER_PROFILE_ID.name, userProfileId)
    bundle.putString(IntentConstant.FP_ID.name, fpId)
    bundle.putBoolean(IntentConstant.IS_SERVICE_CREATION.name, true)
    val fragment = if (bankAccountDetail != null) FragmentType.BANK_ACCOUNT_DETAILS else FragmentType.ADD_BANK_ACCOUNT_START
    startFragmentAccountActivity(fragment, bundle, isResult = true, requestCode = 202)
  }


  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    inflater.inflate(R.menu.menu_delete, menu)
    menuDelete = menu.findItem(R.id.menu_delete)
    menuDelete?.isVisible = isEdit ?: false
    super.onCreateOptionsMenu(menu, inflater)
  }


  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return when (item.itemId) {
      R.id.menu_delete -> {
        MaterialAlertDialogBuilder(baseActivity).setMessage(resources.getString(R.string.are_you_sure))
            .setNegativeButton(resources.getString(R.string.cancel)) { d, _ -> d.dismiss() }.setPositiveButton(resources.getString(R.string.delete)) { d, _ ->
              d.dismiss()
              showProgress()
              val request = DeleteProductRequest(clientId, "SINGLE", product?.productId, product?.productType)
              viewModel?.deleteService(request)?.observeOnce(viewLifecycleOwner, Observer {
                hideProgress()
                if ((it.error is NoNetworkException).not()) {
                  if ((it.status == 200 || it.status == 201 || it.status == 202)) {
                    goBack()
                  } else showError("Removing product failed, please try again.")
                } else showError(resources.getString(R.string.internet_connection_not_available))
              })
            }.show()
        true
      }
      else -> super.onOptionsItemSelected(item)
    }
  }
}