package com.appservice.staffs.ui.details

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.constant.FragmentType
import com.appservice.databinding.FragmentStaffDetailsBinding
import com.appservice.staffs.model.*
import com.appservice.staffs.ui.Constants
import com.appservice.staffs.ui.home.UserSession
import com.appservice.staffs.ui.startStaffFragmentActivity
import com.appservice.ui.catalog.widgets.ClickType
import com.appservice.ui.catalog.widgets.ImagePickerBottomSheet
import com.framework.imagepicker.ImagePicker
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.fragment_staff_details.*
import kotlinx.android.synthetic.main.item_preview_image.*
import kotlinx.android.synthetic.main.item_preview_image.view.*
import java.io.ByteArrayOutputStream

class StaffDetailsFragment : AppBaseFragment<FragmentStaffDetailsBinding, StaffDetailsViewModel>() {
    private var imageUri: Uri? = null
    private var gender: String? = null
    private var experience: Double = 0.0
    private var isAvailable: Boolean = true
    private var isImageChosen: Boolean = false
    private var allMandatoryFieldFilled = false
    private var servicesList: List<DataItemService>? = null

    companion object {
        fun newInstance(): StaffDetailsFragment {
            return StaffDetailsFragment()
        }
    }

    override fun getLayout(): Int {
        return R.layout.fragment_staff_details
    }

    override fun getViewModelClass(): Class<StaffDetailsViewModel> {
        return StaffDetailsViewModel::class.java
    }

    override fun onCreateView() {
        setOnClickListener(binding?.flAddStaffImg, binding?.rlStaffTiming, binding?.rlServiceProvided,
                binding?.rlScheduledBreaks, binding!!.btnSave)
        initViews()
    }

