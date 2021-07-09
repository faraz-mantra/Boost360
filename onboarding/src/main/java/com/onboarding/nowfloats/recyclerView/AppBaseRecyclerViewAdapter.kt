package com.onboarding.nowfloats.recyclerView

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.framework.base.BaseActivity
import com.onboarding.nowfloats.R
import com.onboarding.nowfloats.constant.RecyclerViewItemType.*
import com.onboarding.nowfloats.databinding.*
import com.onboarding.nowfloats.holders.CityRecyclerViewHolder
import com.onboarding.nowfloats.holders.apiprocess.ApiProcessChannelRecyclerViewHolder
import com.onboarding.nowfloats.holders.apiprocess.ApiProcessRecyclerViewHolder
import com.onboarding.nowfloats.holders.category.CategoryRecyclerViewHolder
import com.onboarding.nowfloats.holders.channel.*
import com.onboarding.nowfloats.holders.common.SectionHeaderRecyclerViewHolder
import com.onboarding.nowfloats.holders.features.ChannelFeatureRecyclerViewHolder
import com.onboarding.nowfloats.holders.features.FeatureDetailsRecyclerViewHolder
import com.onboarding.nowfloats.holders.visitingCard.*


open class AppBaseRecyclerViewAdapter<T : AppBaseRecyclerViewItem>(
    activity: BaseActivity<*, *>,
    list: ArrayList<T>,
    itemClickListener: RecyclerItemClickListener? = null,
) : BaseRecyclerViewAdapter<T>(activity, list, itemClickListener) {

  override fun getViewHolder(parent: ViewGroup, viewType: Int): BaseRecyclerViewHolder<*> {
    val inflater = LayoutInflater.from(parent.context)
    val recyclerViewItemType = values().first { it.getLayout() == viewType }
    val binding = getViewDataBinding(inflater, recyclerViewItemType, parent)
    return when (recyclerViewItemType) {
      CATEGORY_ITEM -> CategoryRecyclerViewHolder(binding as ItemCategoryBinding)
      CHANNEL_ITEM -> ChannelRecyclerViewHolder(binding as ItemChannelBinding)
      FEATURE_ITEM -> ChannelFeatureRecyclerViewHolder(binding as ItemChannelFeatureBinding)
      CHANNEL_BOTTOM_SHEET_ITEM -> ChannelSelectorBottomSheetRecyclerViewHolder(binding as ItemChannelBottomSheetBinding)
      SECTION_HEADER_ITEM -> SectionHeaderRecyclerViewHolder(binding as ItemSectionHeaderBinding)
      FEATURE_DETAILS_BOTTOM_SHEET_ITEM -> FeatureDetailsRecyclerViewHolder(binding as ItemFeatureDetailsBottomSheetBinding)
      SELECTED_CHANNEL_ITEM -> ChannelSelectedRecyclerViewHolder(binding as ItemSelectedChannelBinding)
      SMALL_SELECTED_CHANNEL_ITEM -> ChannelSelectedSmallRecyclerViewHolder(binding as ItemSelectedChannelSmallBinding)
      API_PROCESS_BUSINESS_ITEM -> ApiProcessRecyclerViewHolder(binding as ItemApiCallingProcessBinding)
      API_PROCESS_CHANNEL_ITEM -> ApiProcessChannelRecyclerViewHolder(binding as ItemChildApiCallingBinding)
      CITY_DETAILS_ITEM -> CityRecyclerViewHolder(binding as CityItemDataBinding)
      CHANNEL_ITEM_CONNECT -> ChannelsConnectViewHolder(binding as ItemChannelsConnectedBinding)
      CHANNEL_ITEM_DISCONNECT -> ChannelsDisconnectViewHolder(binding as ItemChannelsDisconnectBinding)
      VISITING_CARD_ONE_ITEM -> VisitingCardOneViewHolder(binding as ItemVisitingCardOneBinding)
      VISITING_CARD_TWO_ITEM -> VisitingCardTwoViewHolder(binding as ItemVisitingCardTwoBinding)
      VISITING_CARD_THREE_ITEM -> VisitingCardThreeViewHolder(binding as ItemVisitingCardThreeBinding)
      VISITING_CARD_FOUR_ITEM -> VisitingCardFourViewHolder(binding as ItemVisitingCardFourBinding)
      VISITING_CARD_FIVE_ITEM -> VisitingCardFiveViewHolder(binding as ItemVisitingCardFiveBinding)
      VISITING_CARD_SIX_ITEM -> VisitingCardSixViewHolder(binding as ItemVisitingCardSixBinding)
      VISITING_CARD_SEVEN_ITEM -> VisitingCardSevenViewHolder(binding as ItemVisitingCardSevenBinding)
      VISITING_CARD_EIGHT_ITEM -> VisitingCardEightViewHolder(binding as ItemVisitingCardEightBinding)
      VISITING_CARD_NINE_ITEM -> VisitingCardNineViewHolder(binding as ItemVisitingCardNineBinding)
      VISITING_CARD_TEN_ITEM -> VisitingCardTenViewHolder(binding as ItemVisitingCardTenBinding)
    }
  }

  fun runLayoutAnimation(recyclerView: RecyclerView?, anim: Int = R.anim.layout_animation_fall_down) = recyclerView?.apply {
    layoutAnimation = AnimationUtils.loadLayoutAnimation(context, anim)
    notifyDataSetChanged()
    scheduleLayoutAnimation()
  }

  fun notify(list: ArrayList<T>?) {
    if (list != null) {
      this.list = list
      notifyDataSetChanged()
    }
  }
}