package com.dashboard.controller.ui.websiteTheme

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.PopupWindow
import com.appservice.model.SessionData
import com.dashboard.R
import com.dashboard.base.AppBaseFragment
import com.dashboard.constant.IntentConstant
import com.dashboard.constant.RecyclerViewActionType
import com.dashboard.controller.getDomainName
import com.dashboard.controller.ui.websiteTheme.bottomsheet.*
import com.dashboard.controller.ui.websiteTheme.dialog.WebViewDialog
import com.dashboard.databinding.FragmentWebsiteThemeBinding
import com.dashboard.model.websitetheme.*
import com.dashboard.recyclerView.AppBaseRecyclerViewAdapter
import com.dashboard.recyclerView.BaseRecyclerViewItem
import com.dashboard.recyclerView.RecyclerItemClickListener
import com.dashboard.utils.WebEngageController
import com.dashboard.viewmodel.WebsiteThemeViewModel
import com.framework.extensions.gone
import com.framework.extensions.observeOnce
import com.framework.extensions.visible
import com.framework.pref.Key_Preferences
import com.framework.pref.UserSessionManager
import com.framework.views.customViews.CustomTextView
import com.framework.webengageconstant.*
import kotlinx.android.synthetic.main.activity_dashboard.view.*

class FragmentWebsiteTheme : AppBaseFragment<FragmentWebsiteThemeBinding, WebsiteThemeViewModel>(), RecyclerItemClickListener {

  private var popupWindow: PopupWindow? = null
  private var session: UserSessionManager? = null
  private var sessionData: SessionData? = null
  private var primaryItem: PrimaryItem? = null
  private var secondaryItem: SecondaryItem? = null
  private var colorsItem: ColorsItem? = null
  private var secondaryFont: List<SecondaryItem?>? = null
  private var primaryFont: List<PrimaryItem?>? = null
  private var adapter: AppBaseRecyclerViewAdapter<ColorsItem>? = null
  private var fonts: FontsList? = null
  var colors: ArrayList<ColorsItem>? = null

  companion object {
    @JvmStatic
    fun newInstance(bundle: Bundle? = null): FragmentWebsiteTheme {
      val fragment = FragmentWebsiteTheme()
      fragment.arguments = bundle
      return fragment
    }
  }

  override fun getLayout(): Int {
    return R.layout.fragment_website_theme
  }

  override fun getViewModelClass(): Class<WebsiteThemeViewModel> {
    return WebsiteThemeViewModel::class.java
  }

  override fun onCreateView() {
    super.onCreateView()

    WebEngageController.trackEvent(WEBSITE_STYLE_LOAD, PAGE_VIEW, NO_EVENT_VALUE)
    setOnClickListener(
      binding?.ctfPrimaryFont, binding?.ctfSecondaryFont,
      binding?.btnDone, binding?.openWebsite, binding?.more, binding?.back
    )
    session = UserSessionManager(baseActivity)
    this.sessionData = arguments?.get(com.appservice.constant.IntentConstant.SESSION_DATA.name) as? SessionData
    getWebsiteTheme(sessionData)
    setWebsiteData()
    hideActionButtons()

  }

  private fun setWebsiteData() {
    binding?.ctvWebsite?.text = UserSessionManager(baseActivity).getDomainName()
  }

  private fun getWebsiteTheme(sessionData: SessionData?) {
    showProgress()
    viewModel?.getWebsiteTheme(sessionData?.fpId!!)?.observeOnce(viewLifecycleOwner, {
      hideProgress()
      val data = it as? WebsiteThemeResponse
      if (it.isSuccess() && data != null) {
        colors = data.result?.colors
        this.fonts = data.result?.fonts
        setFonts()
        setColor()
        binding?.root?.visible()
      } else {
        binding?.root?.gone()
        showShortToast(getString(R.string.error_while_getting_website_theme))
      }
    })
  }

  private fun setColor() {
    this.colorsItem = colors?.firstOrNull { it.isSelected == true } ?: colors?.firstOrNull { it.defaultColor == true }
    val defaultColorCode = colors?.firstOrNull { it.defaultColor == true }?.primary
    if (defaultColorCode != null) {
      binding?.rivDefaultColor?.setBackgroundColor(Color.parseColor(defaultColorCode))
    }
    binding?.rvColors?.setHasFixedSize(true)
    this.adapter = AppBaseRecyclerViewAdapter(
      baseActivity,
      colors ?: arrayListOf(),
      this@FragmentWebsiteTheme
    )
    binding?.rvColors?.adapter = adapter
  }

