package com.appservice.staffs.ui.profile

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.constant.FragmentType
import com.appservice.constant.IntentConstant
import com.appservice.databinding.FragmentStaffProfileBinding
import com.appservice.recyclerView.AppBaseRecyclerViewAdapter
import com.appservice.recyclerView.BaseRecyclerViewItem
import com.appservice.recyclerView.RecyclerItemClickListener
import com.appservice.staffs.model.*
import com.appservice.staffs.ui.Constants
import com.appservice.staffs.ui.UserSession
import com.appservice.staffs.ui.startStaffFragmentActivity
import com.appservice.staffs.ui.viewmodel.StaffViewModel
import com.framework.extensions.observeOnce
import com.framework.glide.util.glideLoad


class StaffProfileDetailsFragment() : AppBaseFragment<FragmentStaffProfileBinding, StaffViewModel>(), RecyclerItemClickListener {
    private  var serviceIds: ArrayList<String>? = null
    private var staffDetails: StaffDetailsResult? = null
    var serviceAdapter: AppBaseRecyclerViewAdapter<ServiceTimingModel>? = null
    private var servicesList: ArrayList<ServiceTimingModel>? = null
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
                        ?.description, experience = staffDetails?.experience, staffId = staffDetails?.id, age = staffDetails?.age, specialisations = staffDetails?.specialisations))?.observe(viewLifecycleOwner, { t ->
            when (t.status) {
                200 -> {
                    showShortToast(getString(R.string.updated))
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
        viewModel?.getStaffDetails(get.id)?.observe(viewLifecycleOwner, {
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
                    val specialisations = StringBuilder()
                    staffDetails?.specialisations?.forEachIndexed { _, specialisationsItem -> specialisations.append(specialisationsItem?.value) }
                    binding?.ctvSpecialization?.text = specialisations
                    fetchServices()
                    fetchTimings()
                }
                else -> {

                }
            }
        })


    }

    private fun fetchTimings() {

    }

    private fun fetchServices() {
        viewModel?.getServiceListing(ServiceListRequest(
                filterBy = FilterBy("ALL", 0, 0), category = "", floatingPointTag = UserSession.fpId))?.observe(viewLifecycleOwner, { response ->
            when (response.status) {
                200 -> {
                    val data = (response as ServiceListResponse).result?.data
                    if (staffDetails?.serviceIds.isNullOrEmpty().not()) {
                        val servicesProvided = data?.filter { item -> staffDetails?.serviceIds!!.contains(item?.id) } as ArrayList<DataItemService>
                        this.serviceIds = data.filter { item -> staffDetails?.serviceIds!!.contains(item?.id) } as ArrayList<String>
                        val services = ArrayList<ServiceTimingModel>()
                        servicesProvided.forEach { itemService -> services.add(ServiceTimingModel(itemService.name!!)) }
                        this.serviceAdapter = AppBaseRecyclerViewAdapter(baseActivity, services)
                        binding?.rvServices?.adapter = serviceAdapter
                    }

                }
                else -> {
                }
            }
        })
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
                startStaffFragmentActivity(FragmentType.STAFF_DETAILS_FRAGMENT, bundle, false, isResult = false)

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

            }
        }
    }

    private fun showPopUp(view: View) {
        val popupMenu = PopupMenu(activity, view)
        // inflate the layout of the popup window
        // inflate the layout of the popup window
        val inflater = LayoutInflater.from(baseActivity)
        val popupView = inflater.inflate(R.layout.popup_window, null)
//        inflater.inflate(R.layout.popup_window, popupMenu.menu)
        popupMenu.show()

        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_menu_inactive -> {
                    staffDetails?.isAvailable = false
                }
                R.id.action_menu_remove_staff -> {
                    showProgress(getString(R.string.removing_staff))
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
            }
            viewModel?.updateStaffProfile(StaffProfileUpdateRequest(isAvailable = false))

            true
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when {
            requestCode == Constants.REQUEST_CODE_SERVICES_PROVIDED && resultCode == AppCompatActivity.RESULT_OK -> {
                val resultServices = data!!.extras!![IntentConstant.STAFF_SERVICES.name] as ArrayList<DataItemService>
                resultServices.forEach { itemService -> servicesList?.add(ServiceTimingModel(itemService.name!!)) }
                serviceAdapter?.clear()
                servicesList?.let { serviceAdapter?.updateList(it) }
                staffDetails?.serviceIds = resultServices.map { it.id }
                serviceAdapter?.notifyDataSetChanged()
                updateStaffProfile()
            }
            requestCode == Constants.REQUEST_CODE_STAFF_TIMING && resultCode == AppCompatActivity.RESULT_OK -> {
            }
        }

    }

    override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {

    }
}