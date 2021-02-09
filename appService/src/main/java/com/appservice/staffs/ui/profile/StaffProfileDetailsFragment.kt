package com.appservice.staffs.ui.profile

import android.content.Intent
import android.graphics.*
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.PopupWindow
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.constant.FragmentType
import com.appservice.constant.IntentConstant
import com.appservice.databinding.FragmentStaffProfileBinding
import com.appservice.staffs.model.*
import com.appservice.staffs.ui.*
import com.appservice.staffs.ui.bottomsheets.InActiveBottomSheet
import com.appservice.staffs.ui.bottomsheets.InActiveStaffConfirmationBottomSheet
import com.appservice.staffs.ui.bottomsheets.RemoveStaffConfirmationBottomSheet
import com.appservice.staffs.ui.viewmodel.StaffViewModel
import com.appservice.ui.catalog.common.AppointmentModel
import com.framework.extensions.observeOnce
import com.framework.glide.util.glideLoad
import com.framework.views.customViews.CustomTextView
import java.lang.StringBuilder


class StaffProfileDetailsFragment() : AppBaseFragment<FragmentStaffProfileBinding, StaffViewModel>() {
    private var popupWindow: PopupWindow? = null
    private var serviceIds: ArrayList<String>? = null
    private var staffDetails: StaffDetailsResult? = null
    override fun getLayout(): Int {
        return R.layout.fragment_staff_profile
    }

    override fun getViewModelClass(): Class<StaffViewModel> {
        return StaffViewModel::class.java
    }

    companion object {
        fun newInstance(): StaffProfileDetailsFragment {
            return StaffProfileDetailsFragment()
        }
    }

    override fun onCreateView() {
        super.onCreateView()
        setOnClickListener(binding!!.civMenu, binding!!.ctvEdit, binding!!.ctvEditLeaves, binding!!.ctvEditServices, binding!!.ctvEditTiming)
        setData()


    }

    private fun updateStaffProfile() {
        viewModel?.updateStaffProfile(
                StaffProfileUpdateRequest(isAvailable = staffDetails?.isAvailable, serviceIds = staffDetails?.serviceIds, gender = staffDetails?.gender, floatingPointTag = UserSession.fpId, name = staffDetails?.name, description = staffDetails
                        ?.description, experience = staffDetails?.experience, staffId = staffDetails?.id, age = staffDetails?.age, specialisations = staffDetails?.specialisations))?.observeOnce(viewLifecycleOwner, { t ->
            when (t.status) {
                200 -> {
                    showShortToast(getString(R.string.updated))
                    setData()
                }
                else -> {
                    showShortToast(getString(R.string.something_went_wrong))
                }
            }


        })

    }

    private fun setData() {
        binding!!.ctvEdit.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        binding!!.ctvEditTiming.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        binding!!.ctvEditServices.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        val get = arguments?.get(IntentConstant.STAFF_DATA.name) as DataItem
        showProgress(getString(R.string.loading))
        viewModel?.getStaffDetails(get.id)?.observeOnce(viewLifecycleOwner, {
            hideProgress()
            when (it.status) {
                200 -> {
                    this.staffDetails = (it as StaffDetailsResponse).result
                    binding?.ctvStaffName?.text = staffDetails?.name
                    setExperience()
                    binding?.ctvStaffGenderAge?.text = "${staffDetails?.gender}, ${staffDetails?.age}"
                    binding?.ctvAboutHeading?.text = "About ${staffDetails?.name}"
                    binding?.ctvAboutStaff?.text = staffDetails?.description
                    let { activity?.glideLoad(binding?.civStaffProfileImg!!, staffDetails?.image.toString(), R.drawable.placeholder_image) }
                    binding?.ctvSpecialization?.text = staffDetails?.specialisations?.get(0)?.value
                    if (staffDetails?.isAvailable == false) showInactiveProfile()
                    fetchServices()
                    setTimings()
                    setViewBackgrounds()
                }
                else -> {

                }
            }
        })

    }

    private fun setExperience() {
        when {
            staffDetails?.experience!! < 2 -> {
                binding?.ctvExperience?.text = "${staffDetails?.experience} Year"
            }
            else -> {
                binding?.ctvExperience?.text = "${staffDetails?.experience} Years"
            }
        }
    }

