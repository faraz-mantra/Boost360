package com.boost.dbcenterapi.infra.api.models.test

import com.boost.dbcenterapi.recycleritem.AppBaseRecyclerStringItem
import com.boost.dbcenterapi.recycleritem.RecyclerStringItemType
import com.framework.base.BaseResponse

data class StringData(
    var data: ArrayList<String>,
    var viewItemType: Int
) : BaseResponse(), AppBaseRecyclerStringItem {


    override fun getViewType(): Int {
        return viewItemType//RecyclerStringItemType.STRING_LIST.ordinal
    }
}