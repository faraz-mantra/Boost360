package com.boost.upgrades.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "Bundles")
data class BundlesModel(

        @PrimaryKey
        @ColumnInfo(name = "bundle_key")
        var bundle_key: String,

        @ColumnInfo(name = "name")
        var name: String? = null,

        @ColumnInfo(name = "min_purchase_months")
        var min_purchase_months: Int = 1,

        @ColumnInfo(name = "overall_discount_percent")
        var overall_discount_percent: Int = 0,


        @ColumnInfo(name = "primary_image")
        var primary_image: String? = null,

        @ColumnInfo(name = "included_features")
        var included_features: String? = null,

        @ColumnInfo(name = "target_business_usecase")
        var target_business_usecase: String? = null
)