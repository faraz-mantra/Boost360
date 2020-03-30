package com.onboarding.nowfloats.ui.registration

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.framework.extensions.observeOnce
import com.framework.views.DotProgressBar
import com.onboarding.nowfloats.R
import com.onboarding.nowfloats.databinding.FragmentRegistrationBusinessApiBinding
import com.onboarding.nowfloats.model.ProcessApiSyncModel
import com.onboarding.nowfloats.model.business.BusinessCreateRequest
import com.onboarding.nowfloats.model.channel.request.UpdateChannelAccessTokenRequest
import com.onboarding.nowfloats.recyclerView.AppBaseRecyclerViewAdapter
import com.onboarding.nowfloats.recyclerView.BaseRecyclerViewItem
import com.onboarding.nowfloats.recyclerView.RecyclerItemClickListener
import com.onboarding.nowfloats.viewmodel.business.BusinessCreateViewModel

class RegistrationBusinessApiFragment : BaseRegistrationFragment<FragmentRegistrationBusinessApiBinding>(), RecyclerItemClickListener {

  private var list: ArrayList<ProcessApiSyncModel>? = null
  private var apiProcessAdapter: AppBaseRecyclerViewAdapter<ProcessApiSyncModel>? = null
  private var token_user: String? = null

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
      apiProcessBusinessCreate(it)
    }
  }

  private fun apiProcessBusinessCreate(dotProgressBar: DotProgressBar) {
    val pref: SharedPreferences = baseActivity.getSharedPreferences("nowfloatsPrefs", 0)
    token_user = pref.getString("user_profile_id", "5e7dfd3d5a9ed3000146ca56")
    val request = getBusinessRequest()
    viewModel?.createBusinessOnboarding(request)?.observeOnce(viewLifecycleOwner, Observer {
      showShortToast(it.error?.message)
      apiProcessChannelAccessTokens(dotProgressBar, "")
    })
  }

  private fun apiProcessChannelAccessTokens(dotProgressBar: DotProgressBar, floatingPointId: String) {
    if (requestFloatsModel?.channelAccessTokens.isNullOrEmpty().not() && token_user.isNullOrEmpty().not()) {
      requestFloatsModel?.channelAccessTokens?.forEachIndexed { index, channelAccessToken ->
        viewModel?.updateChannelAccessToken(UpdateChannelAccessTokenRequest(channelAccessToken, token_user!!, floatingPointId))?.observeOnce(viewLifecycleOwner, Observer {
          if (requestFloatsModel?.channelAccessTokens?.size == index + 1) {
            apiBusinessComplete(dotProgressBar)
          }
        })
      }
    } else apiBusinessComplete(dotProgressBar)
  }

  private fun apiBusinessComplete(dotProgressBar: DotProgressBar) {
    binding?.apiRecycler?.post {
      list?.map { it1 ->
        it1.status = ProcessApiSyncModel.SyncStatus.SUCCESS.name
        it1.channels?.map { it2 -> it2.status = ProcessApiSyncModel.SyncStatus.SUCCESS.name; }
        it1
      }
      dotProgressBar.stopAnimation()
      dotProgressBar.removeAllViews()
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

  private fun getBusinessRequest(): BusinessCreateRequest {
    val createRequest = BusinessCreateRequest()
    createRequest.autoFillSampleWebsiteData = true
    createRequest.clientId = token_user
    createRequest.tag = requestFloatsModel?.contactInfo?.domainName
    createRequest.contactName = requestFloatsModel?.contactInfo?.businessName
    createRequest.name = requestFloatsModel?.contactInfo?.businessName
    createRequest.desc = ""
    createRequest.address = requestFloatsModel?.contactInfo?.address
    createRequest.city = ""
    createRequest.pincode = ""
    createRequest.country = ""
    createRequest.primaryNumber = requestFloatsModel?.contactInfo?.number
    createRequest.email = requestFloatsModel?.contactInfo?.email
    createRequest.primaryNumberCountryCode = "+91"
    createRequest.uri = ""
    createRequest.fbPageName = requestFloatsModel?.contactInfo?.businessName
    createRequest.primaryCategory = ""
    return createRequest
  }
}