package com.appservice.appointment.widgets

import android.view.View
import com.appservice.R
import com.appservice.appointment.model.PaymentResult
import com.appservice.appointment.ui.FragmentCustomerInvoiceSetup
import com.appservice.constant.IntentConstant
import com.appservice.databinding.BottomSheetSetupTaxInvoicesForCustomerPurchaseBinding
import com.appservice.model.FileModel
import com.framework.base.BaseBottomSheetDialog
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.framework.glide.util.glideLoad
import com.framework.models.BaseViewModel
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

class BottomSheetTaxInvoicesForPurchases : BaseBottomSheetDialog<BottomSheetSetupTaxInvoicesForCustomerPurchaseBinding, BaseViewModel>() {
    override fun getLayout(): Int {
        return R.layout.bottom_sheet_setup_tax_invoices_for_customer_purchase
    }

    var clickType: (name: ClickType?) -> Unit = { }

    enum class ClickType {
        SAVECHANGES, CANCEL
    }

    var isEdit = false
    private var paymentProfileDetails: PaymentResult? = null

    var upiId: (name: String?) -> Unit = { }
    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {
        setOnClickListener(binding?.btnSaveChanges, binding?.btnCancel, binding?.btnClickPhoto)
        val parent = (requireParentFragment() as FragmentCustomerInvoiceSetup)
        this.paymentProfileDetails = arguments?.getSerializable(IntentConstant.PAYMENT_PROFILE_DETAILS.name) as PaymentResult
        if (paymentProfileDetails == null) isEdit = false
        binding?.cetUpiId?.setText(paymentProfileDetails?.uPIId)
        val images = arguments?.getSerializable(IntentConstant.IMAGE_SIGNATURE.name) as ArrayList<FileModel>
        if (images.isNotEmpty()) setImage(images, parent = parent) else setImage(parent)
        if (paymentProfileDetails?.uPIId.isNullOrEmpty().not()) binding?.checkboxUpiId?.isChecked = true
        if (binding?.checkboxUpiId?.isChecked == true) binding?.cetUpiId?.visible() else binding?.cetUpiId?.gone()
        binding?.checkboxUpiId?.setOnCheckedChangeListener { buttonView, isChecked ->
            when (isChecked) {
                true -> binding?.cetUpiId?.visible()
                else -> binding?.cetUpiId?.gone()
            }
        }
        parent.onImageClick = {
            val path = it[0].path
            setImage(it, path, parent)
        }
    }

    fun setImage(parent: FragmentCustomerInvoiceSetup) {
        binding?.btnClickPhoto?.gone()
        binding?.layoutImagePreview?.root?.visible()
        binding?.layoutImagePreview?.ctvSize?.text = paymentProfileDetails?.taxDetails?.gSTDetails?.documentName
        activity?.glideLoad(binding?.layoutImagePreview?.image, paymentProfileDetails?.merchantSignature)
        imageClickListners(parent)

    }

    private fun setImage(it: ArrayList<FileModel>, path: String? = it[0].path, parent: FragmentCustomerInvoiceSetup? = null) {
        binding?.btnClickPhoto?.gone()
        binding?.layoutImagePreview?.root?.visible()
        binding?.layoutImagePreview?.ctvSize?.text = it[0].getFileName()
        activity?.glideLoad(binding?.layoutImagePreview?.image, path)
        imageClickListners(parent)
    }

    private fun imageClickListners(parent: FragmentCustomerInvoiceSetup?) {
        binding?.layoutImagePreview?.crossIcon?.setOnClickListener {
            binding?.layoutImagePreview?.root?.gone()
            binding?.btnClickPhoto?.visible()
            parent?.clearImage()
        }
        binding?.layoutImagePreview?.cbChange?.setOnClickListener {
            parent?.clearImage()
            parent?.openImagePicker()
        }
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when (v) {
            binding?.btnCancel -> {
                dismiss()
                clickType(ClickType.CANCEL)
            }
            binding?.btnSaveChanges -> {
                if (validateUPI(binding?.cetUpiId?.text.toString())) {
                    setDataAndGoBack()

                } else {
                    showLongToast(getString(R.string.please_enter_valid_upi_id))
                }
            }
            binding?.btnClickPhoto -> {
                (requireParentFragment() as FragmentCustomerInvoiceSetup).openImagePicker()
            }
        }
    }

    private fun setDataAndGoBack() {
        val upi = binding?.cetUpiId?.text.toString()
        paymentProfileDetails?.uPIId = upi
        upiId(upi)
        clickType(ClickType.SAVECHANGES)
        dismiss()
    }

    companion object {
        fun validateUPI(upi: String?): Boolean {
            val VALID_EMAIL_ADDRESS_REGEX: Pattern = Pattern.compile("^(.+)@(.+)$", Pattern.CASE_INSENSITIVE)
            val matcher: Matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(upi)
            return matcher.find()
        }
    }
}