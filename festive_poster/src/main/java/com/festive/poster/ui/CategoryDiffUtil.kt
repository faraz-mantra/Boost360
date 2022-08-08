package com.festive.poster.ui

import androidx.recyclerview.widget.DiffUtil
import com.festive.poster.models.CategoryUi
import com.festive.poster.models.TemplateUi
import kotlin.math.min

class CategoryDiffUtil(
    val oldList: List<CategoryUi>,
    val newList:List<CategoryUi>
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
        val old = oldList[oldItemPosition]
        val new = newList[newItemPosition]

        if (old.getParentTemplates().isNullOrEmpty().not() &&
            new.getParentTemplates().isNullOrEmpty().not()
        ) {

            for (i in 0 until (min(
                old.getParentTemplates()?.size ?: 0,
                new.getParentTemplates()?.size ?: 0
            ))) {
                val oldTemp = old.getParentTemplates()!![i]
                val newTemp = new.getParentTemplates()!![i]
                if ((oldTemp.id != newTemp.id) ||
                    (oldTemp.primarySvgUrl != newTemp.primarySvgUrl)
                    || (oldTemp.isFavourite != newTemp.isFavourite)
                    || (oldTemp.primaryText !=newTemp.primaryText)
                    || (oldTemp.name != newTemp.name)
                ) {
                    return false
                }
            }
        }

        return (old.id == new.id
                && old.name == new.name
                && old.iconUrl == new.iconUrl
                && old.thumbnailUrl == new.thumbnailUrl
                && old.name == new.name)
    }

}