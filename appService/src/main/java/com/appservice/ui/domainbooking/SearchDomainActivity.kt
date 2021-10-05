package com.appservice.ui.domainbooking

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.appservice.R
import com.appservice.base.AppBaseActivity
import com.appservice.databinding.ActivitySearchDomainBinding
import com.appservice.databinding.BsheetConfirmDomainSearchBinding
import com.appservice.recyclerView.AppBaseRecyclerViewAdapter
import com.appservice.recyclerView.BaseRecyclerViewItem
import com.appservice.recyclerView.RecyclerItemClickListener
import com.appservice.ui.domainbooking.model.SimilarDomainSuggestionModel
import com.appservice.viewmodel.SearchDomainViewModel
import com.framework.base.BaseActivity
import com.framework.extensions.afterTextChanged
import com.framework.extensions.observeOnce
import com.framework.pref.clientId
import com.framework.utils.showKeyBoard
import com.framework.webengageconstant.LOGGED_IN
import com.google.android.material.bottomsheet.BottomSheetDialog

class SearchDomainActivity : AppBaseActivity<ActivitySearchDomainBinding, SearchDomainViewModel>(),
    RecyclerItemClickListener {

    override fun getLayout(): Int {
        return R.layout.activity_search_domain
    }

    override fun getViewModelClass(): Class<SearchDomainViewModel> {
        return SearchDomainViewModel::class.java
    }

    override fun onCreateView() {
        binding?.btnContinue?.setOnClickListener {
            confirmBottomSheet()
        }

        binding?.include?.customImageView4?.setOnClickListener {
            this.onNavPressed()
        }

        binding?.edSearchBox?.afterTextChanged {
            //setRecyclerData()
            searchDomain()
        }

        this.showKeyBoard(binding?.edSearchBox)
    }

    private fun searchDomain(){
        viewModel.searchDomain("SURESH", clientId, ".COM").observeOnce(lifecycleOwner = this, {
            if (!it.isSuccess() || it == null) {
                showShortToast(getString(R.string.something_went_wrong))
                return@observeOnce
            }

            val isAvailable = it.anyResponse as Boolean
            showShortToast(if (isAvailable) "Yes available!" else "Not available!")

        })
        /*viewModel.createDomain(clientId, "SURESH", ".IN",
            session.fpTag!!, "1", 0
        ).observeOnce(lifecycleOwner =  this, {
            Log.i("djkne", it.toString())
        })*/
    }

    private fun setRecyclerData() {
        val arrayDomainSuggestions = arrayListOf<SimilarDomainSuggestionModel>()
        arrayDomainSuggestions.add(SimilarDomainSuggestionModel("samplebizsite.com", false))
        arrayDomainSuggestions.add(SimilarDomainSuggestionModel("samplebizsite.net", false))
        arrayDomainSuggestions.add(SimilarDomainSuggestionModel("samplebusinesswebsite.net", false))
        arrayDomainSuggestions.add(SimilarDomainSuggestionModel("samplebizsite.co.in", false))
        arrayDomainSuggestions.add(SimilarDomainSuggestionModel("samplebizco.co.in", false))

        val adapter = AppBaseRecyclerViewAdapter(
            this,
            arrayDomainSuggestions,
            itemClickListener = this@SearchDomainActivity
        )
        binding?.rvSimilarDomains?.adapter = adapter
        binding?.rvSimilarDomains?.layoutManager = LinearLayoutManager(this)
    }

    private fun confirmBottomSheet() {
        val bSheet = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)
        val sheetBinding = DataBindingUtil.inflate<BsheetConfirmDomainSearchBinding>(
            layoutInflater,
            R.layout.bsheet_confirm_domain_search,
            null,
            false
        )
        bSheet.setContentView(sheetBinding.root)
        bSheet.setCancelable(false)
        sheetBinding.btnConfirm.setOnClickListener {
            startFragmentDomainBookingActivity(
                activity = this,
                type = com.appservice.constant.FragmentType.ACTIVE_NEW_DOMAIN_FRAGMENT,
                bundle = Bundle(),
                clearTop = false
            )
            bSheet.dismiss()
        }
        sheetBinding.btnGoBack.setOnClickListener {
            bSheet.dismiss()
        }

        sheetBinding.ivClose.setOnClickListener {
            bSheet.dismiss()
        }

        bSheet.show()
    }

    override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
    }
}