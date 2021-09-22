package com.appservice.ui.domainbooking

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.databinding.FragmentBankAccountDetailsBinding
import com.appservice.databinding.FragmentBookADomainSslBinding
import com.appservice.recyclerView.AppBaseRecyclerViewAdapter
import com.appservice.recyclerView.BaseRecyclerViewItem
import com.appservice.recyclerView.RecyclerItemClickListener
import com.appservice.ui.bankaccount.AccountFragmentContainerActivity
import com.appservice.ui.bankaccount.BankAccountFragment
import com.appservice.ui.domainbooking.model.DomainSuggestionModel
import com.appservice.viewmodel.AccountViewModel
import com.framework.base.BaseFragment
import com.framework.models.BaseViewModel

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
        (baseActivity as? DomainBookingContainerActivity)?.setToolbarTitleNew(
        resources.getString(
            R.string.book_a_domain_ssl
        ), resources.getDimensionPixelSize(R.dimen.size_44))
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

        val adapter = AppBaseRecyclerViewAdapter(baseActivity, arrayDomainSuggestions, this)
        binding?.rvSuggestedDomains?.adapter = adapter
        binding?.rvSuggestedDomains?.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setOnClickListeners() {
        binding?.btnSearchANewDomain?.setOnClickListener{
            startActivity(Intent(activity, SearchDomainActivity::class.java))
        }

        binding?.btnSelectSuggestedDomain?.setOnClickListener{
            startFragmentDomainBookingActivity(
                activity = baseActivity,
                type = com.appservice.constant.FragmentType.CONFIRMING_DOMAIN_FRAGMENT,
                bundle = Bundle(),
                clearTop = false
            )
        }
    }

    override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
        startFragmentDomainBookingActivity(
            activity = baseActivity,
            type = com.appservice.constant.FragmentType.CONFIRMING_DOMAIN_FRAGMENT,
            bundle = Bundle(),
            clearTop = false
        )
    }
}