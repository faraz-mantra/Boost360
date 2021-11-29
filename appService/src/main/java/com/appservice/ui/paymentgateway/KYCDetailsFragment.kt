package com.appservice.ui.paymentgateway

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.constant.FragmentType
import com.appservice.constant.IntentConstant
import com.appservice.constant.PreferenceConstant
import com.appservice.databinding.FragmentKycDetailsBinding
import com.appservice.model.FileModel
import com.appservice.model.SessionData
import com.appservice.model.accountDetails.AccountDetailsResponse
import com.appservice.model.accountDetails.BankAccountDetails
import com.appservice.model.kycData.DataKyc
import com.appservice.model.kycData.PaymentKycDataResponse
import com.appservice.model.paymentKyc.ActionDataKyc
import com.appservice.model.paymentKyc.PaymentKycRequest
import com.appservice.model.paymentKyc.update.KycSet
import com.appservice.model.paymentKyc.update.UpdateKycValue
import com.appservice.model.paymentKyc.update.UpdatePaymentKycRequest
import com.appservice.model.razor.RazorDataResponse
import com.appservice.recyclerView.AppBaseRecyclerViewAdapter
import com.appservice.recyclerView.BaseRecyclerViewItem
import com.appservice.recyclerView.RecyclerItemClickListener
import com.appservice.ui.catalog.widgets.ClickType
import com.appservice.ui.catalog.widgets.ImagePickerBottomSheet
import com.appservice.utils.*
import com.appservice.viewmodel.WebBoostKitViewModel
import com.framework.exceptions.NoNetworkException
import com.framework.extensions.afterTextChanged
import com.framework.extensions.gone
import com.framework.extensions.observeOnce
import com.framework.extensions.visible
import com.framework.glide.util.glideLoad
import com.framework.imagepicker.ImagePicker
import com.framework.utils.ValidationUtils
import com.framework.utils.convertListObjToString
import com.framework.utils.convertStringToList
import com.framework.webengageconstant.KYC_VERIFICATION
import com.framework.webengageconstant.KYC_VERIFICATION_REQUESTED
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import okio.Buffer
import okio.BufferedSource
import org.json.JSONObject
import java.io.File
import java.nio.charset.Charset
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.ArrayList

class KYCDetailsFragment : AppBaseFragment<FragmentKycDetailsBinding, WebBoostKitViewModel>(), RecyclerItemClickListener {

  private val FILE_SELECT_CODE = 2000
  private var imagePickerMultiple: Boolean? = null
  private var bankStatementImage: File? = null
  private var additionalDocs: ArrayList<FileModel> = ArrayList()
  private var adapterImage: AppBaseRecyclerViewAdapter<FileModel>? = null
  private var session: SessionData? = null
  private var isBankAssociated = false
  private var bankDetail: BankAccountDetails? = null
  private var panCarImage: File? = null
  private var isValidIfsc: Boolean = false
  private var isEdit: Boolean = false
  private var request: PaymentKycRequest? = null
  private var dataKyc: DataKyc? = null
  private var isInstaMojoAccount: Boolean? = null

  companion object {
    @JvmStatic
    fun newInstance(bundle: Bundle? = null): KYCDetailsFragment {
      val fragment = KYCDetailsFragment()
      fragment.arguments = bundle
      return fragment
    }
  }

  override fun getLayout(): Int {
    return R.layout.fragment_kyc_details
  }

  override fun getViewModelClass(): Class<WebBoostKitViewModel> {
    return WebBoostKitViewModel::class.java
  }

