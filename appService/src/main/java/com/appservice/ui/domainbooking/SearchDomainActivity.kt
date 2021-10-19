package com.appservice.ui.domainbooking

import android.os.Bundle
import android.text.Html
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.appservice.R
import com.appservice.base.AppBaseActivity
import com.appservice.databinding.ActivitySearchDomainBinding
import com.appservice.databinding.BsheetConfirmDomainSearchBinding
import com.appservice.model.domainBooking.request.CreateDomainRequest
import com.appservice.recyclerView.AppBaseRecyclerViewAdapter
import com.appservice.recyclerView.BaseRecyclerViewItem
import com.appservice.recyclerView.RecyclerItemClickListener
import com.appservice.ui.domainbooking.model.SimilarDomainSuggestionModel
import com.appservice.utils.Validations
import com.appservice.utils.getDomainSplitValues
import com.appservice.viewmodel.SearchDomainViewModel
import com.framework.extensions.afterTextChanged
import com.framework.extensions.gone
import com.framework.extensions.observeOnce
import com.framework.extensions.visible
import com.framework.pref.clientId
import com.framework.pref.getDomainName
import com.framework.utils.showKeyBoard
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
            binding?.layputEtError?.gone()
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

        this.showKeyBoard(binding?.edSearchBox)
    }

    private fun searchDomain(domainString: String) {
        val splitDomain = getDomainSplitValues(domainString)
        viewModel.searchDomain(
            splitDomain.domainName.lowercase(),
            clientId,
            splitDomain.domainExtension.uppercase()
        ).observeOnce(lifecycleOwner = this, {
            if (!it.isSuccess() || it == null) {
                showShortToast(getString(R.string.something_went_wrong))
                return@observeOnce
            }

            val isAvailable = it.anyResponse as Boolean
            binding?.layputEtError?.visible()

            val color: Int
            val stringAvailability: Int
            if (isAvailable) {
                color = R.color.green_dark
                stringAvailability = R.string.is_available_new
                binding?.btnContinue?.visible()
            } else {
                color = R.color.red_dark
                stringAvailability = R.string.is_not_available
                binding?.btnContinue?.gone()
            }
            binding?.tvIsDomainAvailable?.text = "$domainString "
            binding?.tvIsDomainAvailable?.setTextColor(ContextCompat.getColor(this, color))
            binding?.tvAvailabilitySuffix?.text = getString(stringAvailability)
        })
    }

    private fun createDomain(domainSelected: String, domainType: String, bSheet: BottomSheetDialog) {
        val createDomainRequest = CreateDomainRequest(
            clientId = clientId,
            domainName = domainSelected.lowercase(),
            domainType = domainType.uppercase(),
            existingFPTag = session.fpTag!!,
            domainChannelType = 1,
            DomainRegService = 0,
            validityInYears = "1",
            DomainOrderType = 0,

            )
        viewModel.createDomain(createDomainRequest).observeOnce(lifecycleOwner = this, {
            if (!it.isSuccess() || it == null) {
                showShortToast(getString(R.string.something_went_wrong))
                return@observeOnce
            }

            val stringResponse = it.stringResponse as String
            startFragmentDomainBookingActivity(
                activity = this,
                type = com.appservice.constant.FragmentType.ACTIVE_NEW_DOMAIN_FRAGMENT,
                bundle = Bundle(),
                clearTop = false
            )
            bSheet.dismiss()
        })
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
        val enteredDomainName = binding?.edSearchBox?.text.toString()

        sheetBinding.tvWbAddress.text = enteredDomainName
        sheetBinding.customTextView4.text = Html.fromHtml(
            "${getString(R.string.domain_confirm_msg_1)} " +
                    "<u>${session.getDomainName(true)}</u>. " +
                    "${getString(R.string.domain_confirm_msg_2)}"
        )

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

        sheetBinding.btnConfirm.setOnClickListener {
            val domainSplit = getDomainSplitValues(enteredDomainName)
            createDomain(domainSplit.domainName, domainSplit.domainExtension, bSheet)
        }

        bSheet.show()
    }

    override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
    }
}