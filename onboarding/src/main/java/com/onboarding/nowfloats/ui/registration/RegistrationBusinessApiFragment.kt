package com.onboarding.nowfloats.ui.registration

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.onboarding.nowfloats.R
import com.onboarding.nowfloats.databinding.FragmentRegistrationBusinessApiBinding
import com.onboarding.nowfloats.model.ProcessApiSyncModel
import com.onboarding.nowfloats.model.business.BusinessCreateRequest
import com.onboarding.nowfloats.recyclerView.AppBaseRecyclerViewAdapter
import com.onboarding.nowfloats.recyclerView.BaseRecyclerViewItem
import com.onboarding.nowfloats.recyclerView.RecyclerItemClickListener
import com.onboarding.nowfloats.viewmodel.business.BusinessCreateViewModel
import java.util.*

class RegistrationBusinessApiFragment : BaseRegistrationFragment<FragmentRegistrationBusinessApiBinding, BusinessCreateViewModel>(), RecyclerItemClickListener {

    private var list: ArrayList<ProcessApiSyncModel>? = null
    private var apiProcessAdapter: AppBaseRecyclerViewAdapter<ProcessApiSyncModel>? = null
    private var businessCreateRequest: BusinessCreateRequest? = null

    companion object {
        @JvmStatic
        fun newInstance(bundle: Bundle? = null): RegistrationBusinessApiFragment {
            val fragment = RegistrationBusinessApiFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    @ExperimentalStdlibApi
    override fun onCreateView() {
        super.onCreateView()
        setOnClickListener(binding?.next)
        list = ProcessApiSyncModel().getData(channels)
        setApiProcessAdapter(list)
        getDotProgress()?.let {
            binding?.textBtn?.visibility = View.GONE
            binding?.next?.addView(it)
            it.startAnimation()
            Timer().schedule(object : TimerTask() {
                override fun run() {
                    binding?.apiRecycler?.post {
                        list?.map { it1 ->
                            it1.status = ProcessApiSyncModel.SyncStatus.SUCCESS.name
                            it1.channels?.map { it2 -> it2.status = ProcessApiSyncModel.SyncStatus.SUCCESS.name; }
                            it1
                        }
                        it.stopAnimation()
                        it.removeAllViews()
                        binding?.next?.alpha = 1F
                        binding?.textBtn?.visibility = View.VISIBLE
                        binding?.container?.setBackgroundResource(R.drawable.bg_card_blue)
                        binding?.categoryCard?.setBackgroundColor(getColor(R.color.white))
                        binding?.title?.setTextColor(getColor(R.color.white))
                        binding?.title?.text = resources.getString(R.string.business_information_completed)
                        binding?.categoryImage?.setTintColor(getColor(R.color.dodger_blue_two))
                        apiProcessAdapter?.notify(list)
                    }
                }
            }, 2000)
        }
    }


    private fun setApiProcessAdapter(list: ArrayList<ProcessApiSyncModel>?) {
        list?.let {
            apiProcessAdapter = AppBaseRecyclerViewAdapter(baseActivity, it, this)
            binding?.apiRecycler?.layoutManager = LinearLayoutManager(baseActivity)
            binding?.apiRecycler?.adapter = apiProcessAdapter
            binding?.apiRecycler?.let { it1 -> apiProcessAdapter?.runLayoutAnimation(it1) }
        }
    }

    override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when (v) {
            binding?.next -> if ((binding?.textBtn?.visibility == View.VISIBLE)) {
                gotoRegistrationComplete()
            }
        }
    }

    override fun getViewModelClass(): Class<BusinessCreateViewModel> {
        return BusinessCreateViewModel::class.java
    }
}