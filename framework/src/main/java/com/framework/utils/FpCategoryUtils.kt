package com.framework.utils

fun isHotelProfile(category_code: String?): Boolean {
    return category_code.equals("HOT", true)
}

fun isManufacturingProfile(category_code: String?): Boolean {
    return category_code.equals("MFG", true)
}

fun isRestaurantProfile(category_code: String?): Boolean {
    return category_code.equals("CAF", true)
}