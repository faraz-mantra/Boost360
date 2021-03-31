package com.appservice.offers.details

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.constant.FragmentType
import com.appservice.constant.IntentConstant
import com.appservice.databinding.FragmentAddNewOffersBinding
import com.appservice.model.FileModel
import com.appservice.model.servicev1.ServiceDetailResponse
import com.appservice.model.servicev1.ServiceModelV1
import com.appservice.offers.OfferCreatedSuccessFullyBottomSheet
import com.appservice.offers.models.*
import com.appservice.offers.selectservices.OfferSelectServiceBottomSheet
import com.appservice.offers.startOfferFragmentActivity
import com.appservice.offers.viewmodel.OfferViewModel
import com.appservice.rest.TaskCode
import com.appservice.staffs.ui.UserSession
import com.appservice.ui.catalog.catalogService.listing.TypeSuccess
import com.appservice.ui.catalog.widgets.ClickType
import com.appservice.ui.catalog.widgets.ImagePickerBottomSheet
import com.appservice.utils.getBitmap
import com.framework.base.BaseResponse
import com.framework.exceptions.NoNetworkException
import com.framework.extensions.gone
import com.framework.extensions.invisible
import com.framework.extensions.observeOnce
import com.framework.extensions.visible
import com.framework.glide.util.glideLoad
import com.framework.imagepicker.ImagePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.File

class AddNewOfferFragment : AppBaseFragment<FragmentAddNewOffersBinding, OfferViewModel>() {
    private var isRefresh: Boolean = false
    private var menuDelete: MenuItem? = null
    private var offerImage: File? = null
    private var offerModel: OfferModel? = null
    private var currencyType: String? = "INR"
    private var fpId: String? = null
    private var fpTag: String? = null
    private var serviceDetails: ServiceModelV1? = null
    private var clientId: String? = null
    private var applicationId: String? = null
    private var isEdit: Boolean = false
    private var offerIdCreate: String? = null
    private var secondaryImage: ArrayList<FileModel> = ArrayList()

    override fun getLayout(): Int {
        return R.layout.fragment_add_new_offers
    }

    override fun getViewModelClass(): Class<OfferViewModel> {
        return OfferViewModel::class.java
    }

    override fun onCreateView() {
        super.onCreateView()
        setOnClickListener(binding?.btnOtherInfo, binding?.clearImage, binding?.imageAddBtn, binding?.ctvChange, binding?.btnChangePicture, binding?.cbAddOffer, binding?.btnSelectServices)
        getBundleData()
        binding?.toggleServiceApplicableTo?.isOn = true
        binding?.toggleOfferAvailability?.isOn = true
        binding?.toggleServiceApplicableTo?.setOnToggledListener { _, isOn ->
            when (isOn) {
                true -> binding?.llSelectTheService?.gone()
                false -> binding?.llSelectTheService?.visible()
            }
        }
    }

    private fun getBundleData() {
        initOfferFromBundle(arguments)
        currencyType = arguments?.getString(IntentConstant.CURRENCY_TYPE.name) ?: "INR"
        fpId = arguments?.getString(IntentConstant.FP_ID.name) ?: UserSession.fpId
        fpTag = arguments?.getString(IntentConstant.FP_TAG.name) ?: UserSession.fpTag
        clientId = arguments?.getString(IntentConstant.CLIENT_ID.name) ?: UserSession.clientId
        applicationId = arguments?.getString(IntentConstant.APPLICATION_ID.name)
        if (isEdit) menuDelete?.isVisible = true
    }

    private fun initOfferFromBundle(data: Bundle?) {
        val offer = data?.getSerializable(IntentConstant.OFFER_DATA.name) as? OfferModel
        isEdit = (offer != null && offer.offerId.isNullOrEmpty().not())
        if (isEdit) getOfferDetailObject(offer?.offerId) else this.offerModel = OfferModel()
    }

    private fun getOfferDetailObject(offerId: String?) {
        hitApi(viewModel?.getOfferDetails(OfferDetailsRequest(offerId)), R.string.error_getting_offer_details)

    }

    private fun createOfferAPI() {
        hitApi(viewModel?.createOffer(offerModel), R.string.offer_adding_error)
    }

