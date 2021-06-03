package com.boost.presignin.ui.mobileVerification

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import com.boost.presignin.R
import com.boost.presignin.constant.IntentConstant
import com.boost.presignin.constant.RecyclerViewActionType
import com.boost.presignin.databinding.FragmentFpListBinding
import com.boost.presignin.helper.WebEngageController
import com.boost.presignin.model.authToken.AuthTokenDataItem
import com.boost.presignin.model.login.VerificationRequestResult
import com.boost.presignin.recyclerView.AppBaseRecyclerViewAdapter
import com.boost.presignin.recyclerView.BaseRecyclerViewItem
import com.boost.presignin.recyclerView.RecyclerItemClickListener
import com.framework.webengageconstant.NO_EVENT_VALUE
import com.framework.webengageconstant.PAGE_VIEW
import com.framework.webengageconstant.PS_BUSINESS_ACCOUNT_PAGE_LOAD
import kotlin.system.exitProcess

class FloatingPointAuthFragment : AuthBaseFragment<FragmentFpListBinding>(), RecyclerItemClickListener {

  private var exitToast: Toast? = null

  override fun getLayout(): Int {
    return R.layout.fragment_fp_list
  }

  companion object {
    private const val FP_LIST_AUTH = "fp_list_auth"

    @JvmStatic
    fun newInstance(bundle: Bundle?) = FloatingPointAuthFragment().apply {
      arguments = bundle
    }
  }

  private var result: AuthTokenDataItem? = null

  private lateinit var adapter: AppBaseRecyclerViewAdapter<AuthTokenDataItem>

  private val resultLogin1 by lazy {
    arguments?.getSerializable(IntentConstant.EXTRA_FP_LIST_AUTH.name) as? VerificationRequestResult
  }

  private val resultLogin: VerificationRequestResult?
    get() {
      return resultLogin1
    }

  private val fpListAuth: ArrayList<AuthTokenDataItem>
    get() {
      return resultLogin?.authTokens ?: ArrayList()
    }

  override fun resultLogin(): VerificationRequestResult? {
    return resultLogin
  }

  override fun authTokenData(): AuthTokenDataItem? {
    return result
  }

  override fun onCreateView() {
    super.onCreateView()
    WebEngageController.trackEvent(PS_BUSINESS_ACCOUNT_PAGE_LOAD, PAGE_VIEW, NO_EVENT_VALUE)
    setOnClickListener(binding?.btnGoToDashboard)
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
      baseActivity.finish()
    }
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.btnGoToDashboard -> result?.createAccessTokenAuth()
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
}