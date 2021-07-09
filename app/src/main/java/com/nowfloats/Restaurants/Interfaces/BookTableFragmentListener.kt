package com.nowfloats.Restaurants.Interfaces

import com.nowfloats.Restaurants.API.model.GetBookTable.Data


interface BookTableFragmentListener {

    fun itemMenuOptionStatus(pos: Int, status: Boolean)

    fun editOptionClicked(data: Data)

    fun deleteOptionClicked(data: Data)
}