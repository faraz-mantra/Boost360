package com.dashboard.controller.ui.website_theme.bottomsheet

import android.view.View
import com.dashboard.R
import com.dashboard.controller.getDomainName
import com.dashboard.databinding.BottomSheetThemeUpdatedSuccessfullyBinding
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel
import com.framework.pref.UserSessionManager
import com.framework.utils.ContentSharing
import com.framework.utils.fromHtml

enum class TypeSuccess {
    CLOSE, VISIT_WEBSITE, PUBLISH_CHANGES, DISCARD, GO_BACK
}

class WebsiteThemeUpdatedSuccessfullySheet :
    BaseBottomSheetDialog<BottomSheetThemeUpdatedSuccessfullyBinding, BaseViewModel>() {

    private lateinit var websiteLink: String
    private lateinit var businessName: String
    private lateinit var businessContact: String
    private lateinit var userSessionManager: UserSessionManager
    var onClicked: (value: String) -> Unit = { }
    override fun getLayout(): Int {
        return R.layout.bottom_sheet_theme_updated_successfully
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {
        setOnClickListener(
            binding?.rivCloseBottomSheet,
            binding?.visitWebsite,
            binding?.btnFb,
            binding?.btnCopyMessage,
            binding?.btnTwitter,
            binding?.btnLinkedin,
            binding?.shareWhatsapp,
            binding?.btnShare
        )
        isCancelable = false
        binding?.txtMessage?.text = getString(R.string.website_updated)
        binding?.txtDesc?.text = getString(R.string.your_theme_update_published)
        this.userSessionManager = UserSessionManager(requireActivity())
        this.websiteLink = fromHtml("<u>${userSessionManager.getDomainName()}</u>").toString()
        businessName = userSessionManager.fPName!!
        businessContact = userSessionManager.fPPrimaryContactNumber!!

    }

    override fun onClick(v: View) {
        super.onClick(v)
        when (v) {
            binding?.rivCloseBottomSheet -> {
                dismiss()
                onClicked(TypeSuccess.CLOSE.name)
            }
            binding?.visitWebsite -> onClicked(TypeSuccess.VISIT_WEBSITE.name)
            binding?.btnFb -> ContentSharing.shareWebsiteTheme(
                businessName,
                websiteLink,
                businessContact,
                isFb = true
            )
            binding?.btnTwitter -> ContentSharing.shareWebsiteTheme(
                businessName,
                websiteLink,
                businessContact,
                isTwitter = true
            )
            binding?.shareWhatsapp -> ContentSharing.shareWebsiteTheme(
                businessName,
                websiteLink,
                businessContact,
                isWhatsApp = true
            )
            binding?.btnLinkedin -> ContentSharing.shareWebsiteTheme(
                businessName,
                websiteLink,
                businessContact,
                isLinkedin = true
            )
            binding?.btnShare -> ContentSharing.shareWebsiteTheme(
                businessName,
                websiteLink,
                businessContact
            )
            binding?.btnCopyMessage -> ContentSharing.shareWebsiteTheme(
                businessName,
                websiteLink,
                businessContact,
                isCopy = true
            )
        }
    }
}