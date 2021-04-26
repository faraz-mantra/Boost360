package com.boost.presignin.ui.mobileVerification.fp

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.Observer
import com.boost.presignin.R
import com.boost.presignin.base.AppBaseFragment
import com.boost.presignin.constant.RecyclerViewActionType
import com.boost.presignin.databinding.FragmentFpListBinding
import com.boost.presignin.helper.ProcessFPDetails
import com.boost.presignin.helper.WebEngageController
import com.boost.presignin.model.fpList.FPListResponse
import com.boost.presignin.model.fpList.ResultItem
import com.boost.presignin.model.fpdetail.UserFpDetailsResponse
import com.boost.presignin.recyclerView.AppBaseRecyclerViewAdapter
import com.boost.presignin.recyclerView.BaseRecyclerViewItem
import com.boost.presignin.recyclerView.RecyclerItemClickListener
import com.boost.presignin.viewmodel.LoginSignUpViewModel
import com.framework.extensions.observeOnce
import com.framework.pref.UserSessionManager
import com.framework.pref.clientId
import com.framework.pref.clientId2
import com.framework.webengageconstant.*
import java.util.*

class FragmentFpList : AppBaseFragment<FragmentFpListBinding, LoginSignUpViewModel>(), RecyclerItemClickListener {

  override fun getLayout(): Int {
    return R.layout.fragment_fp_list
  }

  companion object {
    private const val PHONE_NUMBER = "phone_number"

    @JvmStatic
    fun newInstance(phoneNumber: String) = FragmentFpList().apply {
      arguments = Bundle().apply {
        putString(PHONE_NUMBER, phoneNumber)
      }
    }
  }

  private var result: ResultItem? = null
  private lateinit var session: UserSessionManager
  private lateinit var adapter: AppBaseRecyclerViewAdapter<ResultItem>
  private var businessResult: ArrayList<ResultItem>? = null
  private val phoneNumber by lazy { requireArguments().getString(PHONE_NUMBER) }

  override fun getViewModelClass(): Class<LoginSignUpViewModel> {
    return LoginSignUpViewModel::class.java
  }

  override fun onCreateView() {
    super.onCreateView()
    WebEngageController.trackEvent(CHOOSE_BUSINESS_ACCOUNT, PAGE_VIEW, NO_EVENT_VALUE)
    setOnClickListener(binding?.btnGoToDashboard)
    this.session = UserSessionManager(baseActivity)
    getFpList()
    binding?.backIv?.setOnClickListener { goBack() }
    activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
      override fun handleOnBackPressed() {
        goBack()
      }
    })
  }

  private fun goBack() {
    parentFragmentManager.popBackStackImmediate()
    parentFragmentManager.beginTransaction().remove(this@FragmentFpList).commitAllowingStateLoss()
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.btnGoToDashboard -> storeFpDetails()
    }
  }

  private fun getFpList() {
    showProgress(getString(R.string.loading))
    viewModel?.getFpListByMobile(phoneNumber, clientId2)?.observeOnce(viewLifecycleOwner, Observer {
      hideProgress()
      val fpListResponse = it as? FPListResponse
      if (fpListResponse?.isSuccess() == true && fpListResponse.result.isNullOrEmpty().not()) {
        this.businessResult = fpListResponse.result
        this.adapter = AppBaseRecyclerViewAdapter(activity = baseActivity, list = businessResult!!, itemClickListener = this)
        binding?.rvBusinessList?.adapter = adapter
      } else {
        showLongToast(getString(R.string.unable_to_find_business_account_associated))
        baseActivity.onNavPressed()
      }
    })
  }

  override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
    if (actionType == RecyclerViewActionType.BUSINESS_LIST_ITEM_CLICK.ordinal) {
      this.result = item as? ResultItem
      binding?.btnGoToDashboard?.isEnabled = true
      result?.isItemSelected = true
      businessResult?.forEach { item2 -> if (item2 != result) item2.isItemSelected = false }
      adapter.notifyItemChanged(position)
    }
  }

  private fun storeFpDetails() {
    WebEngageController.trackEvent(CHOOSE_BUSINESS_ACCOUNT, CHOOSE_BUSINESS, result?.floatingPointId ?: "")
    showProgress()
    session.setUserLogin(true)
    session.setAccountSave(true)
    session.storeFPID(result?.floatingPointId)
    session.storeFpTag(result?.floatingPointTag)
    val map = HashMap<String, String>()
    map["clientId"] = clientId
    viewModel?.getFpDetails(this.result?.floatingPointId ?: "", map)?.observeOnce(viewLifecycleOwner, {
      val response = it as? UserFpDetailsResponse
      if (it.isSuccess() && response != null) {
        ProcessFPDetails(session).storeFPDetails(response)
        session.userProfileId = response.accountManagerId
        startDashboard()
      } else {
        hideProgress()
        showShortToast(getString(R.string.error_getting_fp_detail))
      }
    })
  }

  private fun startDashboard() {
    try {
      hideProgress()
      val dashboardIntent = Intent(requireContext(), Class.forName("com.dashboard.controller.DashboardActivity"))
      dashboardIntent.putExtras(requireActivity().intent)
      val bundle = Bundle()
      bundle.putParcelableArrayList("message", ArrayList())
      dashboardIntent.putExtras(bundle)
      dashboardIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
      startActivity(dashboardIntent)
      baseActivity.finish()
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }
}