    private fun onOfferCreated(it: BaseResponse) {
        hideProgress()
        val res = it as? OfferBaseResponse
        val offerId = res?.Result
        if (offerId.isNullOrEmpty().not()) {
            offerModel?.offerId = res?.Result
            offerIdCreate = offerId
            uploadPrimaryImage()
        } else showError(resources.getString(R.string.offer_adding_error))
    }

    private fun onPrimaryImageUploaded(it: BaseResponse) {
        hideProgress()
        uploadSecondaryImages()
    }

    override fun onSuccess(it: BaseResponse) {
        when (it.taskcode) {
            TaskCode.CREATE_OFFER.ordinal -> onOfferCreated(it)
            TaskCode.UPDATE_OFFER.ordinal -> onOfferUpdated(it)
            TaskCode.ADD_OFFER_IMAGE.ordinal -> onPrimaryImageUploaded(it)
            TaskCode.OFFER_DETAILS.ordinal -> onOfferDetailsResponseReceived(it)
            TaskCode.DELETE_OFFER.ordinal -> onDeleteOffer(it)
            TaskCode.GET_SERVICE_DETAILS.ordinal -> onServiceDetailResponseReceived(it)

        }
    }

    private fun onServiceDetailResponseReceived(it: BaseResponse) {
        this.serviceDetails = (it as? ServiceDetailResponse)?.Result ?: return
        with(binding!!) {
            btnSelectServices.gone()
            llSelectTheService.visible()
            ctvChange.invisible()
            llServicesSelectMessage.gone()
            val layoutParams = llSelectedServiceView.layoutParams as LinearLayoutCompat.LayoutParams
            layoutParams.setMargins(10,0,10,10)
            llSelectedServiceView.layoutParams = layoutParams
            toggleServiceApplicableTo.isOn = false
            llSelectedServiceView.visible()
            ctvServiceName.text = serviceDetails?.Name
            ctvPriceTime.text = "₹ ${serviceDetails?.Price} for ${serviceDetails?.Duration} min"
        }
        offerModel?.category = serviceDetails?.category
    }


