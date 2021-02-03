package com.appservice.staffs.ui.profile

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupMenu
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
import com.appservice.staffs.ui.startStaffFragmentActivity
import com.appservice.staffs.ui.viewmodel.StaffViewModel
import com.appservice.ui.catalog.common.AppointmentModel
import com.framework.extensions.observeOnce
import com.framework.glide.util.glideLoad
import com.framework.views.customViews.CustomTextView


class StaffProfileDetailsFragment() : AppBaseFragment<FragmentStaffProfileBinding, StaffViewModel>(), RecyclerItemClickListener {
    private  var serviceIds: ArrayList<String>? = null
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
                    fetchServices()
                    setTimings()
                }
                else -> {

                }
            }
        })


    }

    private fun setTimings() {
        staffDetails?.timings?.forEach {
            binding?.llTimingContainer?.addView(getTimeView(it))
        }
    }

    private fun getTimeView(appointmentModel: AppointmentModel): View {
        val itemView = LayoutInflater.from(binding?.llTimingContainer?.context).inflate(R.layout.recycler_item_service_timing, null, false);
        val timeTextView = itemView.findViewById(R.id.ctv_timing_services) as CustomTextView
        timeTextView.text = "${appointmentModel.day}${appointmentModel.timeSlots.joinToString(", ").removeSurrounding("[", "]")}"
        return itemView;
    }

    private fun fetchServices() {
        viewModel?.getServiceListing(ServiceListRequest(
                filterBy = FilterBy("ALL", 0, 0), category = "", floatingPointTag = UserSession.fpId))?.observeOnce(viewLifecycleOwner, { response ->
            when (response.status) {
                200 -> {
                    val data = (response as ServiceListResponse).result?.data
                    if (staffDetails?.serviceIds.isNullOrEmpty().not()) {
                        val servicesProvided = data?.filter { item -> staffDetails?.serviceIds!!.contains(item?.id) } as ArrayList<DataItemService>
                        this.serviceIds = data.filter { item -> staffDetails?.serviceIds!!.contains(item?.id) } as ArrayList<String>
                        setServices(servicesProvided.map { it.name })
                    }

                }
                else -> {
                }
            }
        })
    }

    private fun setServices(map: List<String?>) {
        binding?.llServices?.removeAllViews()
        map.forEach {
            binding?.llServices?.addView(getServiceView(it))
        }
    }

    private fun getServiceView(services: String?): View {
        val itemView = LayoutInflater.from(binding?.llTimingContainer?.context).inflate(R.layout.recycler_item_service_timing, null, false);
        val timeTextView = itemView.findViewById(R.id.ctv_timing_services) as CustomTextView
        timeTextView.text = services
        return itemView;
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when (v) {
            binding!!.civMenu -> {
                showPopUp(binding!!.civMenu)
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

    private fun showPopUp(view: View) {
        val popupMenu = PopupMenu(activity, view)
        // inflate the layout of the popup window
        // inflate the layout of the popup window
        val inflater = LayoutInflater.from(baseActivity)
        val popupView = inflater.inflate(R.layout.popup_window, null)
        popupMenu.show()

        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_menu_inactive -> {
                    staffDetails?.isAvailable = false
                    updateStaffProfile()
                }
                R.id.action_menu_remove_staff -> {
                    showProgress(getString(R.string.removing_staff))
                    removeStaffProfile()

                }
            }
            true
        }
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