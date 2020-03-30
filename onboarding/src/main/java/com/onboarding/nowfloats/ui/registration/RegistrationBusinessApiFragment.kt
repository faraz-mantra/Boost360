package com.onboarding.nowfloats.ui.registration

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.framework.extensions.gone
import com.framework.extensions.observeOnce
import com.framework.extensions.visible
import com.framework.views.DotProgressBar
import com.onboarding.nowfloats.R
import com.onboarding.nowfloats.databinding.FragmentRegistrationBusinessApiBinding
import com.onboarding.nowfloats.model.ProcessApiSyncModel
import com.onboarding.nowfloats.model.business.BusinessCreateRequest
import com.onboarding.nowfloats.model.channel.request.UpdateChannelAccessTokenRequest
import com.onboarding.nowfloats.model.channel.request.UpdateChannelActionDataRequest
import com.onboarding.nowfloats.recyclerView.AppBaseRecyclerViewAdapter
import com.onboarding.nowfloats.recyclerView.BaseRecyclerViewItem
import com.onboarding.nowfloats.recyclerView.RecyclerItemClickListener
import com.onboarding.nowfloats.viewmodel.business.BusinessCreateViewModel

class RegistrationBusinessApiFragment : BaseRegistrationFragment<FragmentRegistrationBusinessApiBinding>(), RecyclerItemClickListener {

  private var list: ArrayList<ProcessApiSyncModel>? = null
  private var apiProcessAdapter: AppBaseRecyclerViewAdapter<ProcessApiSyncModel>? = null

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
    val request = getBusinessRequest()
    viewModel?.createBusinessOnboarding(request)?.observeOnce(viewLifecycleOwner, Observer {
      if (it.status == 200 || it.status == 201 || it.status == 202) {
        if (it.stringResponse.isNullOrEmpty().not()) {
          apiProcessChannelWhatsApp(dotProgressBar, it.stringResponse!!)
        } else updateError("Floating point null return!", it.status, "CREATE")
      } else updateError(it.message, it.status, "CREATE")
    })
  }

  private fun apiProcessChannelWhatsApp(dotProgressBar: DotProgressBar, floatingPointId: String) {
    if (requestFloatsModel?.channelActionDatas.isNullOrEmpty().not()) {
      viewModel?.postUpdateWhatsappRequest(UpdateChannelActionDataRequest(requestFloatsModel?.channelActionDatas!![0], requestFloatsModel?.contactInfo?.domainName))
              ?.observeOnce(viewLifecycleOwner, Observer {
                if (it.status == 200 || it.status == 201 || it.status == 202) {
                  apiProcessChannelAccessTokens(dotProgressBar, floatingPointId)
                } else updateError(it.message, it.status, "CHANNELS")
              })
    } else apiProcessChannelAccessTokens(dotProgressBar, floatingPointId)
  }


  private fun apiProcessChannelAccessTokens(dotProgressBar: DotProgressBar, floatingPointId: String) {
    if (requestFloatsModel?.channelAccessTokens.isNullOrEmpty().not()) {
      if (clientId.isNullOrEmpty().not()) {
        var index = 0
        requestFloatsModel?.channelAccessTokens?.forEach { channelAccessToken ->
          var isBreak = false
          viewModel?.updateChannelAccessToken(UpdateChannelAccessTokenRequest(channelAccessToken, clientId!!, floatingPointId))?.observeOnce(viewLifecycleOwner, Observer {
            index += 1
            if (it.status == 200 || it.status == 201 || it.status == 202) {
              if (requestFloatsModel?.channelAccessTokens?.size == index) apiBusinessComplete(dotProgressBar)
            } else {
              isBreak = true
              updateError(it.message, it.status, "CHANNELS")
            }
          })
          if (isBreak) return@forEach
        }
      }
    } else apiBusinessComplete(dotProgressBar)
  }

  private fun updateError(message: String?, status: Int?, type: String) {
    when (type) {
      "CREATE" -> {
        list?.forEach { apiSync ->
          apiSync.status = ProcessApiSyncModel.SyncStatus.ERROR.name
          apiSync.channels?.map { it.status = ProcessApiSyncModel.SyncStatus.ERROR.name; it }
        }
      }
      "CHANNELS" -> {
        list?.forEachIndexed { index, apiSync ->
          if (index != 0) {
            apiSync.status = ProcessApiSyncModel.SyncStatus.ERROR.name
            apiSync.channels?.map { it.status = ProcessApiSyncModel.SyncStatus.ERROR.name; it }
          }
        }
      }
    }
    apiProcessAdapter?.notify(list)
    binding?.errorView?.visible()
    binding?.errorApi?.text = if (message.isNullOrEmpty().not()) "$message: $status" else "Connection error: $status"
    binding?.next?.gone()
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
    createRequest.clientId = clientId
    createRequest.tag = requestFloatsModel?.contactInfo?.domainName
    createRequest.contactName = requestFloatsModel?.contactInfo?.businessName
    createRequest.name = ""
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
    createRequest.primaryCategory = requestFloatsModel?.categoryDataModel?.category_Name
    return createRequest
  }
}