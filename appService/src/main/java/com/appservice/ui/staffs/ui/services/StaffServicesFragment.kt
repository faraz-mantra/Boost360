package com.appservice.ui.staffs.ui.services

import android.content.Intent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.constant.IntentConstant
import com.appservice.databinding.FragmentSelectServicesBinding
import com.appservice.recyclerView.AppBaseRecyclerViewAdapter
import com.appservice.recyclerView.BaseRecyclerViewItem
import com.appservice.recyclerView.RecyclerItemClickListener
import com.appservice.model.staffModel.*
import com.appservice.ui.staffs.ui.viewmodel.StaffViewModel
import com.framework.extensions.observeOnce
import com.framework.pref.UserSessionManager
import kotlinx.android.synthetic.main.fragment_kyc_details.*
import java.util.*

class StaffServicesFragment : AppBaseFragment<FragmentSelectServicesBinding, StaffViewModel>(),
  RecyclerItemClickListener {
  private var isEdit: Boolean? = null
  lateinit var data: List<DataItemService?>
  var adapter: AppBaseRecyclerViewAdapter<DataItemService>? = null
  private var listServices: ArrayList<DataItemService>? = null
  private var serviceIds: ArrayList<String>? = null

  companion object {
    fun newInstance(): StaffServicesFragment {
      return StaffServicesFragment()
    }
  }

  override fun getLayout(): Int {
    return R.layout.fragment_select_services
  }

  override fun getViewModelClass(): Class<StaffViewModel> {
    return StaffViewModel::class.java
  }

  override fun onCreateView() {
    sessionLocal = UserSessionManager(requireActivity())
    init()
    getBundleData()

  }

  private fun getBundleData() {
    if (listServices == null) listServices = arrayListOf()
    serviceIds = arguments?.getStringArrayList(IntentConstant.STAFF_SERVICES.name)
    isEdit = serviceIds.isNullOrEmpty().not()
  }


  private fun fetchServices() {
    showProgress(getString(R.string.loading_))
    val request =
      ServiceListRequest(FilterBy("ALL", 0, 0), "", floatingPointTag = sessionLocal.fpTag)
    viewModel?.getServiceListing(request)?.observeOnce(viewLifecycleOwner, {
      hideProgress()
      data = (it as? ServiceListResponse)?.result?.data ?: return@observeOnce
      setServiceCount()
      this.adapter = AppBaseRecyclerViewAdapter(
        activity = baseActivity,
        list = data as ArrayList<DataItemService>,
        itemClickListener = this@StaffServicesFragment
      )
      binding?.rvServiceProvided?.adapter = adapter
      when (isEdit) {
        true -> {
          data.forEach { datum ->
            if (serviceIds?.contains(datum?.id) == true) {
              datum?.isChecked = true
              listServices?.add(datum!!)
            }
          }
        }
      }
      setServiceCount()
      adapter?.notifyDataSetChanged()
    })
  }

  private fun init() {
    fetchServices()
    setOnClickListener(binding?.flConfirmServices)
  }


  override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
    val dataItem = item as DataItemService
    when (dataItem.isChecked) {
      true -> {
        dataItem.isChecked = false
      }
      else -> {
        dataItem.isChecked = true
      }
    }
    when (dataItem.isChecked) {
      true -> listServices?.add(dataItem)
      false -> listServices?.remove(dataItem)
    }
    setServiceCount()

  }

  private fun setServiceCount() {
    val serviceMsg = getString(R.string.service_selected)
    val servicesMsg = getString(R.string.services_selected)
    val serviceButtonText = getString(R.string.service)
    val servicesButtonText = getString(R.string.services)
    var serviceCount = ""
    var serviceConfirm = ""
    when {
      listServices?.size ?: 0 < 2 -> {
        serviceCount = "${listServices?.size}/${data.size} $serviceMsg"
        serviceConfirm = "CONFIRM ${listServices?.size} $serviceButtonText"
      }
      else -> {
        serviceCount = "${listServices?.size}/${data.size} $servicesMsg"
        serviceConfirm = "CONFIRM ${listServices?.size} $servicesButtonText"
      }
    }

    binding?.ctvServicesCountTitle?.text = serviceCount
    binding?.ctvServicesCount?.text = serviceConfirm
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.flConfirmServices -> {
        val intent = Intent()
        intent.putExtra(IntentConstant.STAFF_SERVICES.name, listServices)
        baseActivity.setResult(AppCompatActivity.RESULT_OK, intent)
        baseActivity.finish()
      }
    }
  }
}
