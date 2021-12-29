package com.boost.marketplace.infra.api.models.test

import com.boost.dbcenterapi.recycleritem.AppBaseRecyclerViewItem
import com.boost.dbcenterapi.recycleritem.RecyclerViewItemType
import com.framework.base.BaseResponse

data class Packs_Data(var details:String?=null, var recyclerViewItemType: Int= RecyclerViewItemType.PACKS_BUNDLES.ordinal
) : BaseResponse(), AppBaseRecyclerViewItem {


    override fun getViewType(): Int {
        return recyclerViewItemType
    }
}

fun getDatas1(recyclerViewItem: Int): ArrayList<ViewpagerData> {
    val datas = ViewpagerData(recyclerViewItemType = recyclerViewItem)
    return  arrayListOf(datas,datas,datas)
}