    private fun initViews() {
        binding!!.toggleYesNo.setOnToggledListener { _, isOn ->
            isAvailable = when (isOn) {
                true -> true
                else -> false
            }
        }
        binding!!.csGender.setHintAdapter(requireContext(), arrayOf("Male", "Female", "Please select"))
        binding!!.csGender.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                gender = p0?.getItemAtPosition(p2).toString()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                gender = null
            }

        }
        // set hint always to last element
        val ageMap = mutableMapOf("<1" to 0.6, "1" to 1.0, "2" to 2.0, "3" to 3.0, "4" to 4.0, "5" to 5.0, "5+" to 5.0, "Select Experience" to 0.0)
        binding!!.csExperience.setHintAdapter(context = requireContext(), list = ageMap.keys.toTypedArray())
        binding!!.csExperience.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                experience = ageMap.values.toMutableList()[p2]
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

        }
    }

    override fun onClick(v: View) {
        super.onClick(v)
        val bundle: Bundle = Bundle.EMPTY
        when (v) {
            binding?.flAddStaffImg -> {
                openImagePicker()
            }
            binding?.rlStaffTiming -> {
                startStaffFragmentActivity(requireActivity(), FragmentType.STAFF_TIMING_FRAGMENT, bundle, clearTop = false, isResult = true, requestCode = Constants.REQUEST_CODE_STAFF_TIMING)
            }
            binding?.rlServiceProvided -> {
                startStaffFragmentActivity(requireActivity(), FragmentType.STAFF_SELECT_SERVICES_FRAGMENT, bundle, clearTop = false, isResult = true, requestCode = Constants.REQUEST_CODE_SERVICES_PROVIDED)
            }
            binding?.rlScheduledBreaks -> {
                startStaffFragmentActivity(requireActivity(), FragmentType.STAFF_SCHEDULED_BREAK_FRAGMENT, bundle, clearTop = false, isResult = true, requestCode = Constants.REQUEST_CODE_SCHEDULED_BREAK)
            }
            binding?.btnSave -> {
                //validation
                var imageToByteArray: ByteArray? = null
                var imageExtension: String? = null
                val specialization = binding?.etvSpecialization?.text.toString()
                var staffName: String? = null
                val serviceListId = ArrayList<String>()
                val staffDescription: String? = null
                val specializationList = ArrayList<SpecialisationsItemStaffRequest>()
                when (binding!!.etvName.text.isNullOrBlank()) {
                    false -> {
                        staffName = binding?.etvName?.text.toString()
                        allMandatoryFieldFilled = true
                    }
                    true -> {
                        showShortToast("please enter name")
                    }
                }
                when (gender.isNullOrBlank() || gender.equals("Please select")) {
                    true -> showShortToast("please choose gender")
                    false -> allMandatoryFieldFilled = true

                }
                when (experience == 0.0) {
                    true -> showShortToast("please choose experience")
                    false -> allMandatoryFieldFilled = true

                }
                when (isImageChosen) {
                    false -> showShortToast("please choose image")
                    true -> {
                        allMandatoryFieldFilled = true
                        imageExtension = imageUri!!.toString().substring(imageUri.toString().lastIndexOf("."))
                        imageToByteArray = imageToByteArray()

                    }
                }
                when (binding!!.etvSpecialization.text.isNullOrBlank()) {
                    true -> showShortToast("please enter specialization")
                    false -> {
                        for ((index, s) in specialization.split(" ").withIndex()) {
                            specializationList.add(SpecialisationsItemStaffRequest(s, "key$index"))
                        }
                        allMandatoryFieldFilled = true
                    }
                }
//                when (servicesList.isNullOrEmpty()) {
//                    true -> {
//                        servicesList?.forEach { serviceListId.add(it.id!!) }
//                    }
//                    false -> {
//                        showShortToast("please choose services")
//                    }
//                }
                when (allMandatoryFieldFilled) {
                    true -> when {
                        imageExtension != null -> when {
                            imageToByteArray != null -> {
                                createStaffProfile(serviceListId, staffDescription, specializationList, imageExtension, staffName, imageToByteArray)
                            }
                        }
                    }
                    else -> {
                        showShortToast("something went wrong")
                    }
                }


            }
        }
    }

    private fun createStaffProfile(serviceListId: ArrayList<String>, staffDescription: String?, specializationList: ArrayList<SpecialisationsItemStaffRequest>, imageExtension: String, staffName: String?, byteArrayImage: ByteArray) {
        val staffCreateProfileRequest = StaffCreateProfileRequest(serviceIds = serviceListId, experience = experience, description = staffDescription,
                specialisations = specializationList, isAvailable = isAvailable, floatingPointTag = UserSession.fpId, image = Image(imageFileType = imageExtension,
                fileName = "$staffName.$imageExtension", image = Base64.encodeToString(byteArrayImage, Base64.DEFAULT)), name = staffName)
        viewModel?.createStaffProfile(staffCreateProfileRequest)?.observe(viewLifecycleOwner, Observer { t ->
            println(t.message + " " + t.status)
        })
    }

    private fun imageToByteArray(): ByteArray {
        val bm: Bitmap = BitmapFactory.decodeFile(imageUri!!.toString())
        val byteArrayOutStream = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutStream) // bm is the bitmap object
        return byteArrayOutStream.toByteArray()
    }

    private fun openImagePicker() {
        val filterSheet = ImagePickerBottomSheet()
        filterSheet.isHidePdf(true)
        filterSheet.onClicked = { openImagePicker(it) }
        filterSheet.show(this@StaffDetailsFragment.parentFragmentManager, ImagePickerBottomSheet::class.java.name)
    }

    private fun openImagePicker(it: ClickType) {
        val type = if (it == ClickType.CAMERA) ImagePicker.Mode.CAMERA else ImagePicker.Mode.GALLERY
        ImagePicker.Builder(baseActivity)
                .mode(type)
                .compressLevel(ImagePicker.ComperesLevel.SOFT).directory(ImagePicker.Directory.DEFAULT)
                .extension(ImagePicker.Extension.PNG).allowMultipleImages(false)
                .enableDebuggingMode(true).build()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when {
            requestCode == ImagePicker.IMAGE_PICKER_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK -> {
                val mPaths = data?.getSerializableExtra(ImagePicker.EXTRA_IMAGE_PATH) as List<String>
                setImage(mPaths)
            }
            requestCode == Constants.REQUEST_CODE_SERVICES_PROVIDED && resultCode == AppCompatActivity.RESULT_OK -> {
                this.servicesList = data!!.extras!![Constants.SERVICES_LIST] as List<DataItemService>
                setServicesList()
            }
            requestCode == Constants.REQUEST_CODE_STAFF_TIMING && resultCode == AppCompatActivity.RESULT_OK -> {
            }
        }

    }

    private fun setServicesList() {
        val services = StringBuilder()
        servicesList?.forEach { dataItem -> services.append("${dataItem.name}, ") }
        binding!!.ctvServices.text = services
    }

    private fun setImage(mPaths: List<String>) {
        isImageChosen = true
        this.imageUri = Uri.parse(mPaths[0])
        binding?.civStaffImg?.setImageURI(imageUri)
        binding?.ctvImgChange?.text = getString(R.string.change_picture)
        binding?.ctvImgChange?.setTextColor(getColor(R.color.black_4a4a4a))
        binding?.ctvImgChange?.setBackgroundColor(Color.WHITE)
        binding?.flAddStaffImg?.setPadding(2, 2, 2, 2);
        binding?.flAddStaffImg?.backgroundTintList = ColorStateList.valueOf(getColor(R.color.gray_light_4))

    }

}

class HintAdapter<T>(context: Context, resource: Int, objects: Array<T>) :
        ArrayAdapter<T>(context, resource, objects) {
    override fun getCount(): Int {
        val count = super.getCount()
        // The last item will be the hint.
        return if (count > 0) count - 1 else count
    }

    override fun isEnabled(position: Int): Boolean {
        return when (position) {
            count -> false
            else -> true
        }
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getDropDownView(position, convertView, parent)
        val textView = view as TextView
        if (position == count+1)
            textView.setTextColor(Color.GRAY)
        else textView.setTextColor(Color.BLACK)
        return view
    }

}

fun Spinner.setHintAdapter(context: Context, list: Array<String>) {
    val hintAdapter =
            HintAdapter(
                    context,
                    android.R.layout.simple_spinner_dropdown_item,
                    list
            )
    adapter = hintAdapter
    setSelection(count)

}