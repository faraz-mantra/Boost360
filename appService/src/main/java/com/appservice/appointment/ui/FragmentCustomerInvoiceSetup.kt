package com.appservice.appointment.ui

import android.content.Intent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.appservice.R
import com.appservice.appointment.model.PaymentProfileResponse
import com.appservice.appointment.widgets.BottomSheetEnterGSTDetails
import com.appservice.appointment.widgets.BottomSheetTaxInvoicesForPurchases
import com.appservice.base.AppBaseFragment
import com.appservice.databinding.FragmentCustomerInvoiceSetupBinding
import com.appservice.model.FileModel
import com.appservice.rest.TaskCode
import com.appservice.staffs.ui.UserSession
import com.appservice.ui.catalog.widgets.ClickType
import com.appservice.ui.catalog.widgets.ImagePickerBottomSheet
import com.appservice.viewmodel.AppointmentSettingsViewModel
import com.framework.base.BaseResponse
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.framework.imagepicker.ImagePicker

class FragmentCustomerInvoiceSetup : AppBaseFragment<FragmentCustomerInvoiceSetupBinding, AppointmentSettingsViewModel>() {
    override fun getLayout(): Int {
        return R.layout.fragment_customer_invoice_setup
    }

    var setGstData: (gstin: String) -> Unit = {}
    var setBusinessName: (businessName: String) -> Unit = {}
    var secondaryImages: (secondaryImage: ArrayList<FileModel>) -> Unit = { }

    private lateinit var bottomSheetTaxInvoicesForPurchases: BottomSheetTaxInvoicesForPurchases
    var secondaryImage: ArrayList<FileModel> = ArrayList()


    override fun getViewModelClass(): Class<AppointmentSettingsViewModel> {
        return AppointmentSettingsViewModel::class.java
    }

    companion object {
        fun newInstance(): FragmentCustomerInvoiceSetup {
            return FragmentCustomerInvoiceSetup()
        }
    }

    override fun onCreateView() {
        super.onCreateView()
        setOnClickListener(binding?.gstinContainer, binding?.editPurchases)
        hitApi(viewModel?.getPaymentProfileDetails(UserSession.fpId, UserSession.clientId), (R.string.error_getting_payment_details))
    }

    override fun onSuccess(it: BaseResponse) {
        super.onSuccess(it)
        when (it.taskcode) {
            TaskCode.GET_PAYMENT_PROFILE_DETAILS.ordinal -> {
                val data = it as PaymentProfileResponse
                val gSTIN = data.result?.taxDetails?.gSTDetails?.gSTIN
                val businessName = data.result?.taxDetails?.gSTDetails?.businessName
                setGstData(gSTIN!!)
                setBusinessName(businessName!!)
                data.result.taxDetails.gSTDetails.documentContent

            }
        }
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when (v) {
            binding?.gstinContainer -> {
                showEnterBusinessGSTIN()
            }
            binding?.editPurchases -> {
                showTaxInvoicesForPurchases()
            }
        }
    }

    private fun showTaxInvoicesForPurchases() {
        this.bottomSheetTaxInvoicesForPurchases = BottomSheetTaxInvoicesForPurchases()
        bottomSheetTaxInvoicesForPurchases.upiId = { binding?.upiId?.text = it.toString() }
        bottomSheetTaxInvoicesForPurchases.show(childFragmentManager, BottomSheetTaxInvoicesForPurchases::class.java.name)
    }

    private fun showEnterBusinessGSTIN() {
        val bottomSheetEnterGSTDetails = BottomSheetEnterGSTDetails()
        bottomSheetEnterGSTDetails.businessName = {
            binding?.ctvCompanyName?.text = it
            binding?.ctvCompanyName?.visible()
            binding?.ctvCompanyNameHeading?.visible()
            binding?.hintEnterGst?.gone()
        }
        bottomSheetEnterGSTDetails.gstIn = { gst ->
            binding?.ctvGstNum?.text = gst
            binding?.ctvCompanyName?.gone()
            binding?.ctvCompanyNameHeading?.gone()
            binding?.hintEnterGst?.visible()
        }
        bottomSheetEnterGSTDetails.show(childFragmentManager, BottomSheetEnterGSTDetails::class.java.name)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ImagePicker.IMAGE_PICKER_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK) {
            val mPaths = data?.getSerializableExtra(ImagePicker.EXTRA_IMAGE_PATH) as ArrayList<String>
            secondaryImage(mPaths)
        }
    }

    fun openImagePicker() {
        val filterSheet = ImagePickerBottomSheet()
        filterSheet.isHidePdf(true)
        filterSheet.onClicked = { openImagePicker(it) }
        filterSheet.show(parentFragmentManager, ImagePickerBottomSheet::class.java.name)
    }

    private fun openImagePicker(it: ClickType) {
        val type = if (it == ClickType.CAMERA) ImagePicker.Mode.CAMERA else ImagePicker.Mode.GALLERY
        ImagePicker.Builder(requireActivity())
                .mode(type)
                .compressLevel(ImagePicker.ComperesLevel.SOFT).directory(ImagePicker.Directory.DEFAULT)
                .extension(ImagePicker.Extension.PNG).allowMultipleImages(true)
                .scale(800, 800)
                .enableDebuggingMode(true).build()
    }


    private fun secondaryImage(mPaths: ArrayList<String>) {
        if (secondaryImage.size < 1) {
            if (mPaths.size + secondaryImage.size > 1) showLongToast(resources.getString(R.string.only_eight_files_are_allowed_discarding))
            var index: Int = secondaryImage.size
            while (index < 1 && mPaths.isNotEmpty()) {
                secondaryImage.add(FileModel(path = mPaths[0]))
                mPaths.removeAt(0)
                index++
            }
            secondaryImages(secondaryImage)
        } else showLongToast(getString(R.string.only_one_file_is_allowed))
    }


    fun clearImage() {
        secondaryImage.clear()

    }

}

