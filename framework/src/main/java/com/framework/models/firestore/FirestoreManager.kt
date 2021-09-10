package com.framework.models.firestore

import android.text.TextUtils
import android.util.Log
import com.framework.base.BaseResponse
import com.framework.models.firestore.restApi.model.CreateDrRequest
import com.framework.models.firestore.restApi.model.UpdateDrRequest
import com.framework.models.firestore.restApi.repository.DrScoreRepository
import com.framework.models.toLiveData
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.Observable

object FirestoreManager {

  var db: FirebaseFirestore? = null
  val gson = Gson()
  var model: DrScoreModel? = null
  var fpTag: String = ""
  var fpId: String = ""
  var clientId: String = ""
  var TAG = "FirestoreManager"
  private const val COLLECTION_NAME = "drsMerchants"
  var listener: (() -> Unit)? = null

  fun initData(fpTag: String, fpId: String, clientId: String) {
    FirestoreManager.fpTag = fpTag
    FirestoreManager.fpId = fpId
    FirestoreManager.clientId = clientId
    db = Firebase.firestore
    if (model == null) {
      model = DrScoreModel()
    }
    readDrScoreDocument()
  }

//  fun readCollection() {
//    this.db?.collection("drsMerchants")?.get()?.addOnSuccessListener { result ->
//      for (document in result) {
//        Log.d("FirestoreManager", "${document.id} => ${document.data}")
//      }
//    }
//      ?.addOnFailureListener { exception ->
//        Log.w("FirestoreManager", "Error getting documents.", exception)
//      }
//  }

  fun readDrScoreDocument() {
    Log.e("readDrScoreDocument ", "readDrScoreDocument")
    getDocumentReference()?.addSnapshotListener(MetadataChanges.INCLUDE) { snapshot, e ->
      if (e == null) {
        Log.d(TAG, "Document Data is : " + snapshot?.data)
        model = snapshot?.data?.toDataClass<DrScoreModel>()
        updateDrScoreIfNull()
        listener?.invoke()
      } else {
        Log.d(TAG, "Exception" + e)
      }
    }
  }

  private fun getDocumentReference(): DocumentReference? {
    try {
      return db?.collection(COLLECTION_NAME)?.document(fpTag)
    } catch (e: Exception) {
      e.printStackTrace()
      Log.e(TAG, "Firestore document reference")
    }
    return null
  }

  private fun updateDrScoreIfNull() {
    if (model == null) {
      model = DrScoreModel()
//      val docRef = getDocumentReference()
//      updateDocument(docRef, this.model.serializeToMap())
      if (fpTag.isNotEmpty()) DrScoreRepository.createDrScoreData(CreateDrRequest(fpTag = fpTag)).apiCreateUpdate()
    }
  }

  fun updateDocument(doc: DocumentReference?, map: Map<String, Any>) {
    doc?.set(map)?.addOnSuccessListener {
      Log.d(TAG, "document updated")
    }
  }

  fun getDrScoreData(): DrScoreModel? {
    return model
  }

  fun updateDrScoreData(model: DrScoreModel) {
    FirestoreManager.model = model
    updateDocument(getDocumentReference(), FirestoreManager.model.serializeToMap())
  }

  fun updateDocument() {
    if (model != null && !TextUtils.isEmpty(model?.client_id) && model?.metricdetail?.currentValueUpdate != null) {
//      updateDocument(getDocumentReference(), this.model.serializeToMap())
      DrScoreRepository.updateDrScoreData(
        UpdateDrRequest(
          model?.client_id,
          model?.fp_tag,
          model?.metricdetail?.currentValueUpdate?.key,
          model?.metricdetail?.currentValueUpdate?.value
        )
      ).apiCreateUpdate()
    }
  }

  private fun Observable<BaseResponse>.apiCreateUpdate() {
    this.toLiveData().observeForever {
      if (it.isSuccess()) {
        readDrScoreDocument()
        Log.d("apiCreateUpdate", "Success: ${it.anyResponse?.toString()}")
      } else Log.d("apiCreateUpdate", "error: ${it.message()}")
    }
  }
}

//convert a map to a data class
inline fun <reified T> Map<String, Any>.toDataClass(): T {
  return convert()
}

//convert a data class to a map
fun <T> T.serializeToMap(): Map<String, Any> {
  return convert()
}

//convert an object of type I to type O
inline fun <I, reified O> I.convert(): O {
  val json = FirestoreManager.gson.toJson(this)
  return FirestoreManager.gson.fromJson(json, object : TypeToken<O>() {}.type)
}