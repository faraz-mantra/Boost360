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


class SharedAdapter<T : BaseRecyclerItem?>(val list: ArrayList<T?>, val listener: OnItemClickListener? = null) : RecyclerView.Adapter<BaseRecyclerViewHolder<*>>() {

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
    }
  }

  override fun onBindViewHolder(holder: BaseRecyclerViewHolder<*>, position: Int) {
    holder.bindTo(position, list[position])
  }

  override fun getItemViewType(position: Int): Int {
    return list[position]?.getViewType() ?: FeaturesEnum.LOADER.ordinal
  }

  override fun getItemCount(): Int = list.size

  fun submitList(newList: List<T>, hasMoreItems: Boolean = false) {
    if (list != newList) {
      // list is not same, then append new items
      list.addAll(newList)
    }
    // for pagination
    if (hasMoreItems) {
      list.add(null)
      notifyItemRangeInserted(list.size, newList.size + 1)
    } else {
      notifyItemRangeInserted(list.size, newList.size)
    }
  }

  fun removeLoader() {
    try {
      // get last item
      val item = list[list.size - 1]
      // if last item is null then it was added for pagination
      if (item == null) {
        list.removeLast()
      }
    } catch (e: NoSuchElementException) {
      Timber.i("List is empty")
    } catch (e: IndexOutOfBoundsException) {
      Timber.i("tried to remove item with no items present in it")
    } catch (e: Exception) {
      Timber.e(e)
    }
  }

  fun clearList() {
    list.clear()
    notifyDataSetChanged()
  }
}

fun getViewDataBinding(inflater: LayoutInflater, recyclerViewItemType: FeaturesEnum, parent: ViewGroup): ViewDataBinding {
  return DataBindingUtil.inflate(inflater, recyclerViewItemType.getLayout(), parent, false)
}