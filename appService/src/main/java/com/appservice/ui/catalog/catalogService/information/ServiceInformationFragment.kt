package com.appservice.ui.catalog.catalogService.information

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
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
import com.appservice.model.servicev1.DeleteSecondaryImageRequest
import com.appservice.model.servicev1.ImageModel
import com.appservice.model.servicev1.ServiceModelV1
import com.appservice.recyclerView.AppBaseRecyclerViewAdapter
import com.appservice.recyclerView.BaseRecyclerViewItem
import com.appservice.recyclerView.RecyclerItemClickListener
import com.appservice.staffs.model.StaffDetailsResult
import com.appservice.staffs.ui.Constants
import com.appservice.staffs.ui.startStaffFragmentActivity
import com.appservice.ui.catalog.ServiceModel
import com.appservice.ui.catalog.TimeSlot
import com.appservice.ui.catalog.startFragmentActivity
import com.appservice.ui.catalog.startServiceFragmentActivity
import com.appservice.ui.catalog.widgets.ClickType
import com.appservice.ui.catalog.widgets.GstDetailsBottomSheet
import com.appservice.ui.catalog.widgets.ImagePickerBottomSheet
import com.appservice.utils.WebEngageController
import com.appservice.viewmodel.ServiceViewModelV1
import com.framework.extensions.observeOnce
import com.framework.imagepicker.ImagePicker
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.lang.StringBuilder


class ServiceInformationFragment : AppBaseFragment<FragmentServiceInformationBinding, ServiceViewModelV1>(), RecyclerItemClickListener {

    private var product: ServiceModelV1? = null
    private var isEdit: Boolean? = null
    private var tagList = ArrayList<String>()
    private var specList: ArrayList<KeySpecification> = arrayListOf(KeySpecification())
    private var secondaryImage: ArrayList<FileModel> = ArrayList()
    private var adapterSpec: AppBaseRecyclerViewAdapter<KeySpecification>? = null
    private var adapterImage: AppBaseRecyclerViewAdapter<FileModel>? = null
    private var ordersQuantity: Int = 0
    private var serviceModel : ArrayList<ServiceModel> ?= null
    var days = StringBuilder()

    private var secondaryDataImage: ArrayList<ImageModel>? = null

    companion object {
        fun newInstance(): ServiceInformationFragment {
            return ServiceInformationFragment()
        }
    }

    override fun getLayout(): Int {
        return R.layout.fragment_service_information
    }

    override fun getViewModelClass(): Class<ServiceViewModelV1> {
        return ServiceViewModelV1::class.java
    }

    override fun onCreateView() {
        super.onCreateView()
        WebEngageController.trackEvent("Service other information catalogue load", "SERVICE CATALOGUE ADD/UPDATE", "")

        setOnClickListener(binding?.cbFacebookPage, binding?.cbGoogleMerchantCenter, binding?.cbTwitterPage,
                binding?.civIncreaseQuantityOrder, binding?.civDecreseQuantityOrder, binding?.btnAddTag, binding?.btnAddSpecification,
                binding?.btnConfirm, binding?.btnClickPhoto, binding?.edtGst, binding?.weeklyAppointmentSchedule)
        product = arguments?.getSerializable(IntentConstant.PRODUCT_DATA.name) as? ServiceModelV1
        serviceModel = arguments?.getSerializable(IntentConstant.TIMESLOT.name) as ArrayList<ServiceModel>?

        isEdit = (product != null && product?.productId.isNullOrEmpty().not())
        secondaryImage = (arguments?.getSerializable(IntentConstant.NEW_FILE_PRODUCT_IMAGE.name) as? ArrayList<FileModel>)
                ?: ArrayList()
        tagList = product?.tags ?: ArrayList()
        specList = if (product?.otherSpecification.isNullOrEmpty()) arrayListOf(KeySpecification()) else product?.otherSpecification!!
        if (isEdit == true) {
            secondaryDataImage = product?.secondaryImages;
            if (secondaryImage.isNullOrEmpty()) secondaryDataImage?.forEach { secondaryImage.add(FileModel(pathUrl = it.ActualImage)) }
        }
        setUiText()
        serviceTagsSet()
        specificationAdapter()
//    val rangeFilter = InputFilterIntRange(0, 100)
//    binding?.edtGst?.filters = arrayOf<InputFilter>(rangeFilter)
//    binding?.edtGst?.onFocusChangeListener = rangeFilter
    }

