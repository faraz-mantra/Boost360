package com.appservice.ui.domainbooking

import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.databinding.FragmentBookADomainSslBinding
import com.appservice.recyclerView.AppBaseRecyclerViewAdapter
import com.appservice.recyclerView.BaseRecyclerViewItem
import com.appservice.recyclerView.RecyclerItemClickListener
import com.appservice.ui.domainbooking.model.DomainSuggestionModel
import com.appservice.utils.WebEngageController
import com.framework.models.BaseViewModel
import com.framework.webengageconstant.*

class BookDomainSslFragment : AppBaseFragment<FragmentBookADomainSslBinding, BaseViewModel>(),
    RecyclerItemClickListener {

    companion object {
        @JvmStatic
        fun newInstance(bundle: Bundle? = null): BookDomainSslFragment {
            val fragment = BookDomainSslFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getLayout(): Int {
        return R.layout.fragment_book_a_domain_ssl
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {
        WebEngageController.trackEvent(DOMAIN_BOOK_A_DOMAIN_SSL_PAGE_LOAD, PAGE_VIEW, NO_EVENT_VALUE)
        setOnClickListeners()
        setData()
    }

    private fun setData() {
        val arrayDomainSuggestions = arrayListOf<DomainSuggestionModel>()
        arrayDomainSuggestions.add(DomainSuggestionModel("samplebizsite.com"))
        arrayDomainSuggestions.add(DomainSuggestionModel("samplebizsite.net"))
        arrayDomainSuggestions.add(DomainSuggestionModel("samplebusinesswebsite.net"))
        arrayDomainSuggestions.add(DomainSuggestionModel("samplebizsite.co.in"))
        arrayDomainSuggestions.add(DomainSuggestionModel("samplebizco.co.in"))

        val adapter = AppBaseRecyclerViewAdapter(baseActivity, arrayDomainSuggestions, itemClickListener = this@BookDomainSslFragment)
        val linearLayoutManager =
            LinearLayoutManager(baseActivity, LinearLayoutManager.VERTICAL, false)
        /*val dividerItemDecoration =
            DividerItemDecoration(baseActivity, linearLayoutManager.orientation)
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(baseActivity, R.drawable.adapter_divider_white_3)!!)*/
        binding?.rvSuggestedDomains?.layoutManager = linearLayoutManager
        //binding?.rvSuggestedDomains?.addItemDecoration(dividerItemDecoration)
        binding?.rvSuggestedDomains?.adapter = adapter
    }

    private fun setOnClickListeners() {
        binding?.btnSearchANewDomain?.setOnClickListener {
            WebEngageController.trackEvent(DOMAIN_SEARCH_A_NEW_DOMAIN_CLICK, CLICK, NO_EVENT_VALUE)
            startFragmentDomainBookingActivity(
                activity = baseActivity,
                type = com.appservice.constant.FragmentType.SEARCH_DOMAIN_FRAGMENT,
                bundle = Bundle(),
                clearTop = false
            )
        }

        binding?.btnSelectSuggestedDomain?.setOnClickListener {
            WebEngageController.trackEvent(DOMAIN_SELECT_SUGGESTED_DOMAIN_CLICK, CLICK, NO_EVENT_VALUE)
            startFragmentDomainBookingActivity(
                activity = baseActivity,
                type = com.appservice.constant.FragmentType.CONFIRMING_DOMAIN_FRAGMENT,
                bundle = Bundle(),
                clearTop = false
            )
        }
    }

    override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
        WebEngageController.trackEvent(DOMAIN_SELECT_SUGGESTED_DOMAIN_CLICK, CLICK, NO_EVENT_VALUE)
        startFragmentDomainBookingActivity(
            activity = baseActivity,
            type = com.appservice.constant.FragmentType.CONFIRMING_DOMAIN_FRAGMENT,
            bundle = Bundle(),
            clearTop = false
        )
    }
}