    private fun setViewBackgrounds() {
        when (staffDetails?.isAvailable) {
            null, true -> {
                binding?.ctvEdit?.visibility = View.VISIBLE
                binding?.ctvEditServices?.visibility = View.VISIBLE
                binding?.civMenu?.visibility = View.VISIBLE
                binding?.civStaffProfileImg?.clearColorFilter()
                binding?.ctvEditTiming?.visibility = View.VISIBLE
                binding?.ctvHeadingExperience?.setTextColor(resources.getColor(R.color.black))
                binding?.ctvExperience?.setTextColor(resources.getColor(R.color.gray_4e4e4e))
                binding?.ctvAboutStaff?.setTextColor(resources.getColor(R.color.gray_4e4e4e))
                binding?.ctvExperience?.setTextColor(resources.getColor(R.color.gray_4e4e4e))
                binding?.ctvAboutHeading?.setTextColor(resources.getColor(R.color.black))
                binding?.ctvHeadingServices?.setTextColor(resources.getColor(R.color.black))
                binding?.ctvHeadingSpecialization?.setTextColor(resources.getColor(R.color.black))
                binding?.ctvSpecialization?.setTextColor(resources.getColor(R.color.gray_4e4e4e))
                binding?.ctvHeadingTiming?.setTextColor(resources.getColor(R.color.black))
                binding?.rlStaffContainer?.setBackgroundColor(resources.getColor(R.color.yellow_ffb900))
                (requireActivity() as StaffFragmentContainerActivity).getToolbar().setBackgroundColor(resources.getColor(R.color.yellow_ffb900))
                (requireActivity() as StaffFragmentContainerActivity).window.statusBarColor = resources.getColor(R.color.yellow_f5b200)

            }
            else -> {
                binding?.ctvEdit?.visibility = View.INVISIBLE
                binding?.ctvEditServices?.visibility = View.INVISIBLE
                binding?.civMenu?.visibility = View.INVISIBLE
                binding?.ctvEditTiming?.visibility = View.INVISIBLE
                binding?.ctvHeadingExperience?.setTextColor(resources.getColor(R.color.pinkish_grey))
                binding?.ctvExperience?.setTextColor(resources.getColor(R.color.pinkish_grey))
                binding?.ctvAboutStaff?.setTextColor(resources.getColor(R.color.pinkish_grey))
                binding?.ctvExperience?.setTextColor(resources.getColor(R.color.pinkish_grey))
                binding?.ctvAboutHeading?.setTextColor(resources.getColor(R.color.pinkish_grey))
                binding?.ctvHeadingServices?.setTextColor(resources.getColor(R.color.pinkish_grey))
                binding?.ctvHeadingSpecialization?.setTextColor(resources.getColor(R.color.pinkish_grey))
                binding?.ctvSpecialization?.setTextColor(resources.getColor(R.color.pinkish_grey))
                binding?.ctvHeadingTiming?.setTextColor(resources.getColor(R.color.pinkish_grey))
                binding?.civStaffProfileImg?.colorFilter = ColorMatrixColorFilter(ColorMatrix().apply { setSaturation(0f) })
                binding?.rlStaffContainer?.setBackgroundColor(resources.getColor(R.color.pinkish_grey))
                (requireActivity() as StaffFragmentContainerActivity).getToolbar().setBackgroundColor(resources.getColor(R.color.pinkish_grey))
                (requireActivity() as StaffFragmentContainerActivity).window.statusBarColor = Color.parseColor("#ADADAD")
            }
        }
    }

    private fun setTimings() {
        binding?.llTimingContainer?.removeAllViews()
        staffDetails?.timings?.forEach {
            if (it?.timeSlots != null && it?.timeSlots?.isNotEmpty()) {
                binding?.llTimingContainer?.addView(getTimeView(it))
            }
        }
    }

