package com.boost.upgrades.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Cart")
data class CartModel (

    @PrimaryKey
    @ColumnInfo(name = "boost_widget_key")
    var boost_widget_key: String,

    @ColumnInfo(name = "item_name")
    var item_name: String? = null,

    @ColumnInfo(name = "description_title")
    var description_title: String? = null,

    @ColumnInfo(name = "link")
    var link: String? = null,

    @ColumnInfo(name = "price")
    var price: Int = 0,

    @ColumnInfo(name = "MRPPrice")
    var MRPPrice: Int = 0,

    @ColumnInfo(name = "discount")
    var discount: Int = 0,

    @ColumnInfo(name = "quantity")
    var quantity: Int = 1,

    @ColumnInfo(name = "extended_properties")
    var extended_properties: String? = null
)