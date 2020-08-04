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
import com.appservice.databinding.FragmentKycDetailsBinding
import com.appservice.model.FileModel
import com.appservice.model.SessionData
import com.appservice.model.accountDetails.AccountDetailsResponse
import com.appservice.model.accountDetails.BankAccountDetails
import com.appservice.model.paymentKyc.ActionDataKyc
import com.appservice.model.paymentKyc.PaymentKycRequest
import com.appservice.model.razor.RazorDataResponse
import com.appservice.recyclerView.AppBaseRecyclerViewAdapter
import com.appservice.recyclerView.BaseRecyclerViewItem
import com.appservice.recyclerView.RecyclerItemClickListener
import com.appservice.ui.catlogService.widgets.ClickType
import com.appservice.ui.catlogService.widgets.ImagePickerBottomSheet
import com.appservice.utils.FileUtils
import com.appservice.utils.getBitmap
import com.appservice.utils.getMimeType
import com.appservice.viewmodel.WebBoostKitViewModel
import com.framework.exceptions.NoNetworkException
import com.framework.extensions.afterTextChanged
import com.framework.extensions.gone
import com.framework.extensions.observeOnce
import com.framework.extensions.visible
import com.framework.imagepicker.ImagePicker
import com.framework.utils.convertListObjToString
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import okio.Buffer
import okio.BufferedSource
import java.io.File
import java.nio.charset.Charset
import java.util.*
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
  private var request: PaymentKycRequest? = null

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
    getUserDetails()
    val panImagePath = arguments?.getString(IntentConstant.PAN_CARD_IMAGE.name)
    val panImageUri = Uri.parse(panImagePath)
    binding?.imagePanCard?.setImageURI(panImageUri)
    panCarImage = File(FileUtils(baseActivity).getPath(panImageUri) ?: "")
    setOnClickListener(binding?.btnSubmitDetails, binding?.btnRetakePanImage, binding?.btnBankStatementPicker,
        binding?.btnAdditionalDocs, binding?.btnClearBankStatementImage, binding?.btnAnotherAccount, binding?.btnMyAccount)

    binding?.edtBankIfscCode?.afterTextChanged {
      val ifsc = binding?.edtBankIfscCode?.text.toString().trim()
      if (ifsc.length == 11) apiGetIfscDetail(ifsc, true)
    }
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.btnSubmitDetails -> if (isValidAndGenerateRequest()) bottomSheetConfirm()
      binding?.btnRetakePanImage -> baseActivity.onNavPressed()
      binding?.btnBankStatementPicker -> openImagePicker(false)
      binding?.btnAdditionalDocs -> openImagePicker(true)
      binding?.btnClearBankStatementImage -> {
        binding?.bankStatementView?.gone()
        bankStatementImage = null
      }
      binding?.btnAnotherAccount -> bankAssociated(false)
      binding?.btnMyAccount -> bankAssociated(true)
    }
  }

  private fun saveApiBankDetail() {
    showProgress(resources.getString(R.string.uploading_file_wait))
    val filePancard = takeIf { panCarImage?.name.isNullOrEmpty().not() }?.let { panCarImage?.name } ?: "pan_card_${Date()}.jpg"
    val mimType = panCarImage?.getMimeType() ?: "multipart/form-data"
    val requestBody = RequestBody.create(MediaType.parse(mimType), panCarImage)
    val bodyPanCard = MultipartBody.Part.createFormData("file", filePancard, requestBody)
    viewModel?.putUploadFile(session?.auth_2, bodyPanCard, filePancard)?.observeOnce(viewLifecycleOwner, Observer {
      if ((it.error is NoNetworkException).not()) {
        if (it.status == 200 || it.status == 201 || it.status == 202) {
          request?.actionData?.panCardDocument = getResponse(it.responseBody)
          uploadBankStatement()
        } else showError(resources.getString(R.string.failed_to_upload_pan_card))
      } else showError(resources.getString(R.string.internet_connection_not_available))
    })
  }

  private fun uploadBankStatement() {
    showProgress(resources.getString(R.string.uploading_file_wait))
    val fileStatement = takeIf { bankStatementImage?.name.isNullOrEmpty().not() }?.let { bankStatementImage?.name } ?: "bank_statement_${Date()}.jpg"
    val mimType = bankStatementImage?.getMimeType() ?: "multipart/form-data"
    val requestBody = RequestBody.create(MediaType.parse(mimType), bankStatementImage)
    val bodyStatement = MultipartBody.Part.createFormData("file", fileStatement, requestBody)
    viewModel?.putUploadFile(session?.auth_2, bodyStatement, fileStatement)?.observeOnce(viewLifecycleOwner, Observer {
      if ((it.error is NoNetworkException).not()) {
        if (it.status == 200 || it.status == 201 || it.status == 202) {
          hideProgress()
          request?.actionData?.bankAccountStatement = getResponse(it.responseBody)
          uploadAdditionalDocument()
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
        val fileAdditional = takeIf { file?.name.isNullOrEmpty().not() }?.let { file?.name } ?: "additional_doc_${Date()}.jpg"
        val mimType = file?.getMimeType() ?: "multipart/form-data"
        val requestBody = RequestBody.create(MediaType.parse(mimType), file)
        val bodyAdditional = MultipartBody.Part.createFormData("file", fileAdditional, requestBody)
        viewModel?.putUploadFile(session?.auth_2, bodyAdditional, fileAdditional)?.observeOnce(viewLifecycleOwner, Observer {
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
    } else addKycInformation(respFileList)
  }

  private fun addKycInformation(additionFile: ArrayList<String>) {
    showProgress(resources.getString(R.string.please_wait_))
    request?.actionData?.additionalDocument = convertListObjToString(additionFile)
    viewModel?.addKycData(session?.auth_1, request)?.observeOnce(viewLifecycleOwner, Observer {
      hideProgress()
      if ((it.error is NoNetworkException).not()) {
        if (it.status == 200 || it.status == 201 || it.status == 202) {
          val bundle = Bundle()
          bundle.putSerializable(IntentConstant.SESSION_DATA.name, session)
          bundle.putSerializable(IntentConstant.KYC_DETAIL.name, request)
          startFragmentPaymentActivity(FragmentType.KYC_STATUS, bundle, clearTop = true)
        } else showError(resources.getString(R.string.add_kyc_error))
      } else showError(resources.getString(R.string.internet_connection_not_available))
    })
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
      panCarImage == null -> {
        showShortToast("Please select valid pan card file")
        return false
      }
      bankStatementImage == null -> {
        showShortToast("Please select valid bank statement file")
        return false
      }
      panNumber.isNullOrEmpty() -> {
        showShortToast("Pan number can't empty.")
        return false
      }
      panName.isNullOrEmpty() -> {
        showShortToast("Pan name can't empty.")
        return false
      }
      this.isBankAssociated.not() -> {
        if (accountNumber.isNullOrEmpty()) {
          showShortToast("Bank account number can't empty.")
          return false
        } else if (nameAccount.isNullOrEmpty()) {
          showShortToast("Bank account name can't empty.")
          return false
        } else if (ifsc.isNullOrEmpty()) {
          showShortToast("Bank IFSC can't empty.")
          return false
        } else if (ifsc.length < 11 || !isValidIfsc) {
          showLongToast("Please enter valid IFSC code")
          return false
        } else if (bankName.isNullOrEmpty()) {
          showShortToast("Bank name can't empty.")
          return false
        }
      }
    }
    request = if (this.isBankAssociated.not()) {
      val action = ActionDataKyc(panNumber = panNumber, nameOfPanHolder = panName, bankAccountNumber = accountNumber,
          nameOfBankAccountHolder = nameAccount, nameOfBank = bankName, ifsc = ifsc, bankBranchName = bankBranch,
          hasexisistinginstamojoaccount = "true", instamojoEmail = "", instamojoPassword = "", fpTag = session?.fpTag, isVerified = "no")
      PaymentKycRequest(actionData = action, websiteId = session?.fpId)
    } else {
      val action = ActionDataKyc(panNumber = panNumber, nameOfPanHolder = panName, bankAccountNumber = bankDetail?.accountNumber,
          nameOfBankAccountHolder = bankDetail?.accountName, nameOfBank = bankDetail?.bankName, ifsc = bankDetail?.iFSC,
          bankBranchName = bankDetail?.bankBranch, hasexisistinginstamojoaccount = "true", instamojoEmail = "", instamojoPassword = "", fpTag = session?.fpTag, isVerified = "no")
      PaymentKycRequest(actionData = action, websiteId = session?.fpId)
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
    filterSheet.onClicked = { openImagePicker(it, allowMultiple) }
    filterSheet.show(this@KYCDetailsFragment.parentFragmentManager, ImagePickerBottomSheet::class.java.name)
  }

  private fun openImagePicker(it: ClickType, allowMultiple: Boolean) {
    imagePickerMultiple = allowMultiple
    if (it != ClickType.PGF) {
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
        adapterImage = AppBaseRecyclerViewAdapter(baseActivity, additionalDocs, this@KYCDetailsFragment)
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
      val result = baseActivity.checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
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
    viewModel?.userAccountDetails(session?.fpId, session?.clientId)?.observeOnce(viewLifecycleOwner, Observer {
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

  private fun updateUiBankAssociated(bankDetail: BankAccountDetails?, detail: String, isBankAssociated: Boolean) {
    hideProgress()
    this.isBankAssociated = isBankAssociated
    this.bankDetail = bankDetail
    if (this.isBankAssociated) {
      binding?.account?.text = "A/C No. ${bankDetail?.accountNumber}"
      binding?.bankDetail?.text = detail
    }
    bankAssociated(this.isBankAssociated)
    binding?.btnMyAccount?.visibility = if (this.isBankAssociated) View.VISIBLE else View.GONE
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
    sheet.setData(request, panCarImage, bankStatementImage, additionalDocs)
    sheet.onClicked = { saveApiBankDetail() }
    sheet.show(this@KYCDetailsFragment.parentFragmentManager, ConfirmKycBottomSheet::class.java.name)
  }
}