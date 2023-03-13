package com.appservice.ui.ecommerce.bottomsheets

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.appservice.R
import com.appservice.constant.IntentConstant
import com.appservice.model.aptsetting.UserFpDetailsResponse
import com.appservice.model.businessmodel.BusinessProfileUpdateRequest
import com.appservice.model.businessmodel.Update
import com.appservice.ui.aptsetting.ui.FragmentCatalogSettings
import com.appservice.ui.aptsetting.widgets.BottomSheetButtonNameSuccessfullyUpdated
import com.appservice.ui.ecommerce.FragmentEcommerceCatalogSettings
import com.appservice.viewmodel.RenameCategoryViewModel
import com.framework.base.BaseBottomSheetDialog
import com.framework.extensions.observeOnce
import com.framework.pref.UserSessionManager
import com.framework.pref.clientId
import kotlinx.android.synthetic.main.bottom_sheet_category_rename.view.*
import java.util.regex.Matcher
import java.util.regex.Pattern


@Suppress("NAME_SHADOWING")
class BottomSheetCategoryRename(
    fragmentCatalogSettings: FragmentCatalogSettings?,
    fragmentEcommerceCatalogSettings: FragmentEcommerceCatalogSettings?,
    flag: Int
) :
    BaseBottomSheetDialog<com.appservice.databinding.BottomSheetCategoryRenameBinding, RenameCategoryViewModel>() {
    private var fpDetails: UserFpDetailsResponse? = null
    lateinit var sessionLocal: UserSessionManager
    var regex = "^[a-zA-Z0-9]+$"
    var pattern: Pattern = Pattern.compile(regex)

    var fragmentCatalogSettings: FragmentCatalogSettings? = null
    var fragmentEcommerceCatalogSettings: FragmentEcommerceCatalogSettings? = null
    var flag: Int = 0

    init {
        this.fragmentCatalogSettings = fragmentCatalogSettings
        this.fragmentEcommerceCatalogSettings = fragmentEcommerceCatalogSettings
        this.flag = flag
    }

    override fun getLayout(): Int {
        return R.layout.bottom_sheet_category_rename
    }

    override fun getViewModelClass(): Class<RenameCategoryViewModel> {
        return RenameCategoryViewModel::class.java
    }

    override fun onCreateView() {
        sessionLocal = UserSessionManager(baseActivity)
        this.fpDetails =
            arguments?.getSerializable(IntentConstant.CATALOG_DATA.name) as? UserFpDetailsResponse

        val textRemaining = 18 - sessionLocal.customCta?.length!!
        binding?.catRename?.setText(sessionLocal.customCta)
        binding?.textCount?.text = textRemaining.toString()
        binding?.btnPublish?.isEnabled = textRemaining < 18

        binding?.rivCloseBottomSheet?.setOnClickListener {
            this.dismiss()
        }

        binding?.btnPublish?.setOnClickListener {
            val businessProfileUpdateUrl =
                "https://api2.withfloats.com/Discover/v1/FloatingPoint/update"
            val businessProfileUpdateRequest = BusinessProfileUpdateRequest()
            businessProfileUpdateRequest.clientId = clientId
            businessProfileUpdateRequest.fpTag = sessionLocal.fpTag
            val updateItemList = arrayListOf<Update>()
            val ctaInfo = Update()
            ctaInfo.key = "CTATOGGLE";
            ctaInfo.value = "${true}#${binding?.catRename?.text.toString().uppercase()}"
            updateItemList.add(ctaInfo)
            businessProfileUpdateRequest.updates = updateItemList
            binding?.progressBar?.visibility = View.VISIBLE
            viewModel?.updateBusinessDetails(
                businessProfileUpdateUrl,
                businessProfileUpdateRequest
            )
                ?.observeOnce(viewLifecycleOwner) {
                    if (it.isSuccess()) {
                        sessionLocal.customCta = binding?.catRename?.text.toString()
                        if (flag == 1) {
                            fragmentCatalogSettings?.catalogueName?.text =
                                binding?.catRename?.text
                        } else {
                            fragmentEcommerceCatalogSettings?.catalogueName?.text =
                                binding?.catRename?.text
                        }
                        BottomSheetButtonNameSuccessfullyUpdated().apply {
                            if (flag == 1) {
                                show(
                                    fragmentCatalogSettings!!.parentFragmentManager,
                                    BottomSheetButtonNameSuccessfullyUpdated::class.java.name
                                )
                            } else {
                                show(
                                    fragmentEcommerceCatalogSettings!!.parentFragmentManager,
                                    BottomSheetButtonNameSuccessfullyUpdated::class.java.name
                                )
                            }
                        }
                    } else {
                        showShortToast("Error while updating the catalogue information")
                    }
                    binding?.progressBar?.visibility = View.GONE
                    this.dismiss()
                }
        }

        binding?.catRename?.addTextChangedListener(object : TextViewListener() {
            override fun onTextChanged(
                outputString: String?
            ) {
                startUpdates()
                binding?.catRename?.setText(outputString)
                val pos: Int? = outputString?.length
                binding?.catRename?.setSelection(pos!!)

                val textRemaining = 18 - pos!!

                binding?.btnPublish?.isEnabled = textRemaining < 18
                binding?.textCount?.text = textRemaining.toString()

                val m: Matcher = pattern.matcher(outputString.toString())
                if (m.matches()) {
                    binding?.alertView?.visibility = View.GONE
                } else {
                    binding?.alertView?.visibility = View.VISIBLE
                }
                endUpdates()
            }

        })

    }

    internal abstract class TextViewListener : TextWatcher {

        private var outputString: String? = null
        private var ignore = false
        override fun beforeTextChanged(sequence: CharSequence, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(sequence: CharSequence, start: Int, before: Int, count: Int) {
            outputString = sequence.toString().replaceFirstChar { it.uppercase() }
        }

        override fun afterTextChanged(sequence: Editable) {
            if (ignore) return
            onTextChanged(outputString)
        }

        protected abstract fun onTextChanged(
            outputString: String?
        )

        protected fun startUpdates() {
            ignore = true
        }

        protected fun endUpdates() {
            ignore = false
        }
    }

}