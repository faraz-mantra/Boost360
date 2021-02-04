package com.appservice.staffs.ui.profile

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.util.Size
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupWindow
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.constant.FragmentType
import com.appservice.constant.IntentConstant
import com.appservice.databinding.FragmentStaffProfileBinding
import com.appservice.recyclerView.BaseRecyclerViewItem
import com.appservice.recyclerView.RecyclerItemClickListener
import com.appservice.staffs.model.*
import com.appservice.staffs.ui.Constants
import com.appservice.staffs.ui.UserSession
import com.appservice.staffs.ui.bottomsheets.InActiveBottomSheet
import com.appservice.staffs.ui.startStaffFragmentActivity
import com.appservice.staffs.ui.viewmodel.StaffViewModel
import com.appservice.ui.catalog.common.AppointmentModel
import com.framework.extensions.observeOnce
import com.framework.glide.util.glideLoad
import com.framework.views.customViews.CustomTextView


class StaffProfileDetailsFragment() : AppBaseFragment<FragmentStaffProfileBinding, StaffViewModel>(), RecyclerItemClickListener {
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
        binding!!.ctvEdit.text = getString(R.string.u_edit_info_u)
        val get = arguments?.get(IntentConstant.STAFF_DATA.name) as DataItem
        showProgress(getString(R.string.loading))
        viewModel?.getStaffDetails(get.id)?.observeOnce(viewLifecycleOwner, {
            when (it.status) {
                200 -> {
                    hideProgress()
                    this.staffDetails = (it as StaffDetailsResponse).result
                    binding?.ctvStaffName?.text = staffDetails?.name
                    binding?.ctvExperience?.text = staffDetails?.experience.toString()
                    binding?.ctvStaffGenderAge?.text = "${staffDetails?.gender}, ${staffDetails?.age}"
                    binding?.ctvAboutHeading?.text = "About ${staffDetails?.name}"
                    binding?.ctvAboutStaff?.text = staffDetails?.description
                    let { activity?.glideLoad(binding?.civStaffProfileImg!!, staffDetails?.image.toString(), R.drawable.placeholder_image) }
                    binding?.ctvSpecialization?.text = staffDetails?.specialisations?.get(0)?.value
                    if (staffDetails?.isAvailable==false) showInactiveProfile()
                    fetchServices()
                    setTimings()
                }
                else -> {

                }
            }
        })


    }

    private fun setTimings() {
        binding?.llTimingContainer?.removeAllViews()
        staffDetails?.timings?.forEach {
            binding?.llTimingContainer?.addView(getTimeView(it))
        }
    }

    private fun getTimeView(appointmentModel: AppointmentModel): View {
        val itemView = LayoutInflater.from(binding?.llTimingContainer?.context).inflate(R.layout.recycler_item_service_timing, null, false);
        val timeTextView = itemView.findViewById(R.id.ctv_timing_services) as CustomTextView
        timeTextView.text = "${appointmentModel.day}  ${appointmentModel.timeSlots.joinToString(" ,").removeSurrounding("(", ")")}"
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
                    setServices(servicesProvided?.map { it.name }?:null)

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
        val timeTextView = itemView.findViewById(R.id.ctv_timing_services) as CustomTextView
        timeTextView.text = services
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
                        showPopupWindow(binding!!.civMenu)
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
        if (this.popupWindow == null) this.popupWindow = PopupWindow(anchor.context)
        this.popupWindow?.isOutsideTouchable = true
        val inflater = LayoutInflater.from(anchor.context)
        this.popupWindow?.contentView = inflater.inflate(R.layout.popup_window, null).apply {
            measure(
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )
        }
        val markAsActive = this.popupWindow?.contentView?.findViewById<CustomTextView>(R.id.mark_as_active)
        val markAsInactive = this.popupWindow?.contentView?.findViewById<CustomTextView>(R.id.mark_as_inactive)
        markAsActive?.setOnClickListener {
            staffDetails?.isAvailable = true
            markActiveInActive()
        }
        markAsInactive?.setOnClickListener {
            staffDetails?.isAvailable = false
            markActiveInActive()


        }
        // Absolute location of the anchor view
        val location = IntArray(2)
        anchor.getLocationOnScreen(location)

        Size(
                popupWindow?.contentView?.measuredWidth!!,
                popupWindow?.contentView?.measuredHeight!!
        )
        popupWindow?.setBackgroundDrawable(resources.getDrawable(R.drawable.pop_window_background))
        this.popupWindow?.showAsDropDown(anchor, -130, 60, Gravity.END)


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


    private fun markActiveInActive() {
        updateStaffProfile()
        popupWindow?.dismiss()
    }


    private fun removeStaffProfile() {
        viewModel?.deleteStaffProfile(StaffDeleteImageProfileRequest(staffDetails?.id, UserSession.fpId))?.observe(viewLifecycleOwner, { response ->
            when (response.status) {
                200 -> {
                    baseActivity.setResult(AppCompatActivity.RESULT_OK)
                    baseActivity.finish()
                    hideProgress()
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

    override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {

    }
}