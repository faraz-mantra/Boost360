package com.onboarding.nowfloats.ui.channel

import SectionsFeature
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.framework.extensions.visible
import com.framework.utils.ConversionUtils
import com.framework.utils.ScreenUtils
import com.onboarding.nowfloats.R
import com.onboarding.nowfloats.base.AppBaseFragment
import com.onboarding.nowfloats.bottomsheet.builder.BottomDialog
import com.onboarding.nowfloats.bottomsheet.builder.channelMutableList
import com.onboarding.nowfloats.bottomsheet.builder.featureMutableList
import com.onboarding.nowfloats.bottomsheet.builder.oneButton
import com.onboarding.nowfloats.bottomsheet.contentHeader
import com.onboarding.nowfloats.bottomsheet.util.ObservableList
import com.onboarding.nowfloats.constant.FragmentType
import com.onboarding.nowfloats.constant.IntentConstant
import com.onboarding.nowfloats.constant.RecyclerViewActionType
import com.onboarding.nowfloats.constant.RecyclerViewItemType
import com.onboarding.nowfloats.databinding.FragmentChannelPickerBinding
import com.onboarding.nowfloats.extensions.addInt
import com.onboarding.nowfloats.extensions.fadeIn
import com.onboarding.nowfloats.managers.NavigatorManager
import com.onboarding.nowfloats.model.RequestFloatsModel
import com.onboarding.nowfloats.model.category.CategoryDataModel
import com.onboarding.nowfloats.model.channel.*
import com.onboarding.nowfloats.model.navigator.ScreenModel
import com.onboarding.nowfloats.model.navigator.ScreenModel.Screen.CHANNEL_SELECT
import com.onboarding.nowfloats.recyclerView.AppBaseRecyclerViewAdapter
import com.onboarding.nowfloats.recyclerView.BaseRecyclerViewItem
import com.onboarding.nowfloats.recyclerView.RecyclerItemClickListener
import com.onboarding.nowfloats.ui.startFragmentActivity
import com.onboarding.nowfloats.viewmodel.channel.ChannelPlanViewModel