  private fun setFonts() {
    //set primary font
    this.primaryFont = fonts?.primary
    //set secondary font
    this.secondaryFont = fonts?.secondary
    //if  font is empty disable the view
    if (primaryFont.isNullOrEmpty())
      binding?.ctfPrimaryFont?.isEnabled = false
    if (secondaryFont.isNullOrEmpty()) {
      binding?.ctfSecondaryFont?.gone()
      binding?.primaryFontH?.gone()
      binding?.ctvSecondaryFont?.gone()
    }
    val defaultPrimaryFont = primaryFont?.firstOrNull { it?.defaultFont == true }
    val primarySelectedFont = primaryFont?.firstOrNull { it?.isSelected == true }
    val defaultSecondaryFont = secondaryFont?.firstOrNull { it?.defaultFont == true }
    val secondarySelected = secondaryFont?.firstOrNull { it?.isSelected == true }
    this.primaryItem = primarySelectedFont ?: defaultPrimaryFont
    this.secondaryItem = secondarySelected ?: defaultSecondaryFont

    if (this.primaryItem != null) {
      binding?.ctfPrimaryFont?.setText("${this.primaryItem?.description} ${if (this.primaryItem?.defaultFont == true) "(default)" else ""}")
    } else {
      binding?.ctfPrimaryFont?.setText(getString(R.string.choose_primary_font))
    }
    if (this.secondaryItem != null) {
      binding?.ctfSecondaryFont?.setText("${this.secondaryItem?.description} ${if (this.secondaryItem?.defaultFont == true) "(default)" else ""}")
    } else {
      binding?.ctfSecondaryFont?.setText(getString(R.string.choose_secondary_font))
    }
  }

