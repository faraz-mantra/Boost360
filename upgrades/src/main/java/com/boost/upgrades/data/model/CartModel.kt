package com.boost.upgrades.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Cart")
data class CartModel (

    @PrimaryKey
    @ColumnInfo(name = "id")
    var id: String,

    @ColumnInfo(name = "item_name")
    var item_name: String? = null,

    @ColumnInfo(name = "link")
    var link: String? = null,

    @ColumnInfo(name = "price")
    var price: Int = 0,

    @ColumnInfo(name = "MRPPrice")
    var MRPPrice: Int = 0,

    @ColumnInfo(name = "discount")
    var discount: Int = 0,

    @ColumnInfo(name = "quantity")
    var quantity: Int = 0

)