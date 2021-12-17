package dev.patrickgold.florisboard.customization.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.onboarding.nowfloats.databinding.*
import dev.patrickgold.florisboard.customization.viewholder.*
import dev.patrickgold.florisboard.customization.viewholder.visitingCard.*
import dev.patrickgold.florisboard.databinding.*
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList


open class SharedAdapter<T : BaseRecyclerItem?>(val list: ArrayList<T>, val listener: OnItemClickListener? = null) : RecyclerView.Adapter<BaseRecyclerViewHolder<*>>() {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseRecyclerViewHolder<*> {
    val inflater = LayoutInflater.from(parent.context)
    val itemType = FeaturesEnum.values()[viewType]
    val view = getViewDataBinding(inflater, itemType, parent)
    return when (itemType) {
      FeaturesEnum.LOADER -> LoaderViewHolder(view as PaginationLoaderKeyboardBinding, listener)
      FeaturesEnum.UPDATES -> FloatUpdateViewHolder(view as AdapterItemUpdateBinding, listener)
      FeaturesEnum.PRODUCTS -> ProductViewHolder(view as AdapterItemProductNewBinding, listener)
      FeaturesEnum.PHOTOS -> PhotoViewHolder(view as AdapterItemPhotosBinding, listener)
      FeaturesEnum.DETAILS -> DetailsViewHolder(view as AdapterItemDetailsBinding, listener)
      FeaturesEnum.VISITING_CARD_ONE_ITEM -> VisitingCardOneViewHolder(view as ItemVisitingCardOneBinding, listener)
      FeaturesEnum.VISITING_CARD_TWO_ITEM -> VisitingCardTwoViewHolder(view as ItemVisitingCardTwoBinding, listener)
      FeaturesEnum.VISITING_CARD_THREE_ITEM -> VisitingCardThreeViewHolder(view as ItemVisitingCardThreeBinding, listener)
      FeaturesEnum.VISITING_CARD_FOUR_ITEM -> VisitingCardFourViewHolder(view as ItemVisitingCardFourBinding, listener)
      FeaturesEnum.VISITING_CARD_FIVE_ITEM -> VisitingCardFiveViewHolder(view as ItemVisitingCardFiveBinding, listener)
      FeaturesEnum.VISITING_CARD_SIX_ITEM -> VisitingCardSixViewHolder(view as ItemVisitingCardSixBinding, listener)
      FeaturesEnum.VISITING_CARD_SEVEN_ITEM -> VisitingCardSevenViewHolder(view as ItemVisitingCardSevenBinding, listener)
      FeaturesEnum.VISITING_CARD_EIGHT_ITEM -> VisitingCardEightViewHolder(view as ItemVisitingCardEightBinding, listener)
      FeaturesEnum.VISITING_CARD_NINE_ITEM -> VisitingCardNineViewHolder(view as ItemVisitingCardNineBinding, listener)
      FeaturesEnum.VISITING_CARD_TEN_ITEM -> VisitingCardTenViewHolder(view as ItemVisitingCardTenBinding, listener)
      FeaturesEnum.STAFF_LISTING_VIEW -> StaffProfileViewHolder(view as ItemStaffProfileBinding, listener)
      FeaturesEnum.MORE_ACTION_VIEW_ITEM -> MoreActionHolder(view as ItemActionMoreBinding, listener)
      FeaturesEnum.MORE_FIRST_VIEW_ITEM -> MoreFirstItemHolder(view as ItemMoreFirstBinding, listener)
      FeaturesEnum.MORE_SECOND_VIEW_ITEM -> MoreSecondItemHolder(view as ItemMoreSecondBinding, listener)
    }
  }

  override fun onBindViewHolder(holder: BaseRecyclerViewHolder<*>, position: Int) {
    holder.bindTo(position, list[position])
  }

  override fun getItemViewType(position: Int): Int {
    return list[position]?.getViewType() ?: FeaturesEnum.LOADER.ordinal
  }

  override fun getItemCount(): Int = list.size

  open fun addItems(newList: List<T>) {
    if (list != newList) list.addAll(newList)
    notifyDataSetChanged()
  }

  fun notifyNewList(newList: List<T>) {
    this.list.clear()
    this.list.addAll(newList)
    notifyDataSetChanged()
  }

  open fun addLoadingFooter(t: T) {
    list.add(t)
    notifyItemInserted(list.size - 1)
  }

  open fun removeLoader() {
    val position = list.size - 1
    if (position > -1) {
      val item: T? = getItem(position)
      if (item != null && item.getViewType() == FeaturesEnum.LOADER.ordinal) {
        list.removeAt(position)
        notifyItemRemoved(position)
      }
    }
  }

  open fun getItem(position: Int): T? {
    return list[position]
  }

  fun clearList() {
    if (list.isNullOrEmpty().not()) {
      val size = list.size
      list.clear()
      notifyItemRangeRemoved(0, size)
    }
  }

  fun getListData(): ArrayList<T> {
    return list
  }
}

fun getViewDataBinding(inflater: LayoutInflater, recyclerViewItemType: FeaturesEnum, parent: ViewGroup): ViewDataBinding {
  return DataBindingUtil.inflate(inflater, recyclerViewItemType.getLayout(), parent, false)
}