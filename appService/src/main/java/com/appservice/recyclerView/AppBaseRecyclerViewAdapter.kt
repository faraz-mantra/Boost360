package com.appservice.recyclerView

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.appservice.R
import com.appservice.constant.RecyclerViewItemType.*
import com.appservice.databinding.*
import com.appservice.holder.*
import com.framework.base.BaseActivity

open class AppBaseRecyclerViewAdapter<T : AppBaseRecyclerViewItem>(
  activity: BaseActivity<*, *>,
  list: ArrayList<T>,
  itemClickListener: RecyclerItemClickListener? = null
) : BaseRecyclerViewAdapter<T>(activity, list, itemClickListener) {


  override fun getViewHolder(parent: ViewGroup, viewType: Int): BaseRecyclerViewHolder<*> {
    val inflater = LayoutInflater.from(parent.context)
    val recyclerViewItemType = values().first { it.getLayout() == viewType }
    val binding = getViewDataBinding(inflater, recyclerViewItemType, parent)
    return when (recyclerViewItemType) {
      PAGINATION_LOADER -> PagingViewHolder(binding as PaginationLoaderBinding)
      SPECIFICATION_ITEM -> SpecificationViewHolder(binding as RowLayoutAddedSpecsBinding)
      IMAGE_PREVIEW -> ImagePreviewViewHolder(binding as ItemPreviewImageBinding)
      GST_DETAILS_VIEW -> GstDetailViewHolder(binding as ItemGstDetailBinding)
      ADDITIONAL_FILE_VIEW -> AdditionalFileViewHolder(binding as ItemPdfFileBinding)
      SESSION_ITEM_VIEW -> WeeklyAppointmentViewHolder(binding as RecyclerItemSessionBinding)
      SERVICE_TIMING_ITEM_VIEW -> ServiceTimingViewHolder(binding as ItemServiceTimingBinding)
      UPDATE_BUSINESS_ITEM_VIEW -> UpdateBusinessViewHolder(binding as ItemUpdatesListBinding)
      CREATE_CATEGORY_ITEM_VIEW -> CreateCategoryViewHolder(binding as ItemCreateCategoryBinding)
      SERVICE_ITEM_VIEW -> StaffServiceViewHolder(binding as RecyclerItemServiceBinding)
      EXPERIENCE_RECYCLER_ITEM -> StaffExperienceViewHolder(binding as ItemExperienceDetailsBinding)
      STAFF_LISTING_VIEW -> StaffListingViewHolder(binding as RecyclerItemStaffListingBinding)
      STAFF_FILTER_VIEW -> StaffFilterViewHolder(binding as RecyclerItemStaffFilterBinding)
      SERVICE_LISTING_VIEW -> ServiceListingViewHolder(binding as RecyclerItemServiceListingBinding)
      OFFER_LISTING_VIEW -> OfferListingViewHolder(binding as RecyclerItemOfferBinding)
      OFFER_SELECT_SERVICES -> SelectOfferListingViewHolder(binding as RecyclerItemServiceSelectOfferBinding)
      SERVICE_CATEGORY_ITEM_VIEW -> ServiceCategoryViewHolder(binding = binding as RecyclerItemServiceCategoryBinding)
      PRODUCT_CATEGORY_ITEM_VIEW -> ProductCategoryViewHolder(binding = binding as RecyclerItemProductCategoryBinding)
      PRODUCT_LISTING -> ProductListingViewHolder(binding = binding as RecyclerItemProductListingBinding)
      CATALOG_SETTING_TILES -> CatalogTileViewHolder(binding as RecyclerItemEcomAptSettingsBinding)
      DOMAIN_STEPS -> DomainStepsViewHolder(binding as ListItemStepsDomainBinding)
      DOMAIN_NAME_SUGGESTIONS -> DomainSuggestionsViewHolder(binding as ItemDomainSuggestionsBinding)
      SIMILAR_DOMAIN_SUGGESTIONS -> SimilarDomainSuggestionViewHolder(binding as ItemSimilarDomainSuggestionsBinding)
      BACKGROUND_IMAGE_RV -> BackgroundImageHolder(binding = binding as ListItemBackgroundImagesBinding)
      BACKGROUND_IMAGE_FULL_SCREEN -> BGImageFullScreenHolder(binding = binding as ListItemBgImageFullScreenBinding)
      GST_SLAB_SETTING -> GstSlabViewHolder(binding as ItemGstSlabBinding)
      VMN_CALL -> VmnCallViewHolder(binding as SingleItemVmnCallItemV2Binding)
      TESTIMONIAL_ITEM -> TestimonialViewHolder(binding as ItemTestimonialListBinding)
      PAST_UPDATE_ITEM->PastUpdateViewHolder(binding as ListItemPastUpdateBinding)
      PAST_POST_CATEGORIES->PastCategoryViewHolder(binding as ListItemPastCategoryBinding)
      PAST_TAGS->PastTagsViewHolder(binding as ListItemPastTagsBinding)
      PAST_SOCIAL_ICON_LIST_ITEM->PastSocialIconViewHolder(binding as ListItemSocialIconBinding)
    }
  }

  fun runLayoutAnimation(
    recyclerView: RecyclerView?,
    anim: Int = R.anim.layout_animation_fall_down
  ) = recyclerView?.apply {
    layoutAnimation = AnimationUtils.loadLayoutAnimation(context, anim)
    notifyDataSetChanged()
    scheduleLayoutAnimation()
  }

  override fun getItemViewType(position: Int): Int {
    return if (isLoaderVisible) {
      return if (position == list.size - 1) PAGINATION_LOADER.getLayout() else super.getItemViewType(
        position
      )
    } else super.getItemViewType(position)
  }

  fun notify(list: ArrayList<T>?) {
    list?.let { updateList(it) }
  }

  open fun addItems(addList: ArrayList<T>?) {
    addList?.let { list.addAll(it) }
    notifyDataSetChanged()
  }

  override fun getItemCount(): Int {
    return if (list.isNotEmpty()) list.size else 0
  }


  open fun remove(item: T) {
    val position = list.indexOf(item)
    if (position > -1) {
      list.removeAt(position)
      notifyItemRemoved(position)
    }
  }

  open fun clear() {
    isLoaderVisible = false
    while (itemCount > 0) {
      getItem(0)?.let { remove(it) }
    }
  }

  open fun isEmpty(): Boolean {
    return itemCount == 0
  }

  open fun addLoadingFooter(t: T) {
    isLoaderVisible = true
    list.add(t)
    notifyItemInserted(list.size - 1)
  }

  open fun removeLoadingFooter() {
    isLoaderVisible = false
    val position = list.size - 1
    if (position > -1) {
      val item: T? = getItem(position)
      if (item != null && item.getViewType() == PAGINATION_LOADER.getLayout()) {
        list.removeAt(position)
        notifyItemRemoved(position)
      }
    }
  }

  open fun getItem(position: Int): T? {
    return list[position]
  }

  open fun list(): ArrayList<T> {
    return list
  }
}