package com.onboarding.nowfloats.ui.registration

import android.os.Bundle
import android.os.Handler
import android.view.View
import com.framework.extensions.getDrawable
import com.onboarding.nowfloats.constant.ChannelType
import com.onboarding.nowfloats.constant.RecyclerViewItemType
import com.onboarding.nowfloats.extensions.fadeIn
import com.onboarding.nowfloats.extensions.setGridRecyclerViewAdapter
import com.onboarding.nowfloats.factory.ObservableFactory
import com.onboarding.nowfloats.factory.afterTextChangeEvents
import com.onboarding.nowfloats.model.channel.*
import com.onboarding.nowfloats.R
import com.onboarding.nowfloats.databinding.FragmentRegistrationBusinessWebsiteBinding
import com.onboarding.nowfloats.recyclerView.AppBaseRecyclerViewAdapter
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit

class RegistrationBusinessWebsiteFragment : BaseRegistrationFragment<FragmentRegistrationBusinessWebsiteBinding>() {

    private var googleChannelsAdapter: AppBaseRecyclerViewAdapter<ChannelModel>? = null
    private var subdomainObservable: Disposable? = null

    companion object {
        @JvmStatic
        fun newInstance(bundle: Bundle? = null): RegistrationBusinessWebsiteFragment {
            val fragment = RegistrationBusinessWebsiteFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView() {
        super.onCreateView()
        binding?.googleChannels?.post {
            (binding?.googleChannels?.fadeIn(1000L)?.mergeWith(binding?.viewBusiness?.fadeIn()))
                ?.andThen(binding?.title?.fadeIn(200L))?.andThen(binding?.subTitle?.fadeIn(200L))
                ?.andThen(binding?.subdomain?.fadeIn()?.mergeWith(binding?.inputType?.fadeIn(200L)))
                ?.andThen(binding?.next?.fadeIn(100L))?.subscribe()
        }
        setOnClickListener(binding?.next)
        setSetSelectedGoogleChannels(channels)
        setSubDomain(requestFloatsModel?.contactInfo?.storeName, isInitial = true)
        addSubdomainObservable()
    }

    private fun addSubdomainObservable() {
        subdomainObservable = ObservableFactory.afterTextChangeEvents(binding?.subdomain)
            ?.debounce(0, TimeUnit.MILLISECONDS)
            ?.map { it.editable()?.toString() }
            ?.subscribe { setSubDomain(it) }
    }

    override fun getObservables(): List<Disposable?> {
        return listOf(subdomainObservable)
    }

    private fun setSubDomain(storeName: String?, isInitial: Boolean = false) {
        val subdomain = storeName?.filter { it.isLetterOrDigit() } ?: return
        val lengthDifference = storeName.length - subdomain.length

        if (subdomain.isNotEmpty()) {
            resources.getDrawable(baseActivity, R.drawable.ic_valid)
        }

        if (subdomain != storeName || isInitial) {
            val selection = binding?.subdomain?.selectionEnd?.minus(lengthDifference) ?: return
            binding?.subdomain?.setText(subdomain)
            if (selection > 1) binding?.subdomain?.setSelection(selection)
        }
    }

    private fun setSetSelectedGoogleChannels(list: ArrayList<ChannelModel>) {
        val selectedItems = ArrayList(list.filter { it.isGoogleChannel() }.map {
            it.recyclerViewType = RecyclerViewItemType.SELECTED_CHANNEL_ITEM.getLayout(); it
        })
        val googleBusiness = ChannelModel(type = ChannelType.GOOGLE_BUSINESS.name)
        googleBusiness.recyclerViewType = RecyclerViewItemType.SELECTED_CHANNEL_ITEM.getLayout()
        selectedItems.add(googleBusiness)

        googleChannelsAdapter = binding?.googleChannels?.setGridRecyclerViewAdapter(
            baseActivity,
            selectedItems.size,
            selectedItems
        )
        googleChannelsAdapter?.notifyDataSetChanged()
    }

    override fun onClick(v: View) {
        when (v) {
            binding?.next -> if ((binding?.textBtn?.visibility == View.VISIBLE)) {
                getDotProgress()?.let {
                    binding?.textBtn?.visibility = View.GONE
                    binding?.next?.addView(it)
                    it.startAnimation()
                    Handler().postDelayed({
                        it.stopAnimation()
                        it.removeAllViews()
                        binding?.textBtn?.visibility = View.VISIBLE
                        when {
                            channels.haveFacebookChannels() -> {
                                gotoFacebookDetails()
                            }
                            channels.haveTwitterChannels() -> {
                                gotoTwitterDetails()
                            }
                            channels.haveWhatsAppChannels() -> {
                                gotoWhatsAppCallDetails()
                            }
                            else -> {
                                gotoBusinessApiCallDetails()
                            }
                        }
                    }, 1000)
                }
            }
        }
    }
}