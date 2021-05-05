package com.dashboard.controller.ui.website_theme

import android.graphics.Color
import android.os.Bundle
import android.view.View
import com.appservice.model.SessionData
import com.dashboard.R
import com.dashboard.base.AppBaseFragment
import com.dashboard.constant.IntentConstant
import com.dashboard.constant.RecyclerViewActionType
import com.dashboard.controller.DashboardFragmentContainerActivity
import com.dashboard.controller.getDomainName
import com.dashboard.controller.ui.website_theme.bottomsheet.BottomSheetSelectFont
import com.dashboard.controller.ui.website_theme.bottomsheet.TypeSuccess
import com.dashboard.controller.ui.website_theme.bottomsheet.WebsiteThemeUpdatedSuccessfullyBottomsheet
import com.dashboard.controller.ui.website_theme.dialog.WebViewDialog
import com.dashboard.databinding.FragmentWebsiteThemeBinding
import com.dashboard.model.websitetheme.*
import com.dashboard.recyclerView.AppBaseRecyclerViewAdapter
import com.dashboard.recyclerView.BaseRecyclerViewItem
import com.dashboard.recyclerView.RecyclerItemClickListener
import com.dashboard.viewmodel.WebsiteThemeViewModel
import com.framework.extensions.gone
import com.framework.extensions.observeOnce
import com.framework.extensions.visible
import com.framework.pref.UserSessionManager
import com.framework.utils.fromHtml

class FragmentWebsiteTheme : AppBaseFragment<FragmentWebsiteThemeBinding, WebsiteThemeViewModel>(), RecyclerItemClickListener {
  override fun getLayout(): Int {
    return R.layout.fragment_website_theme
  }

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

  override fun getViewModelClass(): Class<WebsiteThemeViewModel> {
    return WebsiteThemeViewModel::class.java
  }

  override fun onCreateView() {
    super.onCreateView()
    setOnClickListener(binding?.ctfPrimaryFont, binding?.ctfSecondaryFont, binding?.btnDone, binding?.btnCancel)
    this.sessionData = arguments?.get(com.appservice.constant.IntentConstant.SESSION_DATA.name) as? SessionData
    getWebsiteTheme(sessionData)
    setWebsiteData()
    hideActionButtons()

  }

  private fun setWebsiteData() {
    binding?.ctvWebsite?.text = fromHtml("<u>${UserSessionManager(requireActivity()).getDomainName()}</u>")

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
      } else {
        error(getString(R.string.error_while_getting_website_theme))
      }
    })
  }

  private fun setColor() {
    val defaultColorCode = colors?.filter { it.defaultColor == true }?.getOrNull(0)?.primary
    binding?.rivDefaultColor?.setBackgroundColor(Color.parseColor(defaultColorCode))
    binding?.rvColors?.setHasFixedSize(true)
    this.adapter = AppBaseRecyclerViewAdapter(baseActivity, colors!!, this@FragmentWebsiteTheme)
    binding?.rvColors?.adapter = adapter
  }

  private fun setFonts() {
    //set primary font
    this.primaryFont = fonts?.primary
    //set secondary font
    this.secondaryFont = fonts?.secondary
    val primaryFilter = primaryFont?.filter { it?.defaultFont!! }
    val secondaryFilter = secondaryFont?.filter { it?.defaultFont!! }
    binding?.ctfPrimaryFont?.setText(primaryFilter?.getOrNull(0)?.description
        ?: primaryFont?.get(0)?.description)
    binding?.ctfSecondaryFont?.setText(secondaryFilter?.getOrNull(0)?.description
        ?: secondaryFont?.get(0)?.description)
    val primary = primaryFont?.filter { it?.defaultFont!! }
    if (primary.isNullOrEmpty()) primaryFont!![0]?.isSelected = true
    val secondary = secondaryFont?.filter { it?.defaultFont!! }
    if (secondary.isNullOrEmpty()) secondaryFont!![0]?.isSelected = true
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
    val bundle = Bundle()
    bundle.putSerializable(IntentConstant.FONT_LIST_PRIMARY.name, fontsList)
    val bottomSheetSelectFont = BottomSheetSelectFont()
    bottomSheetSelectFont.arguments = bundle
    bottomSheetSelectFont.onPrimaryClicked = {
      this.primaryItem = it
      binding?.ctfPrimaryFont?.setText(it.description)
      showActionButtons()
    }
    bottomSheetSelectFont.show(parentFragmentManager, BottomSheetSelectFont::javaClass.name)
  }
  private fun showActionButtons(){
    binding?.btnCancel?.visible()
    binding?.btnDone?.visible()
  }
  private fun hideActionButtons(){
    binding?.btnCancel?.gone()
    binding?.btnDone?.gone()
  }

  private fun showSecondaryFontsBottomSheet(fontsList: ArrayList<SecondaryItem?>?) {
    val bundle = Bundle()
    val bottomSheetSelectFont = BottomSheetSelectFont()
    bundle.putSerializable(IntentConstant.FONT_LIST_SECONDARY.name, fontsList)
    bottomSheetSelectFont.arguments = bundle
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
      binding?.btnCancel -> {
        goBack()
      }
      binding?.btnDone -> {
        updateAPI()
      }
    }
  }

  private fun updateAPI() {
    showProgress()
    viewModel?.updateWebsiteTheme(WebsiteThemeUpdateRequest(Customization(Colors(colorsItem?.secondary, colorsItem?.tertiary,
        colorsItem?.primary, colorsItem?.name), Fonts(secondaryItem
        ?: secondaryFont?.get(0), primaryItem
        ?: primaryFont?.get(0))), floatingPointId = sessionData?.fpId))?.observeOnce(viewLifecycleOwner, {
      hideProgress()
      if (it.isSuccess()) {
        openSuccessDialog()
      } else {
        error(getString(R.string.something_went_wrong))
      }
    })
  }

  private fun openSuccessDialog() {
    val websiteThemeUpdatedSuccessfullyBottomsheet = WebsiteThemeUpdatedSuccessfullyBottomsheet()
    websiteThemeUpdatedSuccessfullyBottomsheet.onClicked = {
      when (it) {
        TypeSuccess.VISIT_WEBSITE.name -> {
          val domainName = UserSessionManager(requireActivity()).getDomainName()!!
          openWebViewDialog(domainName, domainName)
        }
        TypeSuccess.CLOSE.name -> {
          goBack()
        }
      }
    }
    websiteThemeUpdatedSuccessfullyBottomsheet.show(parentFragmentManager, WebsiteThemeUpdatedSuccessfullyBottomsheet::javaClass.name)
  }

  private fun goBack() {
    (requireActivity() as DashboardFragmentContainerActivity).onNavPressed()
  }

  private fun openWebViewDialog(url: String, title: String) {
    WebViewDialog().apply {
      setData(url, title)
      show(this@FragmentWebsiteTheme.parentFragmentManager, WebViewDialog::javaClass.name)
    }
  }
}