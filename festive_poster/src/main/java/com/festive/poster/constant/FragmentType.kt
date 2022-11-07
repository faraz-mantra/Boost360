package com.festive.poster.constant

enum class FragmentType {
    UPDATES_LISTING_FRAGMENT,
    STATS_FRAGMENT;

    companion object{
        fun fromValue(name: String): FragmentType? = values().firstOrNull { it.name.equals(name, ignoreCase = true) }
    }
}