package com.festive.poster.constant

enum class FragmentType {

    PROMOTION_LANDING_PAGE;

    companion object{
        fun fromValue(name: String): FragmentType? = values().firstOrNull { it.name.equals(name, ignoreCase = true) }
    }
}