  override fun onCreateView() {
    super.onCreateView()
    session = arguments?.getSerializable(IntentConstant.SESSION_DATA.name) as? SessionData
    isEdit = arguments?.getBoolean(IntentConstant.IS_EDIT.name) ?: false
    isInstaMojoAccount = arguments?.getBoolean("isInstaMojoAccount")
    if (isEdit) {
      getKycDetails()
    } else getUserDetails()
    val panImagePath = arguments?.getString(IntentConstant.PAN_CARD_IMAGE.name)
    if (panImagePath.isNullOrEmpty().not()) {
      val panImageUri = Uri.parse(panImagePath)
      binding?.imagePanCard?.setImageURI(panImageUri)
      panCarImage = File(FileUtils(baseActivity).getPath(panImageUri) ?: "")
    }
    binding?.edtBankIfscCode?.afterTextChanged {
      val ifsc = binding?.edtBankIfscCode?.text.toString().trim()
      if (ifsc.length == 11) {
        apiGetIfscDetail(ifsc, true)
        binding?.edtBankName?.isFocusable = false
      } else {
        binding?.edtBankName?.isFocusable = true
      }
    }
    setOnClickListener(
      binding?.btnSubmitDetails,
      binding?.btnRetakePanImage,
      binding?.btnBankStatementPicker,
      binding?.btnAdditionalDocs,
      binding?.btnClearBankStatementImage,
      binding?.btnAnotherAccount,
      binding?.btnMyAccount
    )
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.btnSubmitDetails -> if (isValidAndGenerateRequest()) bottomSheetConfirm()
      binding?.btnRetakePanImage -> {
        if (isEdit) {
          val bundle = Bundle()
          bundle.putBoolean(IntentConstant.IS_EDIT.name, true)
          bundle.putSerializable(IntentConstant.SESSION_DATA.name, session)
          startFragmentPaymentActivity(FragmentType.SCAN_PAN_CARD, bundle, isResult = true)
        } else baseActivity.onNavPressed()
      }
      binding?.btnBankStatementPicker -> openImagePicker(false)
      binding?.btnAdditionalDocs -> openImagePicker(true)
      binding?.btnClearBankStatementImage -> {
        dataKyc?.bankAccountStatement = ""
        binding?.bankStatementView?.gone()
        bankStatementImage = null
        binding?.tvAddBankStatment?.text = getString(R.string.add_file_jpg_png)
      }
      binding?.btnAnotherAccount -> bankAssociated(false)
      binding?.btnMyAccount -> bankAssociated(true)
    }
  }

  private fun updateApiBankDetail() {
    if (panCarImage != null) {
      saveApiBankDetail()
    } else if (bankStatementImage != null) {
      uploadBankStatement()
    } else if (additionalDocs.isNotEmpty() && isAddAdditionFile()) {
      uploadAdditionalDocument()
    } else {
      val list = additionalDocs.map { it.pathUrl } as ArrayList<String>
      addKycInformation(list)
    }
  }

  fun isAddAdditionFile(): Boolean {
    var b = false
    additionalDocs.forEach { if (it.path.isNullOrEmpty().not()) b = true }
    return b
  }


  private fun saveApiBankDetail() {
    showProgress(resources.getString(R.string.uploading_file_wait))
    val filePancard = takeIf { panCarImage?.name.isNullOrEmpty().not() }?.let { panCarImage?.name }
      ?: "pan_card_${Date()}.jpg"
    val mimType = panCarImage?.getMimeType() ?: "multipart/form-data"
    val requestBody = panCarImage?.let { it.asRequestBody(mimType.toMediaTypeOrNull()) }
    val bodyPanCard =
      requestBody?.let { MultipartBody.Part.createFormData("file", filePancard, it) }
    viewModel?.putUploadFile(session?.auth_2, bodyPanCard, filePancard)
      ?.observeOnce(viewLifecycleOwner, Observer {
        if ((it.error is NoNetworkException).not()) {
          if (it.status == 200 || it.status == 201 || it.status == 202) {
            request?.actionData?.panCardDocument = getResponse(it.responseBody)
            if (bankStatementImage != null) {
              uploadBankStatement()
            } else if (additionalDocs.isNotEmpty() && isAddAdditionFile()) {
              uploadAdditionalDocument()
            } else {
              val list = additionalDocs.map { path -> path.pathUrl } as ArrayList<String>
              addKycInformation(list)
            }
          } else showError(resources.getString(R.string.failed_to_upload_pan_card))
        } else showError(resources.getString(R.string.internet_connection_not_available))
      })
  }

  private fun uploadBankStatement() {
    showProgress(resources.getString(R.string.uploading_file_wait))
    val fileStatement =
      takeIf { bankStatementImage?.name.isNullOrEmpty().not() }?.let { bankStatementImage?.name }
        ?: "bank_statement_${Date()}.jpg"
    val mimType = bankStatementImage?.getMimeType() ?: "multipart/form-data"
    val requestBody = bankStatementImage?.let { it.asRequestBody(mimType.toMediaTypeOrNull()) }
    val bodyStatement =
      requestBody?.let { MultipartBody.Part.createFormData("file", fileStatement, it) }
    viewModel?.putUploadFile(session?.auth_2, bodyStatement, fileStatement)
      ?.observeOnce(viewLifecycleOwner, Observer {
        if ((it.error is NoNetworkException).not()) {
          if (it.status == 200 || it.status == 201 || it.status == 202) {
            hideProgress()
            request?.actionData?.bankAccountStatement = getResponse(it.responseBody)
            if (additionalDocs.isNotEmpty() && isAddAdditionFile()) {
              uploadAdditionalDocument()
            } else {
              val list = additionalDocs.map { path -> path.pathUrl } as ArrayList<String>
              addKycInformation(list)
            }
          } else showError(resources.getString(R.string.failed_to_upload_statement))
        } else showError(resources.getString(R.string.internet_connection_not_available))
      })

  }

  private fun uploadAdditionalDocument() {
    val respFileList = ArrayList<String>()
    if (additionalDocs.isNullOrEmpty().not()) {
      showProgress(resources.getString(R.string.uploading_file_wait))
      var checkPosition = 0
      additionalDocs.forEach { fileData ->
        val file = fileData.getFile()
        if (file == null) {
          checkPosition += 1
          fileData.pathUrl?.let { respFileList.add(it) }
          if (checkPosition == additionalDocs.size) {
            hideProgress()
            addKycInformation(respFileList)
          }
        } else {
          val fileAdditional = takeIf { file.name.isNullOrEmpty().not() }?.let { file.name }
            ?: "additional_doc_${Date()}.jpg"
          val mimType = file.getMimeType() ?: "multipart/form-data"
          val requestBody = file.asRequestBody(mimType.toMediaTypeOrNull())
          val bodyAdditional =
            MultipartBody.Part.createFormData("file", fileAdditional, requestBody)
          viewModel?.putUploadFile(session?.auth_2, bodyAdditional, fileAdditional)
            ?.observeOnce(viewLifecycleOwner, Observer {
              checkPosition += 1
              if ((it.error is NoNetworkException).not()) {
                if (it.status == 200 || it.status == 201 || it.status == 202) {
                  getResponse(it.responseBody)?.let { it1 -> respFileList.add(it1) }
                } else showError(resources.getString(R.string.failed_to_upload_additional_doc))
              } else showError(resources.getString(R.string.internet_connection_not_available))
              if (checkPosition == additionalDocs.size) {
                hideProgress()
                addKycInformation(respFileList)
              }
            })
        }
      }
    } else {
      addKycInformation(respFileList)
    }
  }

  private fun addKycInformation(additionFile: ArrayList<String>) {
    request?.actionData?.additionalDocument = convertListObjToString(additionFile)
    if (isEdit.not()) {
      showProgress(resources.getString(R.string.please_wait_))
      viewModel?.addKycData(session?.auth_1, request)?.observeOnce(viewLifecycleOwner, Observer {
        hideProgress()
        if ((it.error is NoNetworkException).not()) {
          if (it.status == 200 || it.status == 201 || it.status == 202) {
            setPreference()
            session?.fpTag?.let { it1 ->
              WebEngageController.trackEvent(
                KYC_VERIFICATION_REQUESTED,
                KYC_VERIFICATION,
                it1
              )
            }
            val bundle = Bundle()
            bundle.putSerializable(IntentConstant.SESSION_DATA.name, session)
            bundle.putSerializable(IntentConstant.KYC_DETAIL.name, request)
            startFragmentPaymentActivity(FragmentType.KYC_STATUS, bundle, clearTop = true)
          } else showError(resources.getString(R.string.add_kyc_error))
        } else showError(resources.getString(R.string.internet_connection_not_available))
      })
    } else updateKycInformation(getUpdateRequest(request))
  }

  private fun setPreference() {
    val editor = pref?.edit()
    editor?.putBoolean(PreferenceConstant.IS_SELF_BRANDED_KYC_ADD, true)
    editor?.apply()
  }


  private fun updateKycInformation(updateRequest: UpdatePaymentKycRequest) {
    showProgress(resources.getString(R.string.please_wait_))
    viewModel?.updateKycData(session?.auth_1, updateRequest)
      ?.observeOnce(viewLifecycleOwner, Observer {
        hideProgress()
        if ((it.error is NoNetworkException).not()) {
          if (it.status == 200 || it.status == 201 || it.status == 202) {
            session?.fpTag?.let { it1 ->
              WebEngageController.trackEvent(
                KYC_VERIFICATION_REQUESTED,
                KYC_VERIFICATION,
                it1
              )
            }
            val output = Intent()
            output.putExtra(IntentConstant.IS_EDIT.name, true)
            baseActivity.setResult(AppCompatActivity.RESULT_OK, output)
            baseActivity.finish()
          } else showError(resources.getString(R.string.update_kyc_error))
        } else showError(resources.getString(R.string.internet_connection_not_available))
      })
  }

  private fun getUpdateRequest(request: PaymentKycRequest?): UpdatePaymentKycRequest {
    val requestUpdate = UpdatePaymentKycRequest(query = getQueryId())
    val kycSet = KycSet(
      additionalDocument = request?.actionData?.additionalDocument,
      bankAccountNumber = request?.actionData?.bankAccountNumber,
      bankAccountStatement = request?.actionData?.bankAccountStatement,
      bankBranchName = request?.actionData?.bankBranchName,
      hasexisistinginstamojoaccount = request?.actionData?.hasexisistinginstamojoaccount,
      ifsc = request?.actionData?.ifsc,
      instamojoEmail = request?.actionData?.instamojoEmail,
      instamojoPassword = request?.actionData?.instamojoPassword,
      isArchived = dataKyc?.isArchived,
      nameOfBank = request?.actionData?.nameOfBank,
      nameOfBankAccountHolder = request?.actionData?.nameOfBankAccountHolder,
      nameOfPanHolder = request?.actionData?.nameOfPanHolder,
      panCardDocument = request?.actionData?.panCardDocument,
      panNumber = request?.actionData?.panNumber
    )
    val value = UpdateKycValue(set = kycSet)
    requestUpdate.setUpdateValueKyc(value)
    return requestUpdate
  }

  private fun getQueryId(): String? {
    val jsonObject = JSONObject()
    jsonObject.put("_id", dataKyc?.id)
    return jsonObject.toString()
  }

  private fun showError(errorTxt: String) {
    hideProgress()
    showLongToast(errorTxt)
  }

  private fun getResponse(responseBody: ResponseBody?): String? {
    val source: BufferedSource? = responseBody?.source()
    source?.request(Long.MAX_VALUE)
    val buffer: Buffer? = source?.buffer
    return buffer?.clone()?.readString(Charset.forName("UTF-8"))
  }

  private fun isValidAndGenerateRequest(): Boolean {
    val panNumber = binding?.edtPanNumber?.text?.toString()
    val panName = binding?.edtNameOnPanCard?.text?.toString()
    // if add new
    val accountNumber = binding?.edtBankAccountNumber?.text?.toString()
    val nameAccount = binding?.edtBankAccountHolderName?.text?.toString()
    val ifsc = binding?.edtBankIfscCode?.text?.toString()
    val bankName = binding?.edtBankName?.text?.toString()
    val bankBranch = binding?.edtBankBranch?.text?.toString()

    when {
      panCarImage == null && dataKyc?.panCardDocument.isNullOrEmpty() -> {
        showShortToast("Please select valid pan card file")
        return false
      }
      panNumber.isNullOrEmpty() -> {
        showShortToast("Pan number can't empty.")
        return false
      }
      !isPanNumberValid(panNumber) -> {
        showShortToast("Please enter a valid pan number.")
        return false
      }
      panName.isNullOrEmpty() -> {
        showShortToast("Pan name can't empty.")
        return false
      }
      !isNameValid(panName) -> {
        showShortToast("Please enter a valid name.")
        return false
      }
      bankStatementImage == null && dataKyc?.bankAccountStatement.isNullOrEmpty() -> {
        showShortToast("Please select valid bank statement file")
        return false
      }
      binding?.addDifferent?.isChecked == true -> {
        if (accountNumber.isNullOrEmpty()) {
          showShortToast(getString(R.string.bank_number_can_not_empty))
          return false
        } else if (accountNumber.length < 9) {
          showShortToast(getString(R.string.account_less_than_nine))
          return false
        } else if (accountNumber.length > 18) {
          showShortToast(getString(R.string.account_greater_than_nine))
          return false
        }else if (ValidationUtils.isBankAcValid(accountNumber)){
          showShortToast(getString(R.string.invalid_bank_account_number))
          return false
        }
        else if (nameAccount.isNullOrEmpty()) {
          showShortToast(getString(R.string.bank_account_cannot_empty))
          return false
        } else if (ifsc.isNullOrEmpty()) {
          showShortToast(getString(R.string.bank_ifcs_cannot_empty))
          return false
        } else if (ifsc.length < 11 || !isValidIfsc) {
          showLongToast(getString(R.string.please_enter_valid_ifcs))
          return false
        } else if (bankName.isNullOrEmpty()) {
          showShortToast(getString(R.string.bank_name_cant_empty))
          return false
        }
      }
      bankStatementImage == null && dataKyc?.bankAccountStatement.isNullOrEmpty() -> {
        showShortToast("Please select valid bank statement file")
        return false
      }
    }
    val hasexisistinginstamojoaccount = if (isInstaMojoAccount != null) {
      if (isInstaMojoAccount == true) DataKyc.HasInginstaMojo.YES.name else DataKyc.HasInginstaMojo.NO.name
    } else if (session?.isPaymentGateway == true) {
      DataKyc.HasInginstaMojo.PAID.name
    } else DataKyc.HasInginstaMojo.UNPAID.name

    request = if (binding?.addDifferent?.isChecked == true) {
      val action = ActionDataKyc(
        panNumber = panNumber,
        nameOfPanHolder = panName,
        bankAccountNumber = accountNumber,
        nameOfBankAccountHolder = nameAccount,
        nameOfBank = bankName,
        ifsc = ifsc,
        bankBranchName = bankBranch,
        hasexisistinginstamojoaccount = hasexisistinginstamojoaccount,
        instamojoEmail = dataKyc?.instamojoEmail ?: "",
        instamojoPassword = dataKyc?.instamojoPassword ?: "",
        fpTag = session?.fpTag,
        isVerified = dataKyc?.isVerified ?: DataKyc.Verify.NO.name
      )
      PaymentKycRequest(actionData = action, websiteId = session?.websiteId)
    } else {
      val action = ActionDataKyc(
        panNumber = panNumber,
        nameOfPanHolder = panName,
        bankAccountNumber = bankDetail?.accountNumber,
        nameOfBankAccountHolder = bankDetail?.accountName,
        nameOfBank = bankDetail?.bankName,
        ifsc = bankDetail?.iFSC,
        bankBranchName = bankDetail?.bankBranch,
        hasexisistinginstamojoaccount = hasexisistinginstamojoaccount,
        instamojoEmail = dataKyc?.instamojoEmail ?: "",
        instamojoPassword = dataKyc?.instamojoPassword ?: "",
        fpTag = session?.fpTag,
        isVerified = dataKyc?.isVerified ?: DataKyc.Verify.NO.name
      )
      PaymentKycRequest(actionData = action, websiteId = session?.websiteId)
    }
    return true
  }

  private fun bankAssociated(isBank: Boolean) {
    if (isBank) {
      binding?.addDifferent?.isChecked = false
      binding?.rbStoredAccount?.isChecked = true
      binding?.llAddBankDetails?.gone()
    } else {
      binding?.rbStoredAccount?.isChecked = false
      binding?.addDifferent?.isChecked = true
      binding?.llAddBankDetails?.visible()
    }
  }

  private fun openImagePicker(allowMultiple: Boolean) {
    val filterSheet = ImagePickerBottomSheet()
    filterSheet.isHidePdf(true)
    filterSheet.onClicked = { openImagePicker(it, allowMultiple) }
    filterSheet.show(
      this@KYCDetailsFragment.parentFragmentManager,
      ImagePickerBottomSheet::class.java.name
    )
  }

  private fun openImagePicker(it: ClickType, allowMultiple: Boolean) {
    imagePickerMultiple = allowMultiple
    if (it != ClickType.PDF) {
      val type = if (it == ClickType.CAMERA) ImagePicker.Mode.CAMERA else ImagePicker.Mode.GALLERY
      ImagePicker.Builder(baseActivity).mode(type)
        .compressLevel(ImagePicker.ComperesLevel.MEDIUM)
        .directory(ImagePicker.Directory.DEFAULT)
        .extension(ImagePicker.Extension.PNG)
        .allowMultipleImages(allowMultiple)
        .enableDebuggingMode(true).build()
    } else {
      if (checkPermission()) showFileChooser()
    }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == ImagePicker.IMAGE_PICKER_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK) {
      val mPaths = data?.getSerializableExtra(ImagePicker.EXTRA_IMAGE_PATH) as ArrayList<String>
      if (mPaths.size > 0) {
        // Only a single bank statement file is coming.
        if (this.imagePickerMultiple == false) {
          bankStatementImage = File(mPaths[0])
          binding?.bankStatementView?.visible()
          bankStatementImage?.getBitmap()?.let { binding?.ivBankStatement?.setImageBitmap(it) }
          binding?.tvAddBankStatment?.text = getString(R.string.change_file_jpg_png)
        }
        // Multiple files might come. These are the additional docs
        else additionalDocsViewPopulation(mPaths)
      }
    } else if (requestCode == FILE_SELECT_CODE && resultCode == AppCompatActivity.RESULT_OK) {
      val path = data?.data?.let { FileUtils(baseActivity).getPath(it) } ?: return
      if ((File(path).length() / 1024).toInt() > 1040) {
        showLongToast("File must be lessor than 1 MB.")
        return
      }
      if (imagePickerMultiple == false) {
        bankStatementImage = File(path)
        binding?.bankStatementView?.visible()
        binding?.ivBankStatement?.setImageResource(R.drawable.ic_pdf_placholder)
      } else additionalDocsViewPopulation(arrayListOf(path))
    } else if (resultCode == AppCompatActivity.RESULT_OK && requestCode == 101) {
      val panImagePath = data?.getStringExtra(IntentConstant.PAN_CARD_IMAGE.name)
      val panImageUri = Uri.parse(panImagePath)
      binding?.imagePanCard?.setImageURI(panImageUri)
      panCarImage = File(FileUtils(baseActivity).getPath(panImageUri) ?: "")
    }
  }

  private fun additionalDocsViewPopulation(mPaths: ArrayList<String>) {
    if (additionalDocs.size < 5) {
      if (mPaths.size + additionalDocs.size > 5) showLongToast("Only 5 files are allowed. Discarding the rest.")
      var index: Int = additionalDocs.size
      while (index < 5 && mPaths.isNotEmpty()) {
        additionalDocs.add(FileModel(path = mPaths[0]))
        mPaths.removeAt(0)
        index++
      }
      setAdapter()
    } else showLongToast("Only 5 files are allowed.")
  }

  private fun setAdapter() {
    if (adapterImage == null) {
      binding?.rvAdditionalDocs?.apply {
        adapterImage =
          AppBaseRecyclerViewAdapter(baseActivity, additionalDocs, this@KYCDetailsFragment)
        adapter = adapterImage
      }
    } else adapterImage?.notifyDataSetChanged()
  }

  override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
    additionalDocs.remove(item as? FileModel)
    setAdapter()
  }

  private fun checkPermission(): Boolean {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      val result =
        baseActivity.checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
      return result == PackageManager.PERMISSION_GRANTED
    }
    return true
  }

  private fun showFileChooser() {
    val mimeTypes = arrayOf("application/pdf")
    val selectFile = Intent(Intent.ACTION_GET_CONTENT)
    selectFile.type = if (mimeTypes.size == 1) mimeTypes[0] else "*/*"
    if (mimeTypes.isNotEmpty()) selectFile.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
    try {
      startActivityForResult(Intent.createChooser(selectFile, "Choose a file"), FILE_SELECT_CODE)
    } catch (ex: ActivityNotFoundException) {
      showShortToast("Please install a File Manager.")
    }
  }

  private fun getUserDetails() {
    showProgress()
    viewModel?.userAccountDetails(session?.fpId, session?.clientId)
      ?.observeOnce(viewLifecycleOwner, Observer {
        if (it.status == 200 || it.status == 201 || it.status == 202) {
          val response = it as? AccountDetailsResponse
          if (response?.result?.bankAccountDetails != null) {
            isIfscValid(response.result?.bankAccountDetails)
          } else updateUiBankAssociated(null, "", false)
        } else updateUiBankAssociated(null, "", false)
      })
  }

  private fun isIfscValid(bankDetail: BankAccountDetails?) {
    this.bankDetail = bankDetail
    val ifsc = bankDetail?.iFSC ?: ""
    if (ifsc.length == 11) apiGetIfscDetail(ifsc)
    else updateUiBankAssociated(bankDetail, "", false)
  }

  fun apiGetIfscDetail(ifsc: String, isValidateIfsc: Boolean = false) {
    viewModel?.ifscDetail(ifsc)?.observeOnce(viewLifecycleOwner, Observer {
      val data = it as? RazorDataResponse
      if (isValidateIfsc) responseValidateIfsc(data) else responseIfsc(data)
    })
  }

  private fun responseIfsc(data: RazorDataResponse?) {
    if (data?.status == 200 || data?.status == 201 || data?.status == 202) {
      this.bankDetail?.bankBranch = data.bRANCH
      updateUiBankAssociated(bankDetail, "${data.bANK} - ${data.bRANCH}", true)
    } else updateUiBankAssociated(bankDetail, "", false)
  }

  private fun updateUiBankAssociated(
    bankDetail: BankAccountDetails?,
    detail: String,
    isBankAssociated: Boolean
  ) {
    hideProgress()
    this.isBankAssociated = isBankAssociated
    this.bankDetail = bankDetail
    binding?.btnMyAccount?.visibility = if (this.isBankAssociated) View.VISIBLE else View.GONE
    if (this.isBankAssociated) {
      binding?.account?.text = "A/C No. ${bankDetail?.accountNumber}"
      binding?.bankDetail?.text = detail
    }
    if (isEdit) {
      if (dataKyc?.bankAccountNumber == bankDetail?.accountNumber) {
        bankAssociated(this.isBankAssociated)
        setPreviousData(this.isBankAssociated)
      } else {
        bankAssociated(false)
        setPreviousData(false)
      }
    } else bankAssociated(this.isBankAssociated)
  }

  private fun responseValidateIfsc(data: RazorDataResponse?) {
    if (data?.status == 200 || data?.status == 201 || data?.status == 202) {
      isValidIfsc = true
      binding?.edtBankName?.setText(data.bANK ?: "")
      if (data.bRANCH.isNullOrEmpty().not()) {
        binding?.edtBankBranch?.setText(data.bRANCH)
        binding?.txtBranch?.visible()
        binding?.edtBankBranch?.visible()
      }
    } else {
      isValidIfsc = false
      binding?.edtBankName?.setText("")
      binding?.txtBranch?.gone()
      binding?.edtBankBranch?.gone()
      showLongToast(resources.getString(R.string.invalid_ifsc))
    }
  }

  private fun bottomSheetConfirm() {
    val sheet = ConfirmKycBottomSheet()
    sheet.setData(request, panCarImage, bankStatementImage, additionalDocs, dataKyc)
    sheet.onClicked = {
      if (isEdit) updateApiBankDetail()
      else saveApiBankDetail()
    }
    sheet.show(
      this@KYCDetailsFragment.parentFragmentManager,
      ConfirmKycBottomSheet::class.java.name
    )
  }

  private fun getKycDetails() {
    showProgress()
    viewModel?.getKycData(session?.auth_1, getQuery())?.observeOnce(viewLifecycleOwner, Observer {
      hideProgress()
      if ((it.error is NoNetworkException).not()) {
        val resp = it as? PaymentKycDataResponse
        if (it.status == 200 || it.status == 201 || it.status == 202) {
          if (resp?.data.isNullOrEmpty().not()) {
            dataKyc = resp!!.data!![0]
            getUserDetails()
          } else {
            baseActivity.onNavPressed()
            showLongToast("Kyc detail not found.")
          }
        } else {
          baseActivity.onNavPressed()
          showLongToast(
            if (it.message().isNotEmpty()) it.message() else "Kyc detail getting error."
          )
        }
      } else showLongToast(resources.getString(R.string.internet_connection_not_available))
    })
  }

  private fun getQuery(): String? {
    val json = JSONObject()
    json.put("fpTag", session?.fpTag)
    return json.toString()
  }

  private fun setPreviousData(isEditBankAssociate: Boolean) {
    if (isEditBankAssociate.not()) {
      binding?.edtBankAccountNumber?.setText(dataKyc?.bankAccountNumber)
      binding?.edtBankAccountHolderName?.setText(dataKyc?.nameOfBankAccountHolder)
      binding?.edtBankIfscCode?.setText(dataKyc?.ifsc)
      binding?.edtBankName?.setText(dataKyc?.nameOfBank)
      binding?.edtBankBranch?.setText(dataKyc?.nameOfBank)
    }
    binding?.edtPanNumber?.setText(dataKyc?.panNumber)
    binding?.edtNameOnPanCard?.setText(dataKyc?.nameOfPanHolder)
    dataKyc?.panCardDocument?.let {
      activity?.glideLoad(
        binding?.imagePanCard!!,
        it,
        R.drawable.placeholder_image
      )
    }
    val exUrl = getExtensionUrl(dataKyc?.bankAccountStatement)
    if (exUrl.toLowerCase(Locale.ROOT) == "jpg" || exUrl.toLowerCase(Locale.ROOT) == "png") {
      binding?.bankStatementView?.visible()
      dataKyc?.bankAccountStatement?.let {
        activity?.glideLoad(
          binding?.ivBankStatement!!,
          it,
          R.drawable.placeholder_image
        )
      }
    } else {
      binding?.bankStatementView?.visible()
      binding?.ivBankStatement?.setImageResource(R.drawable.ic_pdf_placholder)
    }

    if (dataKyc?.additionalDocument.isNullOrEmpty().not()) {
      val pathUrls: List<String>? = convertStringToList(dataKyc?.additionalDocument!!)
      if (pathUrls.isNullOrEmpty().not()) {
        pathUrls?.forEachIndexed { index, url ->
          if (index <= 4) additionalDocs.add(FileModel(pathUrl = url))
        }
        setAdapter()
      }
    }
  }

  private fun isPanNumberValid(panNumber: String): Boolean {
    return Pattern.compile("[A-Z]{5}[0-9]{4}[A-Z]{1}").matcher(panNumber).matches()
  }

  private fun isNameValid(name: String): Boolean {
    return Pattern.compile("^([^0-9]*)\$").matcher(name).matches() || Pattern.compile("[$&+,:;=\\\\?@#|/'<>.^*()%!-]").matcher(name).matches()
  }
}