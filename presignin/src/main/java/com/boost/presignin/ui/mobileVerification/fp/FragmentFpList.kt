package com.boost.presignin.ui.mobileVerification.fp

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.boost.presignin.R
import com.boost.presignin.base.AppBaseFragment
import com.boost.presignin.constant.RecyclerViewActionType
import com.boost.presignin.databinding.FragmentFpListBinding
import com.boost.presignin.model.FPListResponse
import com.boost.presignin.model.ResultItem
import com.boost.presignin.model.fpdetail.UserSessionManager
import com.boost.presignin.recyclerView.AppBaseRecyclerViewAdapter
import com.boost.presignin.recyclerView.BaseRecyclerViewItem
import com.boost.presignin.recyclerView.RecyclerItemClickListener
import com.boost.presignin.viewmodel.LoginSignUpViewModel
import com.framework.extensions.observeOnce
import java.util.*

class FragmentFpList : AppBaseFragment<FragmentFpListBinding, LoginSignUpViewModel>(), RecyclerItemClickListener {
    override fun getLayout(): Int {
        return R.layout.fragment_fp_list
    }

    companion object {
        private const val PHONE_NUMBER = "phone_number"

        @JvmStatic
        fun newInstance(phoneNumber: String) =
                FragmentFpList().apply {
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
        this.session= UserSessionManager(requireContext(), requireActivity())
        getFpList()
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when (v) {
            binding?.btnGoToDashboard -> {
                startDashboard()
            }
        }
    }

    private fun getFpList() {
        showProgress(getString(R.string.loading))
        viewModel?.getFpListByMobile(phoneNumber)?.observeOnce(viewLifecycleOwner, Observer {
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
        this. resultItem = item as? ResultItem
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

    private fun startDashboard() {
        session.setUserLogin(true)
        session.storeFPID(resultItem?.floatingPointId)
        session.storeFpTag(resultItem?.floatingPointTag)
        val dashBoardActivity: Class<*> = try {
            Class.forName("com.dashboard.controller.DashboardActivity")
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
            return
        }
        val dashboardIntent = Intent(requireContext(), dashBoardActivity)
        dashboardIntent.putExtras(requireActivity().intent)
        val bundle = Bundle()
        bundle.putParcelableArrayList("message", ArrayList())
        dashboardIntent.putExtras(bundle)
        dashboardIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(dashboardIntent)
        requireActivity().finish()
    }

}