package com.boost.marketplace.ui.popup.call_track

import androidx.annotation.Nullable
import androidx.recyclerview.widget.DiffUtil


class VMNDiffUtilClass(oldNumberList: MutableList<String>, newNumberList: MutableList<String>) :
    DiffUtil.Callback() {
    private var mOldNumberList: MutableList<String> = ArrayList()
    private var mNewNumberList: MutableList<String> = ArrayList()
    override fun getOldListSize(): Int {
        return mOldNumberList.size
    }

    override fun getNewListSize(): Int {
        return mNewNumberList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return mOldNumberList[oldItemPosition] === mNewNumberList[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldEmployee: String = mOldNumberList[oldItemPosition]
        val newEmployee: String = mNewNumberList[newItemPosition]
        return oldEmployee.equals(newEmployee)
    }

    @Nullable
    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        // Implement method if you're going to use ItemAnimator
        return super.getChangePayload(oldItemPosition, newItemPosition)
    }

    init {
        mOldNumberList = oldNumberList
        mNewNumberList = newNumberList
    }
}