package com.boost.upgrades.data.api_model.RazorpayToken

data class Item(
    val card: Card,
    val created_at: Int,
    val entity: String,
    val id: String,
    val method: String,
    val used_at: Int
)