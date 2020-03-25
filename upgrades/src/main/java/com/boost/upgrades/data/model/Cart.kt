package com.boost.upgrades.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Cart")
class Cart {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id = 0
    @ColumnInfo(name = "item_id")
    var item_id: String? = null
    @ColumnInfo(name = "item_name")
    var item_name: String? = null
    @ColumnInfo(name = "link")
    var link: String? = null
    @ColumnInfo(name = "price")
    var price: Float = 0f
    @ColumnInfo(name = "quantity")
    var quantity: Int = 0
}