    private fun onDeleteOffer(it: BaseResponse) {
        showLongToast(getString(R.string.offer_removed_successfully))
        val data = Intent()
        data.putExtra(IntentConstant.IS_UPDATED.name, true)
        appBaseActivity?.setResult(Activity.RESULT_OK, data)
        appBaseActivity?.finish()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.ic_menu_delete_new, menu)
        menuDelete = menu.findItem(R.id.id_delete)
        menuDelete?.isVisible = isEdit
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.id_delete -> {
                MaterialAlertDialogBuilder(baseActivity, R.style.MaterialAlertDialogTheme).setTitle(resources.getString(R.string.are_you_sure))
                        .setMessage(resources.getString(R.string.delete_record_not_undone))
                        .setNegativeButton(resources.getString(R.string.cancel)) { d, _ -> d.dismiss() }.setPositiveButton(resources.getString(R.string.delete_)) { d, _ ->
                            d.dismiss()
                            showProgress()
                            val request = DeleteOfferRequest( offerModel?.offerId,clientId)
                            hitApi(viewModel?.deleteOffer(request), R.string.removing_offer_failed)
                        }.show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun isValid(): Boolean {
        val offerTitle = binding?.ctvOfferTitle?.text.toString()
        val offerDescription = binding?.ctvOffersDescription?.text.toString()
        val discount = binding?.ctvDiscountAmount?.text.toString().toDoubleOrNull()
        val toggleOffer = binding?.toggleOfferAvailability?.isOn

        if (offerImage == null && offerModel?.featuredImage?.imageId.isNullOrEmpty()) {
            showLongToast(getString(R.string.add_offer_image))
            return false
        } else if (offerTitle.isEmpty()) {
            showLongToast(getString(R.string.add_offer_title))
            return false
        } else if (discount == null) {
            showLongToast(getString(R.string.please_enter_discount_amount))
            return false
        } else if (discount < 0) {
            showLongToast(getString(R.string.please_enter_positive_discount_value))
            return false
        } else if (toggleOffer == null) {
            showLongToast(getString(R.string.please_mark_offer_availibility))
            return false
        }
        offerModel?.ClientId = clientId
        offerModel?.FPTag = fpTag
        offerModel?.currencyCode = currencyType
        offerModel?.name = offerTitle
        offerModel?.description = offerDescription
        offerModel?.isAvailable = toggleOffer
        offerModel?.discountAmount = discount
        offerModel?.offerType = OfferType.SERVICE
        if (!isEdit) {
            offerModel?.category = offerModel?.category ?: ""
            offerModel?.tags = offerModel?.tags ?: ArrayList()
            offerModel?.otherSpecifications = offerModel?.otherSpecifications ?: ArrayList()
            offerModel?.isAvailable = toggleOffer
        }

        return true
    }

    private fun onOfferDetailsResponseReceived(it: BaseResponse) {
        this.offerModel = (it as? OfferDetailsResponse)?.result ?: return
        updateUiPreviousData()
    }

    private fun onOfferUpdated(it: BaseResponse) {
        hideProgress()
        uploadPrimaryImage()
    }

    private fun uploadPrimaryImage() {
        if (offerImage != null) {
            showProgress(getString(R.string.image_uploading))
            val request = AddImageOffer.getInstance(clientId, 0, offerModel?.offerId!!, offerImage!!)
            hitApi(viewModel?.addOfferImages(request), R.string.error_offer_image)
        } else uploadSecondaryImages()
    }


    private fun uploadSecondaryImages() {
        val images = secondaryImage.filter { it.path.isNullOrEmpty().not() }
        if (images.isNullOrEmpty().not()) {
            showProgress(getString(R.string.image_uploading))
            var checkPosition = 0
            images.forEach { fileData ->
                val request = AddImageOffer.getInstance(clientId, 1, offerModel?.offerId!!, fileData.getFile()!!)
                viewModel?.addOfferImages(request)?.observeOnce(viewLifecycleOwner, {
                    checkPosition += 1
                    if ((it.error is NoNetworkException).not()) {
                        if (it.isSuccess().not()) showError(getString(R.string.secondary_offer_Image_uploading_error))
                    } else showError(resources.getString(R.string.internet_connection_not_available))
                    if (checkPosition == images.size) openSuccessBottomSheet()
                })
            }
        } else {
            openSuccessBottomSheet()
        }
    }

    private fun createUpdateApi() {
        showProgress()
        if (offerModel?.offerId == null) {
            createOfferAPI()
        } else {
            hitApi(viewModel?.updateOffer(offerModel), R.string.offer_updating_error)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ImagePicker.IMAGE_PICKER_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK) {
            val mPaths = data?.getSerializableExtra(ImagePicker.EXTRA_IMAGE_PATH) as List<String>
            if (mPaths.isNotEmpty()) {
                offerImage = File(mPaths[0])
                binding?.imageAddBtn?.gone()
                binding?.btnChangePicture?.visible()
                binding?.clearImage?.visible()
                binding?.offerImageView?.visible()
                offerImage?.getBitmap()?.let { binding?.offerImageView?.setImageBitmap(it) }
            }
        } else if (resultCode == AppCompatActivity.RESULT_OK && requestCode == 101) {
            this.offerModel = data?.getSerializableExtra(IntentConstant.OFFER_DATA.name) as? OfferModel
            this.secondaryImage = (data?.getSerializableExtra(IntentConstant.OFFER_SECONDARY_IMAGE.name) as? ArrayList<FileModel>)
                    ?: ArrayList()
        }
    }

    private fun openSelectServiceBottomSheet() {
        val selectedService = OfferSelectServiceBottomSheet()
        selectedService.setData(isEdit)
        selectedService.onClicked = {
            with(binding!!) {
                btnSelectServices.gone()
                llSelectedServiceView.visible()
                ctvServiceName.text = it?.name
                ctvPriceTime.text = "₹ ${it?.discountedPrice} for ${it?.duration} min"
                offerModel?.referenceId = it?.id
            }
        }
        selectedService.show(this@AddNewOfferFragment.parentFragmentManager, OfferSelectServiceBottomSheet::class.java.name)
    }

    private fun openSuccessBottomSheet() {
        hideProgress()
        val createdSuccess = OfferCreatedSuccessFullyBottomSheet()
        createdSuccess.setData(isEdit)
        createdSuccess.onClicked = { clickSuccessCreate(it) }
        createdSuccess.show(this@AddNewOfferFragment.parentFragmentManager, OfferCreatedSuccessFullyBottomSheet::class.java.name)
    }

    private fun clickSuccessCreate(it: String) {
        when (it) {
            TypeSuccess.CLOSE.name -> {
                val data = Intent()
                data.putExtra(IntentConstant.IS_UPDATED.name, true)
                appBaseActivity?.setResult(Activity.RESULT_OK, data)
                appBaseActivity?.finish()
            }
            TypeSuccess.VISIT_WEBSITE.name -> {
            }
        }
    }

    private fun updateUiPreviousData() {
        binding?.ctvOfferTitle?.setText(offerModel?.name)
        binding?.toggleOfferAvailability?.isOn = offerModel?.isAvailable ?: false
        binding?.ctvOffersDescription?.setText(offerModel?.description)
        binding?.ctvDiscountAmount?.isEnabled = false
        binding?.toggleServiceApplicableTo?.isEnabled = false
        binding?.clApplicableToAll?.setBackgroundColor(getColor(R.color.gray_light_3))
        binding?.ctvDiscountAmount?.setBackgroundResource(R.drawable.rounded_stroke_grey_4_solid_gray)
        if (offerModel?.referenceId.isNullOrEmpty() || offerModel?.referenceId?.isBlank() == true) {
            binding?.toggleServiceApplicableTo?.isOn = true
        } else {
            hitApi(viewModel?.getServiceDetails(offerModel?.referenceId), R.string.error_getting_service_details)


        }
        setToolbarTitle(getString(R.string.update_offer))
        binding?.cbAddOffer?.text = getString(R.string.update_offer)
        binding?.ctvDiscountAmount?.setText("${offerModel?.discountAmount ?: 0.0}")
        if (offerModel?.featuredImage?.imageId.isNullOrEmpty().not()) {
            binding?.imageAddBtn?.gone()
            binding?.btnChangePicture?.visible()
            binding?.clearImage?.visible()
            binding?.offerImageView?.visible()
            binding?.offerImageView?.let { activity?.glideLoad(it, offerModel?.featuredImage?.actualImage, R.drawable.placeholder_image) }
        } else {
            binding?.btnChangePicture?.invisible()
        }
    }

    private fun showError(string: String) {
        hideProgress()
        showLongToast(string)
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when (v) {
            binding?.btnOtherInfo -> {
                val bundle = Bundle()
                bundle.putSerializable(IntentConstant.OFFER_DATA.name, offerModel)
                bundle.putSerializable(IntentConstant.OFFER_SECONDARY_IMAGE.name, secondaryImage)
                startOfferFragmentActivity(requireActivity(), FragmentType.OFFER_ADDITIONAL_INFO, bundle, isResult = true, requestCode = 101)
            }
            binding?.imageAddBtn, binding?.btnChangePicture -> openImagePicker()
            binding?.clearImage -> clearImage()
            binding?.cbAddOffer -> if (isValid()) createUpdateApi()
            binding?.ctvChange, binding?.btnSelectServices -> {
                openSelectServiceBottomSheet()
            }

        }
    }

    private fun clearImage() {
        binding?.imageAddBtn?.visible()
        binding?.clearImage?.gone()
        binding?.btnChangePicture?.gone()
        binding?.offerImageView?.gone()
        offerModel?.featuredImage = null
        offerImage = null
    }

    private fun openImagePicker() {
        val filterSheet = ImagePickerBottomSheet()
        filterSheet.isHidePdf(true)
        filterSheet.onClicked = { openImagePicker(it) }
        filterSheet.show(this@AddNewOfferFragment.parentFragmentManager, ImagePickerBottomSheet::class.java.name)
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

    companion object {
        fun newInstance(): AddNewOfferFragment {
            return AddNewOfferFragment()
        }
    }
}