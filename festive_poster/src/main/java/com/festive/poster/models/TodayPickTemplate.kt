package com.festive.poster.models

import com.festive.poster.constant.RecyclerViewItemType
import com.festive.poster.recyclerView.AppBaseRecyclerViewItem
import kotlinx.android.parcel.Parcelize

@Parcelize
open class TodayPickTemplate(
    private val _id: String,
    private val _isFavourite: Boolean,
    private val _name: String,
    private val _primarySvgUrl: String,
    private val _primaryText: String,
    private val _tags: List<String>,
    private val _utilizationDate: String?,
    private val _categoryId:String,
    private val _favDate:Long,
    private val _isFeatured:Boolean
):TemplateUi(_id,_isFavourite,_name,_primarySvgUrl,
    _primaryText,_tags,_utilizationDate,_categoryId,_favDate, _isFeatured),AppBaseRecyclerViewItem{
    override fun getViewType(): Int {
        return RecyclerViewItemType.TEMPLATE_VIEW_FOR_TODAY_PICK.getLayout()
    }

}