  override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
    when (actionType) {
      RecyclerViewActionType.ITEM_COLOR_CLICK.ordinal -> {
        updateColors(item)
      }
    }
  }

  private fun updateColors(item: BaseRecyclerViewItem?) {
    this.colorsItem = item as? ColorsItem
    showShortToast("${colorsItem?.name} color selected.")
    colors?.forEach { if (item != it) it.isSelected = false }
    binding?.rvColors?.post { adapter?.notifyDataSetChanged() }
    showActionButtons()

  }

  private fun showPrimaryFontsBottomSheet(fontsList: ArrayList<PrimaryItem?>?) {
    val bottomSheetSelectFont = BottomSheetSelectFont()
    bottomSheetSelectFont.arguments =
      Bundle().apply { putSerializable(IntentConstant.FONT_LIST_PRIMARY.name, fontsList) }
    bottomSheetSelectFont.onPrimaryClicked = {
      this.primaryItem = it
      binding?.ctfPrimaryFont?.setText(it.description)
      showActionButtons()
    }
    bottomSheetSelectFont.show(parentFragmentManager, BottomSheetSelectFont::javaClass.name)
  }

  private fun showActionButtons() {
    binding?.btnDone?.visible()
  }

  private fun hideActionButtons() {
    binding?.btnDone?.gone()
  }

  private fun showSecondaryFontsBottomSheet(fontsList: ArrayList<SecondaryItem?>?) {
    val bottomSheetSelectFont = BottomSheetSelectFont()
    bottomSheetSelectFont.arguments =
      Bundle().apply { putSerializable(IntentConstant.FONT_LIST_SECONDARY.name, fontsList) }
    bottomSheetSelectFont.onSecondaryClicked = {
      this.secondaryItem = it
      binding?.ctfSecondaryFont?.setText(it.description)
      showActionButtons()
    }
    bottomSheetSelectFont.show(parentFragmentManager, BottomSheetSelectFont::javaClass.name)
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.ctfPrimaryFont -> {
        showPrimaryFontsBottomSheet(primaryFont as? ArrayList<PrimaryItem?>?)
      }
      binding?.ctfSecondaryFont -> {
        showSecondaryFontsBottomSheet(secondaryFont as? ArrayList<SecondaryItem?>?)
      }

      binding?.btnDone -> {
        WebEngageController.trackEvent(WEBSITE_STYLE_SAVE, CLICK, NO_EVENT_VALUE)
        updateAPI()
      }
      binding?.openWebsite -> {
        val website = binding?.ctvWebsite?.text.toString()
        openWebViewDialog(url = website, session?.getFPDetails(Key_Preferences.GET_FP_DETAILS_BUSINESS_NAME) ?: "")
      }
      binding?.more -> {
        if (this.popupWindow?.isShowing == true) this.popupWindow?.dismiss()
        else showPopupWindow(binding?.more!!)
      }
      binding?.back -> {
        goBack()
      }
    }
  }

  private fun updateAPI(isReset: Boolean? = false) {
    showProgress()
    if (isReset == true) {
      WebEngageController.trackEvent(WEBSITE_STYLE_RESET, CLICK, NO_EVENT_VALUE)
      viewModel?.updateWebsiteTheme(
        WebsiteThemeUpdateRequest(
          getDefaultRequest(), floatingPointId = sessionData?.fpId
        )
      )?.observeOnce(viewLifecycleOwner, {
        hideProgress()
        if (it.isSuccess()) openSuccessDialog()
        else showShortToast(getString(R.string.something_went_wrong))
      })
    } else {
      WebEngageController.trackEvent(WEBSITE_STYLE_UPDATE, CLICK, NO_EVENT_VALUE)
      viewModel?.updateWebsiteTheme(
        WebsiteThemeUpdateRequest(
          Customization(
            Colors(
              colorsItem?.secondary,
              colorsItem?.tertiary,
              colorsItem?.primary,
              colorsItem?.name
            ),
            Fonts(
              secondaryItem ?: secondaryFont?.firstOrNull(),
              primaryItem ?: primaryFont?.firstOrNull()
            )
          ),
          floatingPointId = sessionData?.fpId
        )
      )?.observeOnce(viewLifecycleOwner, {
        hideProgress()
        if (it.isSuccess()) openSuccessDialog()
        else showShortToast(getString(R.string.something_went_wrong))
      })
    }
  }

  private fun getDefaultRequest(): Customization {
    val defaultColor = colors?.first { it.defaultColor == true }
    val defaultFontPrimary = fonts?.primary?.first { it?.defaultFont == true }
    val defaultFontSecondary = fonts?.secondary?.first { it?.defaultFont == true }
    return Customization(
      Colors(
        defaultColor?.secondary,
        defaultColor?.tertiary,
        defaultColor?.primary,
        defaultColor?.name
      ),
      Fonts(defaultFontSecondary, defaultFontPrimary)
    )
  }

  private fun openSuccessDialog() {
    val websiteUpdateSheet = WebsiteThemeUpdatedSuccessfullySheet(isRepublishFlow = false)
    websiteUpdateSheet.onClicked = {
      when (it) {
        TypeSuccess.VISIT_WEBSITE.name -> {
          openWebViewDialog(binding?.ctvWebsite?.text.toString(), session?.getFPDetails(Key_Preferences.GET_FP_DETAILS_BUSINESS_NAME) ?: "")
        }
        TypeSuccess.CLOSE.name -> goBack()
      }
    }
    websiteUpdateSheet.show(baseActivity.supportFragmentManager, WebSiteThemeResetBottomSheet::javaClass.name)
  }

  private fun goBack() {
    baseActivity.onNavPressed()
  }


  private fun openWebViewDialog(url: String, title: String) {
    WebViewDialog().apply {
      setData(url, title)
      show(this@FragmentWebsiteTheme.parentFragmentManager, WebViewDialog::javaClass.name)
    }
  }

  private fun showPopupWindow(anchor: View) {
    val view = LayoutInflater.from(baseActivity).inflate(R.layout.popup_window_website_menu, null)
    this.popupWindow = PopupWindow(view, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, true)
    val needHelp = this.popupWindow?.contentView?.findViewById<CustomTextView>(R.id.need_help)
    val resetDefault = this.popupWindow?.contentView?.findViewById<CustomTextView>(R.id.reset_default)
    needHelp?.setOnClickListener {
      this.popupWindow?.dismiss()
    }
    resetDefault?.setOnClickListener {
      openResetDialog()
      this.popupWindow?.dismiss()
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) this.popupWindow?.elevation = 5.0F
    this.popupWindow?.showAsDropDown(anchor, 0, 0)
  }

  private fun openResetDialog() {
    val dialogReset = WebSiteThemeResetBottomSheet()
    dialogReset.onClicked = {
      when (it) {
        TypeSuccess.GO_BACK.name -> {
          dialogReset.dismiss()
          openResetConfirmation()
        }
        TypeSuccess.PUBLISH_CHANGES.name -> {
          dialogReset.dismiss()
          updateAPI(true)
        }
      }
    }
    val bundle = Bundle()
    bundle.putSerializable(IntentConstant.FONT_LIST.name, fonts)
    bundle.putSerializable(IntentConstant.COLOR_LIST.name, colors)
    dialogReset.arguments = bundle
    dialogReset.show(parentFragmentManager, WebSiteThemeResetBottomSheet::javaClass.name)
  }

  private fun openResetConfirmation() {
    val websiteThemePublishBottomSheet = WebSiteThemePublishBottomSheet()
    websiteThemePublishBottomSheet.onClicked = {
      when (it) {
        TypeSuccess.DISCARD.name -> {
          websiteThemePublishBottomSheet.dismiss()
          WebEngageController.trackEvent(WEBSITE_STYLE_CANCEL, CLICK, NO_EVENT_VALUE)

        }
        TypeSuccess.PUBLISH_CHANGES.name -> {
          websiteThemePublishBottomSheet.dismiss()
          updateAPI(false)
        }
      }
    }
    websiteThemePublishBottomSheet.show(
      parentFragmentManager,
      WebSiteThemePublishBottomSheet::javaClass.name
    )
  }
}