    private fun getTimeView(appointmentModel: AppointmentModel?): View {
        val itemView = LayoutInflater.from(binding?.llTimingContainer?.context).inflate(R.layout.recycler_item_service_timing, null, false);
        val timeTextView = itemView.findViewById(R.id.ctv_timing_services) as CustomTextView
        if (staffDetails?.isAvailable == true) timeTextView.setTextColor(resources.getColor(R.color.gray_4e4e4e)) else timeTextView.setTextColor(resources.getColor(R.color.pinkish_grey))
       // timeTextView?.text = "${appointmentModel?.day}  ${appointmentModel?.timeSlots?.joinToString(" ,")?.removeSurrounding("(", ")")}"

        var str = StringBuilder()
        str.clear()
        when {
            appointmentModel?.day?.equals("monday", true) == true -> {
                str.append("Mon: ")
            }
            appointmentModel?.day?.equals("tuesday", true) == true -> {
                str.append("Tue: ")
            }
            appointmentModel?.day?.equals("wednesday", true) == true -> {
                str.append("Wed: ")
            }
            appointmentModel?.day?.equals("thursday", true) == true -> {
                str.append("Thu: ")
            }
            appointmentModel?.day?.equals("friday", true) == true -> {
                str.append("Fri: ")
            }
            appointmentModel?.day?.equals("saturday", true) == true -> {
                str.append("Sat: ")
            }
            appointmentModel?.day?.equals("sunday", true) == true -> {
                str.append("Sun: ")
            }
        }

        for ((index, item) in appointmentModel?.timeSlots?.withIndex()!!) {
            if (item.from?.isNotEmpty() == true) {
                str.append("${item?.from} to ${item?.to}")

                if (index < appointmentModel?.timeSlots?.size - 1) str.append(", ")
            }
        }

        timeTextView?.text = str

        return itemView;
    }

    private fun fetchServices() {
        var servicesProvided:ArrayList<DataItemService>? = null
        viewModel?.getServiceListing(ServiceListRequest(
                filterBy = FilterBy("ALL", 0, 0), category = "", floatingPointTag = UserSession.fpId))?.observeOnce(viewLifecycleOwner, { response ->
            when (response.status) {
                200 -> {
                    val data = (response as ServiceListResponse).result?.data
                    if (staffDetails?.serviceIds.isNullOrEmpty().not()) {
                        servicesProvided = data?.filter { item -> staffDetails?.serviceIds!!.contains(item?.id) } as ArrayList<DataItemService>
                        this.serviceIds = data.filter { item -> staffDetails?.serviceIds!!.contains(item?.id) } as ArrayList<String>
                    }
                    setServices(servicesProvided?.map { it.name } ?: null)

                }
                else -> {
                }
            }
        })
    }

    private fun setServices(map: List<String?>?) {
        binding?.llServices?.removeAllViews()
        map?.forEach {
            binding?.llServices?.addView(getServiceView(it))
        }
    }

    private fun getServiceView(services: String?): View {
        val itemView = LayoutInflater.from(binding?.llServices?.context).inflate(R.layout.recycler_item_service_timing, null, false);
        val serviceTextView = itemView.findViewById(R.id.ctv_timing_services) as CustomTextView
        if (staffDetails?.isAvailable == true) serviceTextView.setTextColor(resources.getColor(R.color.gray_4e4e4e)) else serviceTextView.setTextColor(resources.getColor(R.color.pinkish_grey))
        serviceTextView.text = services
        return itemView;
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when (v) {
            binding!!.civMenu -> {
                when (this.popupWindow?.isShowing) {
                    true -> {
                        this.popupWindow?.dismiss()
                    }
                    null, false -> {
                        showPopupWindow(v)
                    }
                }
            }
            binding!!.ctvEdit -> {
                val bundle = Bundle()
                bundle.putSerializable(IntentConstant.STAFF_DATA.name, staffDetails)
                startStaffFragmentActivity(
                        requireActivity(),
                        FragmentType.STAFF_DETAILS_FRAGMENT,
                        bundle,
                        false,
                        isResult = true,
                        Constants.STAFF_PROFILE_UPDATED_DATA
                )

            }
            binding!!.ctvEditLeaves -> {

            }
            binding!!.ctvEditServices -> {
                val bundle = Bundle()
                if (staffDetails?.serviceIds.isNullOrEmpty().not()) {
                    bundle.putSerializable(IntentConstant.STAFF_SERVICES.name, staffDetails?.serviceIds as ArrayList<String>)
                }
                startStaffFragmentActivity(requireActivity(), FragmentType.STAFF_SELECT_SERVICES_FRAGMENT, bundle, clearTop = false, isResult = true, requestCode = Constants.REQUEST_CODE_SERVICES_PROVIDED)
            }
            binding!!.ctvEditTiming -> {
                val bundle = Bundle()
                bundle.putSerializable(IntentConstant.STAFF_DATA.name, staffDetails)
                startStaffFragmentActivity(requireActivity(), FragmentType.STAFF_TIMING_FRAGMENT, bundle, clearTop = false, isResult = true, requestCode = Constants.REQUEST_CODE_STAFF_TIMING)

            }
        }
    }

