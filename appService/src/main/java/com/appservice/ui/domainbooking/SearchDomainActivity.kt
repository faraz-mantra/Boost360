package com.appservice.ui.domainbooking

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.appservice.R
import com.appservice.databinding.ActivitySearchDomainBinding
import com.appservice.databinding.BsheetConfirmDomainSearchBinding
import com.appservice.recyclerView.AppBaseRecyclerViewAdapter
import com.appservice.recyclerView.BaseRecyclerViewItem
import com.appservice.recyclerView.RecyclerItemClickListener
import com.appservice.ui.domainbooking.model.DomainSuggestionModel
import com.appservice.ui.domainbooking.model.SimilarDomainSuggestionModel
import com.framework.base.BaseActivity
import com.framework.models.BaseViewModel
import com.framework.utils.showKeyBoard
import com.google.android.material.bottomsheet.BottomSheetDialog

class SearchDomainActivity: BaseActivity<ActivitySearchDomainBinding, BaseViewModel>(),
    RecyclerItemClickListener {

    override fun getLayout(): Int {
        return R.layout.activity_search_domain
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {
        binding?.btnContinue?.setOnClickListener {
            confirmBottomSheet()
        }

        binding?.include?.customImageView4?.setOnClickListener{
            this.onNavPressed()
        }

        binding?.edSearchBox?.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                setRecyclerData()
            }
        })

        this.showKeyBoard(binding?.edSearchBox)
    }

    private fun setRecyclerData() {
        val arrayDomainSuggestions = arrayListOf<SimilarDomainSuggestionModel>()
        arrayDomainSuggestions.add(SimilarDomainSuggestionModel("samplebizsite.com", false))
        arrayDomainSuggestions.add(SimilarDomainSuggestionModel("samplebizsite.net", false))
        arrayDomainSuggestions.add(SimilarDomainSuggestionModel("samplebusinesswebsite.net", false))
        arrayDomainSuggestions.add(SimilarDomainSuggestionModel("samplebizsite.co.in", false))
        arrayDomainSuggestions.add(SimilarDomainSuggestionModel("samplebizco.co.in", false))

        val adapter = AppBaseRecyclerViewAdapter(this, arrayDomainSuggestions, itemClickListener = this@SearchDomainActivity)
        binding?.rvSimilarDomains?.adapter = adapter
        binding?.rvSimilarDomains?.layoutManager = LinearLayoutManager(this)
    }

    private fun confirmBottomSheet() {
        val bSheet = BottomSheetDialog(this,R.style.BottomSheetDialogTheme)
        val sheetBinding = DataBindingUtil.inflate<BsheetConfirmDomainSearchBinding>(layoutInflater,R.layout.bsheet_confirm_domain_search,null,false)
        bSheet.setContentView(sheetBinding.root)
        bSheet.setCancelable(false)
        sheetBinding.btnConfirm.setOnClickListener{
            startFragmentDomainBookingActivity(
                activity = this,
                type = com.appservice.constant.FragmentType.ACTIVE_NEW_DOMAIN_FRAGMENT,
                bundle = Bundle(),
                clearTop = false
            )
            bSheet.dismiss()
        }
        sheetBinding.btnGoBack.setOnClickListener{
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