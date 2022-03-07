package com.dashboard.model

import com.dashboard.constant.RecyclerViewItemType
import com.dashboard.recyclerView.AppBaseRecyclerViewItem

class DemoToDoListCardsModel(
    var recyclerViewItemType: RecyclerViewItemType? = null
) : AppBaseRecyclerViewItem {

    override fun getViewType(): Int {
        return recyclerViewItemType?.getLayout()!!
    }

    fun getData(): ArrayList<DemoToDoListCardsModel> {
        val list = ArrayList<DemoToDoListCardsModel>()
        list.add(DemoToDoListCardsModel(RecyclerViewItemType.OPTIONAL_TASKS_VIEW))
        list.add(DemoToDoListCardsModel(RecyclerViewItemType.POST_PURCHASE_1_VIEW))
        list.add(DemoToDoListCardsModel(RecyclerViewItemType.POST_PURCHASE_2_VIEW))
        list.add(DemoToDoListCardsModel(RecyclerViewItemType.READINESS_SCORE_1_VIEW))
        list.add(DemoToDoListCardsModel(RecyclerViewItemType.READINESS_SCORE_2_VIEW))
        list.add(DemoToDoListCardsModel(RecyclerViewItemType.RENEWAL_1_VIEW))
        list.add(DemoToDoListCardsModel(RecyclerViewItemType.RENEWAL_2_VIEW))
        list.add(DemoToDoListCardsModel(RecyclerViewItemType.RENEWAL_3_VIEW))
        list.add(DemoToDoListCardsModel(RecyclerViewItemType.NEW_SINGLE_FEATURE_VIEW))
        list.add(DemoToDoListCardsModel(RecyclerViewItemType.NEW_MULTIPLE_FEATURE_VIEW))
        list.add(DemoToDoListCardsModel(RecyclerViewItemType.CONTINUE_WHERE_LEFT_VIEW))
        list.add(DemoToDoListCardsModel(RecyclerViewItemType.MISSED_CALL_1_VIEW))
        list.add(DemoToDoListCardsModel(RecyclerViewItemType.MISSED_CALL_2_VIEW))
        list.add(DemoToDoListCardsModel(RecyclerViewItemType.MISSED_CALL_3_VIEW))
        list.add(DemoToDoListCardsModel(RecyclerViewItemType.MISSED_CALL_4_VIEW))
        list.add(DemoToDoListCardsModel(RecyclerViewItemType.ORDER_DETAILS_VIEW))

        return list
    }
}