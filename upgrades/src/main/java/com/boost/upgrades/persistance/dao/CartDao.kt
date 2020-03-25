package com.biz2.nowfloats.boost.updates.persistance.dao

import androidx.room.*
import com.boost.upgrades.data.model.Cart
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
interface CartDao {
    @Query("SELECT * FROM Cart")
    fun getCartItems(): Single<List<Cart>>

    @Query("SELECT * FROM Cart WHERE id=:item_id")
    fun getCartItemById(item_id: Int): Flowable<List<Cart?>?>?

    @Query("SELECT COUNT(*)from Cart")
    fun countCartItems(): Int

    @Query("DELETE FROM Cart")
    fun emptyCart()

    @Insert
    fun insertToCart(vararg carts: Cart?)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllUPdates(updates: List<Cart>)

    @Update
    fun updateCart(vararg carts: Cart?)

    @Query("DELETE FROM Cart WHERE item_id=:itemId")
    fun deleteCartItem(vararg itemId: String)
}
