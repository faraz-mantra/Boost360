package com.boost.presignin.ui.mobileVerification.fp

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import com.boost.presignin.R
import com.boost.presignin.base.AppBaseFragment
import com.boost.presignin.constant.RecyclerViewActionType
import com.boost.presignin.databinding.FragmentFpListBinding
import com.boost.presignin.helper.ProcessFPDetails
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

  private var resultItem: ResultItem? = null
  private lateinit var session: UserSessionManager
  private lateinit var adapter: AppBaseRecyclerViewAdapter<ResultItem>
  private var businessResult: ArrayList<ResultItem>? = null
  private val phoneNumber by lazy { requireArguments().getString(PHONE_NUMBER) }

  override fun getViewModelClass(): Class<LoginSignUpViewModel> {
    return LoginSignUpViewModel::class.java
  }

  override fun onCreateView() {
    super.onCreateView()
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
      if (it.isSuccess()) {
        val fpListResponse = it as? FPListResponse
        if (fpListResponse?.result == null) {
          showLongToast(getString(R.string.something_doesnt_seem_right))
        } else {
          this.businessResult = fpListResponse.result
          this.adapter = AppBaseRecyclerViewAdapter(activity = baseActivity, list = businessResult!!, itemClickListener = this)
          binding?.rvBusinessList?.adapter = adapter
        }
      }
    })

  }

  override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
    this.resultItem = item as? ResultItem
    binding?.btnGoToDashboard?.isEnabled = true
    if (actionType == RecyclerViewActionType.BUSINESS_LIST_ITEM_CLICK.ordinal) {
      resultItem?.isItemSelected = true
    }
    businessResult?.forEach { dataItems ->
      if (dataItems != resultItem) {
        dataItems.isItemSelected = false
      }
    }
    adapter.notifyDataSetChanged()

  }

  private fun storeFpDetails() {
    showProgress()
    session.setUserLogin(true)
    session.setAccountSave(true)
    session.storeFPID(resultItem?.floatingPointId)
    session.storeFpTag(resultItem?.floatingPointTag)
    val map = HashMap<String, String>()
    map["clientId"] = clientId
    viewModel?.getFpDetails(this.resultItem?.floatingPointId ?: "", map)?.observeOnce(viewLifecycleOwner, {
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