    private fun setUiText() {

        if (serviceModel != null) getSelectedDays()
        if (days.isNotEmpty()) {
            binding?.timings?.visibility = View.VISIBLE
            binding?.timings?.text = days
        } else {
            binding?.timings?.visibility = View.GONE
        }

        ordersQuantity = product?.maxCodOrders!!
//    binding?.edtServiceCategory?.setText(product?.category ?: "")
        binding?.cetSpecKey?.setText(product?.keySpecification?.key ?: "")
        binding?.cetSpecValue?.setText(product?.keySpecification?.value ?: "")
        binding?.edtBrand?.setText(product?.brandName ?: "")
        binding?.ctvQuantityOrderStatus?.text = ordersQuantity.toString()
        if (product?.GstSlab != null) binding?.edtGst?.setText("${(product?.GstSlab ?: 0.0)} %")
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
            binding?.weeklyAppointmentSchedule -> {
                val bundle = Bundle()
                bundle.putSerializable(IntentConstant.TIMESLOT.name, serviceModel)
                startServiceFragmentActivity(requireActivity(), FragmentType.WEEKLY_APPOINTMENT_FRAGMENT, isResult = true, bundle = bundle, clearTop = false, requestCode = Constants.REQUEST_CODE_STAFF_TIMING)
            }
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
                if (product?.keySpecification == null) product?.keySpecification = KeySpecification()
                product?.keySpecification?.key = keySpecification
                product?.keySpecification?.value = valSpecification
                product?.maxCodOrders = ordersQuantity
                product?.otherSpecification = otherSpec
                product?.GstSlab = gst.toIntOrNull() ?: 0;
                val output = Intent()
                output.putExtra(IntentConstant.PRODUCT_DATA.name, product)
                output.putExtra(IntentConstant.NEW_FILE_PRODUCT_IMAGE.name, secondaryImage)
                output.putExtra(IntentConstant.TIMESLOT.name, serviceModel)
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

        if (requestCode == Constants.REQUEST_CODE_STAFF_TIMING && resultCode == AppCompatActivity.RESULT_OK) {
            serviceModel = data?.extras?.get(IntentConstant.TIMESLOT.name) as ArrayList<ServiceModel>
            getSelectedDays()

            if (days.isNotEmpty()) {
                binding?.timings?.visibility = View.VISIBLE
                binding?.timings?.text = days
            } else {
                binding?.timings?.visibility = View.GONE
            }
        }
    }

    private fun getSelectedDays() {
        days.clear()
        for (item in serviceModel!!) {
            if (item.isTurnedOn == true) {
                if (days.isNotEmpty()) days.append(", ")

                if (item.day.equals("monday", true)) {
                    days.append("Mon")
                }

                if (item.day.equals("tuesday", true)) {
                    days.append("Tue")
                }

                if (item.day.equals("Wednesday", true)) {
                    days.append("Wed")
                }

                if (item.day.equals("Thursday", true)) {
                    days.append("Thu")
                }

                if (item.day.equals("Friday", true)) {
                    days.append("Fri")
                }

                if (item.day.equals("Saturday", true)) {
                    days.append("Sat")
                }

                if (item.day.equals("Sunday", true)) {
                    days.append("Sun")
                }
            }
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
                    val dataImage = secondaryDataImage?.firstOrNull { it.ActualImage == data?.pathUrl }
                            ?: return
                    showProgress(resources.getString(R.string.removing_image))
                    val request = DeleteSecondaryImageRequest(product?.productId, dataImage.ImageId);
                    viewModel?.deleteSecondaryImage(request)?.observeOnce(viewLifecycleOwner, Observer {
                        if (it.status == 200 || it.status == 201 || it.status == 202) {
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
        gstSheet.show(this@ServiceInformationFragment.parentFragmentManager, ImagePickerBottomSheet::class.java.name)
    }

    fun onNavPressed() {
        dialogLogout()
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

data class SpinnerImageModel(var state: Pair<String, Boolean>, var resId: Int) {

}
