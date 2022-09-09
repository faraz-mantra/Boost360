package com.boost.cart.interfaces

import com.boost.dbcenterapi.data.api_model.getCouponResponse.Data

interface ApplyCouponListener {

    fun applycoupon(mList: Data)
}