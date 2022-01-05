package com.boost.marketplace.infra.api.models.test

import com.boost.dbcenterapi.recycleritem.AppBaseRecyclerViewItem
import com.boost.dbcenterapi.recycleritem.RecyclerViewItemType
import com.framework.base.BaseResponse

data class TestData(
    var title: String? = null,
    var recyclerViewItemType: Int = RecyclerViewItemType.FEATURE_DETAILS.ordinal
) : BaseResponse(), AppBaseRecyclerViewItem {


    override fun getViewType(): Int {
        return recyclerViewItemType
    }
}

fun getData(recyclerViewItem: Int): ArrayList<TestData> {
    val test = TestData(recyclerViewItemType = recyclerViewItem)
    return  arrayListOf(test,test,test,test,)
}