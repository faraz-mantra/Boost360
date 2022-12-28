package com.framework.firebaseUtils.firestore.marketplaceCart

import android.util.Log
import com.framework.firebaseUtils.firestore.DrScoreModel
import com.framework.firebaseUtils.firestore.FirestoreManager
import com.framework.firebaseUtils.firestore.FirestoreManager.toDataClass
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object CartFirestoreManager {

  var TAG = "CartFirestoreManager"
  var db: FirebaseFirestore? = null
  val gson = Gson()
  var cartItemsModel: Map<String,CartItemsModel>? = null
  var fpTag: String = ""
  var fpId: String = ""
  var clientId: String = ""
  private const val COLLECTION_NAME = "marketplaceCart"
  var listener: (() -> Unit)? = null

  fun initDataCart(fpTag: String, fpId: String, clientId: String) {
    this.fpTag = fpTag
    this.fpId = fpId
    this.clientId = clientId
    this.db = Firebase.firestore
    if (this.cartItemsModel == null) this.cartItemsModel = hashMapOf()
      readCartDocument()
  }

  fun readCartDocument() {
    Log.e("readCartDocument ", "readCartDocument")
    getDocumentReference()?.addSnapshotListener(MetadataChanges.EXCLUDE) { snapshot, e ->
      if (e == null) {
        Log.d(TAG, "Badges Document Data is : ${snapshot!!.data}")
//        cartItemsModel = CartItemsModel()
//        for (documentChange in snapshot.data) {
//            val model = documentChange.document.data.toDataClass<CartItemsModel>()
//            cartItemsModel!!.add(model)
//        }
        cartItemsModel = snapshot.data as Map<String, CartItemsModel>?
        Log.d(TAG, "Document CartItems is : ${cartItemsModel}")
        listener?.invoke()
      } else {
        Log.d(TAG, "Exception$e")
      }
    }
  }

  private fun getDocumentReference(): DocumentReference? {
    try {
      return db?.collection(COLLECTION_NAME)?.document(this.fpId)
    } catch (e: Exception) {
      e.printStackTrace()
      Log.e(TAG, "Firestore document cart items reference")
    }
    return null
  }

  fun updateDocument(map: HashMap<String, Any>) {
    getDocumentReference()?.set(map, SetOptions.merge())?.addOnSuccessListener {
      Log.d(FirestoreManager.TAG, "document cartitems updated")
    }?.addOnFailureListener {
      it.printStackTrace()
    }
  }

  fun removeDocument(itemID: String){
    val updates = hashMapOf<String, Any>(
      itemID to FieldValue.delete()
    )
    getDocumentReference()?.update(updates)?.addOnCompleteListener {
      Log.d(FirestoreManager.TAG, "document cartitems deleted")
    }?.addOnFailureListener {
      it.printStackTrace()
    }
  }

  fun getCartData(): Map<String, CartItemsModel>? {
    return this.cartItemsModel
  }

  //convert a map to a data class
  inline fun <reified T> Map<String, Any>.toDataClass(): T {
    return convert()
  }

  //convert a data class to a map
  private fun <T> T.serializeToMap(): Map<String, Any> {
    return convert()
  }

  //convert an object of type I to type O
  inline fun <I, reified O> I.convert(): O {
    val json = FirestoreManager.gson.toJson(this)
    return FirestoreManager.gson.fromJson(json, object : TypeToken<O>() {}.type)
  }
}