package com.boost.marketplace.infra.api.models.test

import com.boost.dbcenterapi.recycleritem.AppBaseRecyclerViewItem
import com.boost.dbcenterapi.recycleritem.RecyclerViewItemType
import com.framework.base.BaseResponse

data class ViewpagerData(var package_title:String?=null, var recyclerViewItemType: Int= RecyclerViewItemType.TOP_FEATURES.ordinal
) : BaseResponse(), AppBaseRecyclerViewItem {


    override fun getViewType(): Int {
        return recyclerViewItemType
    }
}

fun getDatas(recyclerViewItem: Int): ArrayList<ViewpagerData> {
    val datas = ViewpagerData(recyclerViewItemType = recyclerViewItem)
    return  arrayListOf(datas,datas,datas,datas,datas,datas,datas,datas,datas,datas,datas,datas,datas)
}

