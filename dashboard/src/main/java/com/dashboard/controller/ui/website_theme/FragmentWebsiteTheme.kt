package com.dashboard.controller.ui.website_theme

import android.graphics.Color
import android.os.Bundle
import android.view.View
import com.appservice.model.SessionData
import com.dashboard.R
import com.dashboard.base.AppBaseFragment
import com.dashboard.constant.IntentConstant
import com.dashboard.constant.RecyclerViewActionType
import com.dashboard.controller.getDomainName
import com.dashboard.controller.ui.website_theme.bottomsheet.BottomSheetSelectFont
import com.dashboard.databinding.FragmentWebsiteThemeBinding
import com.dashboard.model.websitetheme.*
import com.dashboard.recyclerView.AppBaseRecyclerViewAdapter
import com.dashboard.recyclerView.BaseRecyclerViewItem
import com.dashboard.recyclerView.RecyclerItemClickListener
import com.dashboard.viewmodel.WebsiteThemeViewModel
import com.framework.extensions.observeOnce
import com.framework.pref.UserSessionManager
import com.framework.utils.fromHtml

class FragmentWebsiteTheme : AppBaseFragment<FragmentWebsiteThemeBinding, WebsiteThemeViewModel>(), RecyclerItemClickListener {
  override fun getLayout(): Int {
    return R.layout.fragment_website_theme
  }

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
    setOnClickListener(binding?.ctfPrimaryFont,binding?.ctfSecondaryFont)
    val sessionData = arguments?.get(com.appservice.constant.IntentConstant.SESSION_DATA.name) as? SessionData
    getWebsiteTheme(sessionData)
    setWebsiteData()

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
  }

  override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
    when (actionType) {
      RecyclerViewActionType.ITEM_COLOR_CLICK.ordinal -> {
        updateColors(item)
      }
    }
  }

  private fun updateColors(item: BaseRecyclerViewItem?) {
    showShortToast((item as? ColorsItem)?.primary)
    colors?.forEach { if (item != it) it.isSelected = false }
    binding?.rvColors?.post { adapter?.notifyDataSetChanged() }

  }

  private fun showPrimaryFontsBottomSheet(fontsList: ArrayList<PrimaryItem?>?) {
    val bundle = Bundle()
    bundle.putSerializable(IntentConstant.FONT_LIST_PRIMARY.name, fontsList)
    val bottomSheetSelectFont = BottomSheetSelectFont()
    bottomSheetSelectFont.arguments = bundle
    bottomSheetSelectFont.show(parentFragmentManager, BottomSheetSelectFont::javaClass.name)
  }

  private fun showSecondaryFontsBottomSheet(fontsList: ArrayList<SecondaryItem?>?) {
    val bundle = Bundle()
    val bottomSheetSelectFont = BottomSheetSelectFont()
    bundle.putSerializable(IntentConstant.FONT_LIST_SECONDARY.name, fontsList)
    bottomSheetSelectFont.arguments = bundle
    bottomSheetSelectFont.show(parentFragmentManager, BottomSheetSelectFont::javaClass.name)
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when(v){
      binding?.ctfPrimaryFont->{
        showPrimaryFontsBottomSheet(primaryFont as? ArrayList<PrimaryItem?>?)
      }
      binding?.ctfSecondaryFont->{
        showSecondaryFontsBottomSheet(secondaryFont as? ArrayList<SecondaryItem?>?)
      }
    }
  }
}