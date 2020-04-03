package com.onboarding.nowfloats.ui.registration

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.framework.extensions.gone
import com.framework.extensions.observeOnce
import com.framework.extensions.visible
import com.framework.utils.NetworkUtils
import com.framework.views.DotProgressBar
import com.onboarding.nowfloats.R
import com.onboarding.nowfloats.databinding.FragmentRegistrationBusinessApiBinding
import com.onboarding.nowfloats.model.ProcessApiSyncModel
import com.onboarding.nowfloats.model.business.BusinessCreateRequest
import com.onboarding.nowfloats.model.channel.ChannelModel
import com.onboarding.nowfloats.model.channel.ChannelType
import com.onboarding.nowfloats.model.channel.getType
import com.onboarding.nowfloats.model.channel.request.ChannelAccessToken
import com.onboarding.nowfloats.model.channel.request.UpdateChannelAccessTokenRequest
import com.onboarding.nowfloats.model.channel.request.UpdateChannelActionDataRequest
import com.onboarding.nowfloats.model.channel.request.getType
import com.onboarding.nowfloats.recyclerView.AppBaseRecyclerViewAdapter
import com.onboarding.nowfloats.recyclerView.BaseRecyclerViewItem
import com.onboarding.nowfloats.recyclerView.RecyclerItemClickListener
import com.onboarding.nowfloats.ui.InternetErrorDialog
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
        setProcessApiSyncModel()
        setApiProcessAdapter(list)
        if (requestFloatsModel?.floatingPointId.isNullOrEmpty().not()) {
            getDotProgress()?.let { apiBusinessComplete(it, requestFloatsModel?.floatingPointId!!) }
        } else {
            getDotProgress()?.let {
                binding?.textBtn?.visibility = View.GONE
                binding?.next?.addView(it)
                if (NetworkUtils.isNetworkConnected()) {
                    it.startAnimation()
                    putCreateBusinessOnboarding(it)
                } else {
                    val dialog = InternetErrorDialog()
                    dialog.onRetryTapped = {
                        it.startAnimation()
                        putCreateBusinessOnboarding(it)
                    }
                    dialog.show(parentFragmentManager, dialog.javaClass.name)
                }
            }
        }
    }

    private fun setProcessApiSyncModel() {
        val channels = this.channels
        val connectedChannelsAccessTokens = requestFloatsModel?.channelAccessTokens?.map { it.getType() }
        val connectedWhatsApp = requestFloatsModel?.channelActionDatas?.firstOrNull()

        val connectedChannels = ArrayList<ChannelModel>()
        for (channel in channels) {
            val isSelected = when (channel.getType()) {
                ChannelType.G_SEARCH -> true
                ChannelType.FB_PAGE -> connectedChannelsAccessTokens?.contains(ChannelAccessToken.AccessTokenType.Facebookpage)
                ChannelType.G_MAPS -> true
                ChannelType.FB_SHOP -> connectedChannelsAccessTokens?.contains(ChannelAccessToken.AccessTokenType.Facebookshop)
                ChannelType.WAB -> connectedWhatsApp != null
                ChannelType.T_FEED -> connectedChannelsAccessTokens?.contains(ChannelAccessToken.AccessTokenType.Twitter)
                ChannelType.G_BUSINESS -> true
                null -> false
            }
            if (isSelected == true) {
                connectedChannels.add(channel)
            }
        }
        list = ProcessApiSyncModel().getData(connectedChannels)
    }

    private fun putCreateBusinessOnboarding(dotProgressBar: DotProgressBar) {
        val request = getBusinessRequest()
        viewModel?.putCreateBusinessOnboarding(userProfileId, request)?.observeOnce(viewLifecycleOwner, Observer {
            if (it.status == 200 || it.status == 201 || it.status == 202) {
                if (it.stringResponse.isNullOrEmpty().not()) {
                    apiProcessChannelWhatsApp(dotProgressBar, it.stringResponse ?: "")
                } else updateError("Floating point return null", it.status, "CREATE")
            } else updateError(it.error?.localizedMessage, it.status, "CREATE")
        })
    }

    private fun apiProcessChannelWhatsApp(dotProgressBar: DotProgressBar, floatingPointId: String) {
        if (requestFloatsModel?.channelActionDatas.isNullOrEmpty().not()) {
            viewModel?.postUpdateWhatsappRequest(UpdateChannelActionDataRequest(requestFloatsModel?.channelActionDatas!![0], requestFloatsModel?.contactInfo?.domainName))
                    ?.observeOnce(viewLifecycleOwner, Observer {
                        if (it.status == 200 || it.status == 201 || it.status == 202) {
                            apiProcessChannelAccessTokens(dotProgressBar, floatingPointId)
                        } else updateError(it.error?.localizedMessage, it.status, "CHANNELS")
                    })
        } else apiProcessChannelAccessTokens(dotProgressBar, floatingPointId)
    }


    private fun apiProcessChannelAccessTokens(dotProgressBar: DotProgressBar, floatingPointId: String) {
        if (requestFloatsModel?.channelAccessTokens.isNullOrEmpty().not()) {
            if (clientId != null) {
                var index = 0
                requestFloatsModel?.channelAccessTokens?.forEach { channelAccessToken ->
                    var isBreak = false
                    viewModel?.updateChannelAccessToken(UpdateChannelAccessTokenRequest(channelAccessToken, clientId!!, floatingPointId))?.observeOnce(viewLifecycleOwner, Observer {
                        index += 1
                        if (it.status == 200 || it.status == 201 || it.status == 202) {
                            if (requestFloatsModel?.channelAccessTokens?.size == index) apiBusinessComplete(dotProgressBar, floatingPointId)
                        } else {
                            isBreak = true
                            updateError(it.error?.localizedMessage, it.status, "CHANNELS")
                        }
                    })
                    if (isBreak) return@forEach
                }
            }
        } else apiBusinessComplete(dotProgressBar, floatingPointId)
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

    private fun apiBusinessComplete(dotProgressBar: DotProgressBar, floatingPointId: String) {
        binding?.apiRecycler?.post {
            requestFloatsModel?.floatingPointId = floatingPointId
            updateInfo()
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
        createRequest.name = requestFloatsModel?.contactInfo?.businessName
        createRequest.address = requestFloatsModel?.contactInfo?.address
        createRequest.city = ""
        createRequest.pincode = ""
        createRequest.country = "india"
        createRequest.primaryNumber = requestFloatsModel?.contactInfo?.number
        createRequest.email = requestFloatsModel?.contactInfo?.email
        createRequest.primaryNumberCountryCode = "+91"
        createRequest.uri = ""
        createRequest.fbPageName = requestFloatsModel?.channelAccessTokens?.firstOrNull { it.getType() == ChannelAccessToken.AccessTokenType.Facebookpage }?.userAccountName
        createRequest.primaryCategory = requestFloatsModel?.categoryDataModel?.category_key
        return createRequest
    }
}