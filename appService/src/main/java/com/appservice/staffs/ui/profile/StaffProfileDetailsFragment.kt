package com.appservice.staffs.ui.profile

import android.graphics.Paint
import android.os.Bundle
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
import com.appservice.staffs.ui.UserSession
import com.appservice.staffs.ui.startStaffFragmentActivity
import com.appservice.staffs.ui.viewmodel.StaffViewModel
import com.framework.glide.util.glideLoad

class StaffProfileDetailsFragment : AppBaseFragment<FragmentStaffProfileBinding, StaffViewModel>(), RecyclerItemClickListener {
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

    private fun setData() {
        binding!!.ctvEdit.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        binding!!.ctvEdit.text = getString(R.string.u_edit_info_u)
        val get = arguments?.get("STAFF_DETAILS") as DataItem
        showProgress("Loading")
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
                FilterBy("ALL", 0, 0), "", floatingPointTag = UserSession.fpId))?.observe(viewLifecycleOwner, { response ->
            when (response.status) {
                200 -> {
                    val data = (response as ServiceListResponse).result?.data
                    val servicesProvided = data?.filter { item -> staffDetails?.serviceIds!!.contains(item?.id) } as ArrayList<DataItemService>
                    val services = ArrayList<ServiceTimingModel>()
                    servicesProvided.forEach { itemService -> services.add(ServiceTimingModel(itemService.name!!)) }
                    binding?.rvServices?.adapter = AppBaseRecyclerViewAdapter(baseActivity, services, itemClickListener = this@StaffProfileDetailsFragment)
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
                bundle.putSerializable(IntentConstant.STAFF_DATA.name, staffDetails)
                startStaffFragmentActivity(FragmentType.STAFF_SELECT_SERVICES_FRAGMENT, bundle, false, isResult = false)
            }
            binding!!.ctvEditTiming -> {

            }
        }
    }

    private fun showPopUp(view: View) {
        val popupMenu = PopupMenu(activity, view)
        val inflater = popupMenu.menuInflater
        inflater.inflate(R.menu.menu_staff_status, popupMenu.menu)
        popupMenu.show()

        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_menu_inactive -> {
                    staffDetails?.isAvailable = false
                }
                R.id.action_menu_remove_staff -> {
                    showProgress("Removing Staff")
                    viewModel?.deleteStaffProfile(StaffDeleteImageProfileRequest(staffDetails?.id, UserSession.fpId))?.observe(viewLifecycleOwner, { response ->
                        when (response.status) {
                            200 -> {
                                baseActivity.setResult(AppCompatActivity.RESULT_OK)
                                baseActivity.finish()
                                hideProgress()
                            }
                            else -> {
                                showShortToast("Unable to delete")
                            }
                        }
                    })

                }
            }
            viewModel?.updateStaffProfile(StaffProfileUpdateRequest(isAvailable = false))

            true
        }
    }


    override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {

    }
}