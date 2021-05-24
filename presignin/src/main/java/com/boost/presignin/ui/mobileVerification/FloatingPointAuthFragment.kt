package com.boost.presignin.ui.mobileVerification

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import com.boost.presignin.R
import com.boost.presignin.base.AppBaseFragment
import com.boost.presignin.constant.RecyclerViewActionType
import com.boost.presignin.databinding.FragmentFpListBinding
import com.boost.presignin.helper.ProcessFPDetails
import com.boost.presignin.helper.WebEngageController
import com.boost.presignin.model.accessToken.AccessTokenRequest
import com.boost.presignin.model.authToken.AccessTokenResponse
import com.boost.presignin.model.authToken.AuthTokenDataItem
import com.boost.presignin.model.fpdetail.UserFpDetailsResponse
import com.boost.presignin.model.login.VerificationRequestResult
import com.boost.presignin.recyclerView.AppBaseRecyclerViewAdapter
import com.boost.presignin.recyclerView.BaseRecyclerViewItem
import com.boost.presignin.recyclerView.RecyclerItemClickListener
import com.boost.presignin.service.APIService
import com.boost.presignin.viewmodel.LoginSignUpViewModel
import com.framework.extensions.observeOnce
import com.framework.pref.UserSessionManager
import com.framework.pref.clientId
import com.framework.pref.clientIdThinksity
import com.framework.pref.saveAccessTokenAuth
import com.framework.webengageconstant.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.system.exitProcess

class FloatingPointAuthFragment : AppBaseFragment<FragmentFpListBinding, LoginSignUpViewModel>(), RecyclerItemClickListener {

  private var exitToast: Toast? = null

  override fun getLayout(): Int {
    return R.layout.fragment_fp_list
  }

  companion object {
    private const val FP_LIST_AUTH = "fp_list_auth"

    @JvmStatic
    fun newInstance(result: VerificationRequestResult? = null) = FloatingPointAuthFragment().apply {
      arguments = Bundle().apply {
        putSerializable(FP_LIST_AUTH, result)
      }
    }
  }

  private var result: AuthTokenDataItem? = null
  private lateinit var session: UserSessionManager
  private lateinit var adapter: AppBaseRecyclerViewAdapter<AuthTokenDataItem>
  private val resultLogin by lazy { requireArguments().getSerializable(FP_LIST_AUTH) as? VerificationRequestResult }

  private val fpListAuth: ArrayList<AuthTokenDataItem>
    get() {
      return resultLogin?.authTokens ?: ArrayList()
    }

  override fun getViewModelClass(): Class<LoginSignUpViewModel> {
    return LoginSignUpViewModel::class.java
  }

