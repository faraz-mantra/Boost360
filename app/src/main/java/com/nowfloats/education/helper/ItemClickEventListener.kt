package com.nowfloats.education.helper

interface ItemClickEventListener {
    fun onEditClick(data: Any, position: Int)
    fun onDeleteClick(data: Any, position: Int)
    fun itemMenuOptionStatus(pos: Int, status: Boolean)
}