    private fun updateStaffTimings() {
        viewModel?.updateStaffTiming(StaffTimingAddUpdateRequest(staffId = staffDetails?.id, staffDetails?.timings))?.observeOnce(viewLifecycleOwner, Observer {
            when (it.status) {
                200 -> {
                    showShortToast(getString(R.string.staff_timings_updated))
                    setTimings()
                }
                else -> {
                    showShortToast(getString(R.string.staff_timings_unable_to_update))
                }
            }
        })
    }

    private fun showPopupWindow(anchor: View) {
        val view = LayoutInflater.from(baseActivity).inflate(R.layout.popup_window_staff_menu, null)
        this.popupWindow = PopupWindow(view, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, true)
        val markAsActive = this.popupWindow?.contentView?.findViewById<CustomTextView>(R.id.mark_as_active)
        val removeStaff = this.popupWindow?.contentView?.findViewById<CustomTextView>(R.id.remove_staff_profile)
        markAsActive?.setOnClickListener {
            staffDetails?.isAvailable = true
            showInactiveConfirmation()
            popupWindow?.dismiss()
        }
        removeStaff?.setOnClickListener {
            showRemoveStaffConfirmation()
            popupWindow?.dismiss()

        }
        this.popupWindow?.showAsDropDown(anchor, Gravity.NO_GRAVITY, 60, 60)


    }

    private fun showInactiveProfile() {
        val inActiveBottomSheet = InActiveBottomSheet()
        inActiveBottomSheet.onClicked = {
            staffDetails?.isAvailable = true
            updateStaffProfile()
        }
        inActiveBottomSheet.isCancelable = false
        inActiveBottomSheet.show(this@StaffProfileDetailsFragment.parentFragmentManager, InActiveBottomSheet::class.java.name)
    }

    private fun showInactiveConfirmation() {
        val inActiveStaffConfirmationBottomSheet = InActiveStaffConfirmationBottomSheet()
        inActiveStaffConfirmationBottomSheet.onClicked = {
            staffDetails?.isAvailable = false
            updateStaffProfile()
        }
        inActiveStaffConfirmationBottomSheet.show(this@StaffProfileDetailsFragment.parentFragmentManager, InActiveStaffConfirmationBottomSheet::class.java.name)
    }

    private fun showRemoveStaffConfirmation() {
        val removeStaffConfirmationBottomSheet = RemoveStaffConfirmationBottomSheet()
        removeStaffConfirmationBottomSheet.onClicked = {
            removeStaffProfile()
        }
        removeStaffConfirmationBottomSheet.show(this@StaffProfileDetailsFragment.parentFragmentManager, RemoveStaffConfirmationBottomSheet::class.java.name)
    }


    private fun removeStaffProfile() {
        viewModel?.deleteStaffProfile(StaffDeleteImageProfileRequest(staffDetails?.id, UserSession.fpId))?.observe(viewLifecycleOwner, { response ->
            hideProgress()
            when (response.status) {
                200 -> {
                    baseActivity.setResult(AppCompatActivity.RESULT_OK)
                    baseActivity.finish()

                }
                else -> {
                    showShortToast(getString(R.string.unable_to_delete))
                }
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when {
            requestCode == Constants.REQUEST_CODE_SERVICES_PROVIDED && resultCode == AppCompatActivity.RESULT_OK -> {
                val resultServices = data!!.extras!![IntentConstant.STAFF_SERVICES.name] as ArrayList<DataItemService>
                staffDetails?.serviceIds = resultServices.map { it.id }
                if (staffDetails?.serviceIds.isNullOrEmpty()) staffDetails?.serviceIds =null
                updateStaffProfile()
            }
            requestCode == Constants.REQUEST_CODE_STAFF_TIMING && resultCode == AppCompatActivity.RESULT_OK -> {
                val result = data!!.extras!![IntentConstant.STAFF_TIMINGS.name] as StaffDetailsResult
                staffDetails?.timings = result.timings
                updateStaffTimings()
            }
            requestCode == Constants.STAFF_PROFILE_UPDATED_DATA && resultCode == AppCompatActivity.RESULT_OK -> {
                val staffDetailsResult = data!!.extras!![IntentConstant.STAFF_DATA.name] as StaffDetailsResult
                this.staffDetails = staffDetailsResult
                setData()

            }
        }

    }


}