package com.boost.dbcenterapi.upgradeDB.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "Coupons")
data class CouponsModel(

  @PrimaryKey
  @ColumnInfo(name = "coupon_key")
  var coupon_key: String,

  @ColumnInfo(name = "discount_percent")
  var discount_percent: Int = 0
)