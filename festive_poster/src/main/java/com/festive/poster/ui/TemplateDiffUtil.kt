package com.festive.poster.ui

import androidx.recyclerview.widget.DiffUtil
import com.festive.poster.models.TemplateUi

class TemplateDiffUtil(
    val oldList: List<TemplateUi>,
    val newList:List<TemplateUi>
): DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
       return oldList[oldItemPosition].id==newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
       val old =  oldList[oldItemPosition]
        val new = newList[newItemPosition]
        return (old.id==new.id
                && old.isFavourite==new.isFavourite
                && old.primaryText==new.primaryText
                &&old.primarySvgUrl==new.primarySvgUrl
                && old.name==new.name)
    }
}