  override fun onCreateView() {
    super.onCreateView()
    WebEngageController.trackEvent(PS_BUSINESS_ACCOUNT_PAGE_LOAD, PAGE_VIEW, NO_EVENT_VALUE)
    setOnClickListener(binding?.btnGoToDashboard)
    this.session = UserSessionManager(baseActivity)
    setAdapterFPList()
    binding?.backIv?.setOnClickListener { goBack() }
    activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
      override fun handleOnBackPressed() {
        goBack()
      }
    })
  }

  private fun setAdapterFPList() {
    if (fpListAuth.isNotEmpty()) {
      this.adapter = AppBaseRecyclerViewAdapter(activity = baseActivity, list = fpListAuth!!, itemClickListener = this)
      binding?.rvBusinessList?.adapter = adapter
    } else {
      showLongToast(getString(R.string.unable_to_find_business_account_associated))
    }
  }

  private fun goBack() {
    if (exitToast == null || exitToast?.view == null || exitToast?.view?.windowToken == null) {
      exitToast = Toast.makeText(baseActivity, resources.getString(R.string.press_again_exit), Toast.LENGTH_SHORT)
      exitToast?.show()
    } else {
      exitToast?.cancel()
      exitProcess(0)
    }
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.btnGoToDashboard -> createAccessTokenAuth()
    }
  }

  override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
    if (actionType == RecyclerViewActionType.BUSINESS_LIST_ITEM_CLICK.ordinal) {
      this.result = item as? AuthTokenDataItem
      binding?.btnGoToDashboard?.isEnabled = true
      result?.isItemSelected = true
      fpListAuth.forEach { dataItems ->
        if (dataItems != result) {
          dataItems.isItemSelected = false
        }
      }
      binding?.rvBusinessList?.post { adapter.notifyDataSetChanged() }
    }
  }

  private fun createAccessTokenAuth() {
    showProgress()
    WebEngageController.initiateUserLogin(resultLogin?.loginId)
    WebEngageController.setUserContactAttributes(resultLogin?.profileProperties?.userEmail, resultLogin?.profileProperties?.userMobile, resultLogin?.profileProperties?.userName, resultLogin?.sourceClientId)
    WebEngageController.trackEvent(PS_LOGIN_SUCCESS, LOGIN_SUCCESS, NO_EVENT_VALUE)
    session.userProfileId = resultLogin?.loginId
    session.userProfileEmail = resultLogin?.profileProperties?.userEmail
    session.userProfileName = resultLogin?.profileProperties?.userName
    session.userProfileMobile = resultLogin?.profileProperties?.userMobile
    session.storeISEnterprise(resultLogin?.isEnterprise.toString() + "")
    session.storeIsThinksity((resultLogin?.sourceClientId != null && resultLogin?.sourceClientId == clientIdThinksity).toString() + "")
    session.storeFPID(result?.floatingPointId)
    session.storeFpTag(result?.floatingPointTag)
    session.setUserLogin(true)
    val request = AccessTokenRequest(authToken = result?.authenticationToken, clientId = clientId, fpId = result?.floatingPointId)
    viewModel?.createAccessToken(request)?.observeOnce(viewLifecycleOwner, {
      val result = it as? AccessTokenResponse
      if (it?.isSuccess() == true && result?.result != null) {
        this.session.saveAccessTokenAuth(result.result!!)
        aliInitializeActivity()
      } else {
        hideProgress()
        showLongToast(getString(R.string.access_token_create_error))
      }
    })
  }

  private fun aliInitializeActivity() {
    try {
      val webIntent = Intent(baseActivity, Class.forName("com.nowfloats.helper.ApiReLoadActivity"))
      startActivityForResult(webIntent, 101)
      baseActivity.overridePendingTransition(0, 0)
    } catch (e: ClassNotFoundException) {
      storeFpDetails()
    }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (resultCode == Activity.RESULT_OK && requestCode == 101) {
      storeFpDetails()
    }
  }

  private fun storeFpDetails() {
    WebEngageController.trackEvent(PS_BUSINESS_ACCOUNT_CHOOSE, CHOOSE_BUSINESS, result?.floatingPointId ?: "")
    val map = HashMap<String, String>()
    map["clientId"] = clientId
    viewModel?.getFpDetails(this.result?.floatingPointId ?: "", map)?.observeOnce(viewLifecycleOwner, {
      val response = it as? UserFpDetailsResponse
      if (it.isSuccess() && response != null) {
        ProcessFPDetails(session).storeFPDetails(response)
        startService()
        startDashboard()
      } else {
        hideProgress()
        showShortToast(getString(R.string.error_getting_fp_detail))
      }
    })
  }

  private fun startDashboard() {
    try {
      val dashboardIntent = Intent(baseActivity, Class.forName("com.dashboard.controller.DashboardActivity"))
      dashboardIntent.putExtras(requireActivity().intent)
      val bundle = Bundle()
      bundle.putParcelableArrayList("message", ArrayList())
      dashboardIntent.putExtras(bundle)
      dashboardIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
      startActivity(dashboardIntent)
      baseActivity.finish()
      hideProgress()
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  private fun startService() {
    baseActivity.startService(Intent(baseActivity, APIService::class.java))
  }
}