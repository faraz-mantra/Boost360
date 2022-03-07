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

        return list
    }
}