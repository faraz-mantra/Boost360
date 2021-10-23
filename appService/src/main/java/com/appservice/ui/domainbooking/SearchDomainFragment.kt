package com.appservice.ui.domainbooking

import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.databinding.ActivitySearchDomainBinding
import com.appservice.databinding.BsheetConfirmDomainSearchBinding
import com.appservice.model.domainBooking.request.CreateDomainRequest
import com.appservice.recyclerView.BaseRecyclerViewItem
import com.appservice.recyclerView.RecyclerItemClickListener
import com.appservice.utils.Validations
import com.appservice.utils.WebEngageController
import com.appservice.utils.getDomainSplitValues
import com.appservice.viewmodel.SearchDomainViewModel
import com.framework.extensions.afterTextChanged
import com.framework.extensions.gone
import com.framework.extensions.observeOnce
import com.framework.extensions.visible
import com.framework.pref.clientId
import com.framework.pref.getDomainName
import com.framework.utils.fromHtml
import com.framework.utils.showKeyBoard
import com.framework.webengageconstant.*
import com.google.android.material.bottomsheet.BottomSheetDialog

class SearchDomainFragment :
    AppBaseFragment<ActivitySearchDomainBinding, SearchDomainViewModel>(),
    RecyclerItemClickListener {

    companion object {
        @JvmStatic
        fun newInstance(bundle: Bundle? = null): SearchDomainFragment {
            val fragment = SearchDomainFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getLayout(): Int {
        return R.layout.activity_search_domain
    }

    override fun getViewModelClass(): Class<SearchDomainViewModel> {
        return SearchDomainViewModel::class.java
    }

    override fun onCreateView() {
        WebEngageController.trackEvent(DOMAIN_SEARCH_PAGE_LOAD, PAGE_VIEW, NO_EVENT_VALUE)
        setOnListeners()
        baseActivity.showKeyBoard(binding?.edSearchBox)
    }

    private fun setOnListeners() {
        binding?.btnContinue?.setOnClickListener {
            WebEngageController.trackEvent(DOMAIN_CONFIRM_SELECTED_DOMAIN_CLICK, CLICK, NO_EVENT_VALUE)
            confirmBottomSheet()
        }

        binding?.tvLearnHowToChooseDomain?.setOnClickListener {
            showShortToast(getString(R.string.coming_soon))
        }

        binding?.edSearchBox?.afterTextChanged {
            binding?.layputEtError?.gone()
            binding?.btnContinue?.gone()
            if (it == "")
                binding?.tvLearnHowToChooseDomain?.visible()
            else
                binding?.tvLearnHowToChooseDomain?.gone()
        }

        binding?.edSearchBox?.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (Validations.isDomainValid(binding?.edSearchBox?.text.toString())) {
                        searchDomain(binding?.edSearchBox?.text.toString())
                        return true
                    } else {
                        showShortToast(getString(R.string.error_wrong_domain_entered))
                    }
                }
                return false
            }

        })
    }

    private fun searchDomain(domainString: String) {
        val splitDomain = getDomainSplitValues(domainString)
        viewModel?.searchDomain(
            splitDomain.domainName.lowercase(),
            clientId,
            splitDomain.domainExtension.uppercase()
        )?.observeOnce(lifecycleOwner = this, {
            if (!it.isSuccess() || it == null) {
                showShortToast(getString(R.string.something_went_wrong))
                return@observeOnce
            }

            val isAvailable = it.anyResponse as Boolean
            binding?.layputEtError?.visible()

            if (isAvailable) {
                binding?.btnContinue?.visible()
                binding?.tvIsNotDomainAvailable?.gone()
                binding?.tvIsDomainAvailable?.visible()
                binding?.tvAvailabilityNotSuffix?.gone()
                binding?.tvAvailabilitySuffix?.visible()
            } else {
                binding?.btnContinue?.gone()
                binding?.tvIsNotDomainAvailable?.visible()
                binding?.tvIsDomainAvailable?.gone()
                binding?.tvAvailabilityNotSuffix?.visible()
                binding?.tvAvailabilitySuffix?.gone()
            }

            binding?.tvIsNotDomainAvailable?.text = domainString
            binding?.tvIsDomainAvailable?.text = domainString
        })
    }

    private fun createDomain(domainSelected: String, domainType: String, bSheet: BottomSheetDialog) {
        val createDomainRequest = CreateDomainRequest(
            clientId = clientId,
            domainName = domainSelected.lowercase(),
            domainType = domainType.uppercase(),
            existingFPTag = sessionLocal.fpTag!!,
            domainChannelType = 1,
            DomainRegService = 0,
            validityInYears = "1",
            DomainOrderType = 0,

            )
        viewModel?.createDomain(createDomainRequest)?.observeOnce(lifecycleOwner = this, {
            if (it.isSuccess().not() || it == null) {
                showShortToast(getString(R.string.something_went_wrong))
                return@observeOnce
            }
            WebEngageController.trackEvent(DOMAIN_SEARCHED_CREATE_SUCCESS, DOMAIN_CREATION_SUCCESS, NO_EVENT_VALUE)
            val stringResponse = it.stringResponse as String
            startFragmentDomainBookingActivity(
                activity = baseActivity,
                type = com.appservice.constant.FragmentType.ACTIVE_NEW_DOMAIN_FRAGMENT,
                bundle = Bundle(),
                clearTop = false
            )
            bSheet.dismiss()
            baseActivity.finish()
        })
    }

    /*private fun setRecyclerData() {
        val arrayDomainSuggestions = arrayListOf<SimilarDomainSuggestionModel>()
        arrayDomainSuggestions.add(SimilarDomainSuggestionModel("samplebizsite.com", false))
        arrayDomainSuggestions.add(SimilarDomainSuggestionModel("samplebizsite.net", false))
        arrayDomainSuggestions.add(SimilarDomainSuggestionModel("samplebusinesswebsite.net", false))
        arrayDomainSuggestions.add(SimilarDomainSuggestionModel("samplebizsite.co.in", false))
        arrayDomainSuggestions.add(SimilarDomainSuggestionModel("samplebizco.co.in", false))

        val adapter = AppBaseRecyclerViewAdapter(
            this,
            arrayDomainSuggestions,
            itemClickListener = this@SearchDomainFragment
        )
        binding?.rvSimilarDomains?.adapter = adapter
        binding?.rvSimilarDomains?.layoutManager = LinearLayoutManager(this)
    }*/

    private fun confirmBottomSheet() {
        val bSheet = BottomSheetDialog(baseActivity, R.style.BottomSheetDialogTheme)
        val sheetBinding = DataBindingUtil.inflate<BsheetConfirmDomainSearchBinding>(layoutInflater, R.layout.bsheet_confirm_domain_search, null, false)
        bSheet.setContentView(sheetBinding.root)
        bSheet.setCancelable(false)
        val enteredDomainName = binding?.edSearchBox?.text.toString()

        sheetBinding.tvWbAddress.text = enteredDomainName
        sheetBinding.customTextView4.text = fromHtml(
            "${getString(R.string.domain_confirm_msg_1)} " +
                    "<u>${sessionLocal.getDomainName(true)}</u>. " +
                    "${getString(R.string.domain_confirm_msg_2)}"
        )

        sheetBinding.btnConfirm.setOnClickListener {
            WebEngageController.trackEvent(DOMAIN_SEARCHED_SELECTED_DOMAIN_CLICK, CLICK, NO_EVENT_VALUE)
            startFragmentDomainBookingActivity(
                activity = baseActivity,
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
            WebEngageController.trackEvent(DOMAIN_BOTTOM_SHEET_CLOSE_CLICK, CLICK, NO_EVENT_VALUE)
            bSheet.dismiss()
        }

        sheetBinding.btnConfirm.setOnClickListener {
            WebEngageController.trackEvent(DOMAIN_CREATE_CALL_INVOKED_CLICK, CLICK, NO_EVENT_VALUE)
            val domainSplit = getDomainSplitValues(enteredDomainName)
            createDomain(domainSplit.domainName, domainSplit.domainExtension, bSheet)
        }

        bSheet.show()
    }

    override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
    }
}