package com.boost.upgrades.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "MarketOffers")
data class MarketOfferModel(

        @PrimaryKey
        @ColumnInfo(name = "coupon_code")
        var coupon_code: String,

        @ColumnInfo(name = "extra_information")
        var extra_information: String? = null,

        @ColumnInfo(name = "createdon")
        var createdon: String? = null,

        @ColumnInfo(name = "updatedon")
        var updatedon: String? = null,

        @ColumnInfo(name = "_kid")
        var _kid: String? = null,

        @ColumnInfo(name = "websiteid")
        var websiteid: String? = null,

        @ColumnInfo(name = "isarchived")
        var isarchived: Boolean = false,

        @ColumnInfo(name = "expiry_date")
        var expiry_date: String? = null,

        @ColumnInfo(name = "title")
        var title: String? = null,

        @ColumnInfo(name = "exclusive_to_categories")
        var exclusive_to_categories: String? = null,

        @ColumnInfo(name = "image")
        var image: String? = null,

        @ColumnInfo(name = "cta_offer_identifier")
        var cta_offer_identifier: String? = null,
)