class ChannelPickerFragment : AppBaseFragment<FragmentChannelPickerBinding, ChannelPlanViewModel>(),
  RecyclerItemClickListener {

  private var requestFloatsModel: RequestFloatsModel? = null
  private var channelFeaturesAdapter: AppBaseRecyclerViewAdapter<SectionsFeature>? = null
  private var channelAdapter: AppBaseRecyclerViewAdapter<ChannelModel>? = null
  private val channelList: ArrayList<ChannelModel> = ArrayList()
  private val selectedChannels: ArrayList<ChannelModel>
    get() {
      return ArrayList(channelList.let { it.filter { it1 -> it1.isSelected == true } })
    }
  private val responseFeatures: ArrayList<SectionsFeature>?
    get() {
      return categoryDataModel?.sections
    }

  private val categoryDataModel: CategoryDataModel?
    get() {
      return requestFloatsModel?.categoryDataModel
    }


  companion object {
    @JvmStatic
    fun newInstance(bundle: Bundle? = null): ChannelPickerFragment {
      val fragment = ChannelPickerFragment()
      fragment.arguments = bundle
      return fragment
    }
  }

  override fun getLayout(): Int {
    return R.layout.fragment_channel_picker
  }

  override fun getViewModelClass(): Class<ChannelPlanViewModel> {
    return ChannelPlanViewModel::class.java
  }

  fun updateBundleArguments(arguments: Bundle?) {
    this.arguments = arguments
    requestFloatsModel = NavigatorManager.getRequest()
    requestFloatsModel?.categoryDataModel?.getChannelList()?.let { channelList.addAll(it) }
    binding?.categorySelectedDesc?.text =
      resources.getString(R.string.string_for) + categoryDataModel?.category_descriptor
  }

  override fun onCreateView() {
    setOnClickListener(binding?.next, binding?.editContainer)
    binding?.channelList?.setOnClickListener { openChannelSelectionSheet() }
  }


  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.next -> {
        selectedChannels.let { channels ->
          if (channels.isEmpty()) {
            showShortToast(resources.getString(R.string.at_least_one_channel_selected))
            return
          }
          onChannelConfirmed()
//                    ChannelConfirmDialog().apply {
//                        setCount(channels.count())
//                        isCancelable = true
//                        setOnConfirmClick(this@ChannelPickerFragment::onChannelConfirmed)
//                        show(this@ChannelPickerFragment.parentFragmentManager, "")
//                    }
        }
      }
      binding?.editContainer -> openChannelSelectionSheet()
    }
  }

  override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
    when (actionType) {
      RecyclerViewActionType.FEATURE_ITEM_CLICKED.ordinal -> openFeatureDetailSheet(item as? SectionsFeature)
    }
  }

  private fun openChannelSelectionSheet() {
    channelList.let { channels ->
      val list = ObservableList.build<ChannelModel> { channels.forEach { add(it) } }
      BottomDialog.builder(baseActivity) {
        expandable = false
        peekHeightProportion = .8f
        mCancelable = false
        contentHeader(
          "${resources.getString(R.string.recommended_on)} ${list.size} ${
            resources.getString(
              R.string.channel
            )
          }", true
        )
        channelMutableList(list) { _, position, item, isType ->
          val action =
            if (isType) RecyclerViewActionType.CHANNEL_ITEM_CLICKED.ordinal else RecyclerViewActionType.CHANNEL_ITEM_WHY_CLICKED.ordinal
          onItemClickBottomSheet(list, position, item, action)
        }
        oneButton(
          resources.getString(R.string.done),
          fadDuration = 1500L,
          drwableId = R.drawable.bg_button_orange,
          autoDismiss = true
        ) { onClick { onChannelSelected() } }
      }
    }
  }

  private fun onItemClickBottomSheet(
    list: ObservableList<ChannelModel>,
    position: Int,
    item: ChannelModel,
    actionType: Int
  ) {
    when (actionType) {
      RecyclerViewActionType.CHANNEL_ITEM_WHY_CLICKED.ordinal -> openWhyChannelDialog(item as? ChannelModel)
      RecyclerViewActionType.CHANNEL_ITEM_CLICKED.ordinal -> {
        if (position != 0) {
//          if (list[position].isFacebookShop()) {
//            showLongToast("You can't connect to Facebook shop using app.")
//            return
//          }
          val isSelected = !list[position].isSelected!!
          list[position].isSelected = isSelected
          if (list[position].isFacebookPage()) {
            if (isSelected.not()) {
              val isShop = list.isFbPageOrShop(ChannelType.FB_SHOP)
              if (isShop != null && isShop.isSelected!!) isShop.isSelected = !isShop.isSelected!!
            }
          } else if (list[position].isFacebookShop()) {
            if (isSelected) {
              val isPage = list.isFbPageOrShop(ChannelType.FB_PAGE)
              if (isPage != null && isPage.isSelected!!.not()) isPage.isSelected =
                !isPage.isSelected!!
            }
          }
          channelList.clear()
          channelList.addAll(list)
          list.clear()
          list.addAll(ObservableList.build { channelList.forEach { add(it) } })
        } else openWhyChannelDialog(item as? ChannelModel)
      }
    }
  }

  private fun openWhyChannelDialog(channelModel: ChannelModel?) {
    ChannelWhyDialog().apply {
      setChannels(channelModel)
      show(this@ChannelPickerFragment.parentFragmentManager, "")
    }
  }

  private fun onChannelSelected() {
    val selectedChannels = channelList.map {
      it.recyclerViewType = RecyclerViewItemType.CHANNEL_ITEM.getLayout(); it
    }.filter { it.isSelected!! }
    setChannelAdapter(ArrayList(selectedChannels))
  }

  private fun openFeatureDetailSheet(feature: SectionsFeature?) {
    feature?.let {
      BottomDialog.builder(baseActivity) {
        expandable = false
        peekHeightProportion = .8f
        contentHeader(it, true)
        featureMutableList(it)
        oneButton(
          resources.getString(R.string.okay),
          fadDuration = 1500L,
          drwableId = R.drawable.bg_button_orange,
          autoDismiss = true
        )
      }
    }
  }

  private fun setChannelFeaturesAdapter(list: ArrayList<SectionsFeature>) {
    binding?.featureList?.post {
      channelFeaturesAdapter = AppBaseRecyclerViewAdapter(baseActivity, list, this)
      binding?.featureList?.layoutManager = LinearLayoutManager(baseActivity)
      binding?.featureList?.adapter = channelFeaturesAdapter
      binding?.featureList?.let { channelFeaturesAdapter?.runLayoutAnimation(it) }
    }
  }

  private fun setChannelAdapter(channels: ArrayList<ChannelModel>, animate: Boolean = true) {
    channelAdapter = AppBaseRecyclerViewAdapter(baseActivity, channels, this)
    val recyclerViewWidth =
      ScreenUtils.instance.getWidth(baseActivity) - ConversionUtils.dp2px(32f, 20f, 24f, 16f, 16f)
    val itemWidth = ConversionUtils.dp2px(44f)
    val spanCount = recyclerViewWidth / itemWidth
    binding?.channelList?.layoutManager = GridLayoutManager(baseActivity, spanCount)
    binding?.channelList?.adapter = channelAdapter
    binding?.channelList?.let {
      if (animate) channelAdapter?.runLayoutAnimation(it)
      else channelAdapter?.notifyDataSetChanged()
    }
    val text = StringBuilder(
      resources.getString(R.string.presence_on) + " ${channels.size} " + resources.getString(R.string.channel)
    )
    if (channels.size > 1 || channels.size == 0) text.append(resources.getString(R.string.more_than_one_add_s))
    binding?.channelPresence?.text = text
  }

  private fun onChannelConfirmed() {
    val bundle = Bundle()
    var totalPages = if (requestFloatsModel?.isUpdate == true) 0 else 2
    selectedChannels.let { channels ->
      if (channels.haveGoogleBusinessChannel()) totalPages++
      if (channels.haveFacebookShop()) totalPages++
      if (channels.haveFacebookPage()) totalPages++
      if (channels.haveTwitterChannels()) totalPages++
      if (channels.haveWhatsAppChannels()) totalPages++
    }
    requestFloatsModel?.channels = ArrayList(selectedChannels)
    NavigatorManager.pushToStackAndSaveRequest(
      ScreenModel(CHANNEL_SELECT, getToolbarTitle()),
      requestFloatsModel
    )
    bundle.addInt(IntentConstant.TOTAL_PAGES, totalPages).addInt(IntentConstant.CURRENT_PAGES, 1)
    if (requestFloatsModel?.isUpdate == false) {
      startFragmentActivity(FragmentType.REGISTRATION_BUSINESS_BASIC_DETAILS, bundle)
    } else {
      val channels = requestFloatsModel?.channels ?: return
      when {
        channels.haveFacebookPage() -> startFragmentActivity(
          FragmentType.REGISTRATION_BUSINESS_FACEBOOK_PAGE,
          bundle
        )
        channels.haveFacebookShop() -> startFragmentActivity(
          FragmentType.REGISTRATION_BUSINESS_FACEBOOK_SHOP,
          bundle
        )
        channels.haveTwitterChannels() -> startFragmentActivity(
          FragmentType.REGISTRATION_BUSINESS_TWITTER_DETAILS,
          bundle
        )
        channels.haveWhatsAppChannels() -> startFragmentActivity(
          FragmentType.REGISTRATION_BUSINESS_WHATSAPP,
          bundle
        )
      }
    }
  }


  fun startAnimationChannelFragment() {
//    for (channel in categoryDataModel?.channels ?: ArrayList()) {
//      channelList.firstOrNull { it.getName() == channel.getName() }?.isSelected = true
//    }
    binding?.viewChannel?.post {
//      setChannelAdapter(selectedChannels, animate = false)
      binding?.viewChannel?.fadeIn(0L)?.doOnComplete {
        binding?.next?.visible()
        responseFeatures?.let { setChannelFeaturesAdapter(it) }
      }?.andThen(binding?.next?.fadeIn(200L))?.subscribe()
    }
  }
}

