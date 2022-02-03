package com.framework.firebaseUtils.firestore

import android.text.TextUtils
import android.util.Log
import com.framework.analytics.SentryController
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.framework.base.BaseResponse
import com.framework.firebaseUtils.firestore.restApi.model.CreateDrRequest
import com.framework.firebaseUtils.firestore.restApi.model.UpdateDrRequest
import com.framework.firebaseUtils.firestore.restApi.repository.DrScoreRepository
import com.framework.models.UpdateDraftBody
import com.framework.models.toLiveData
import io.reactivex.Observable
import com.google.gson.reflect.TypeToken

object FirestoreManager {

  var db: FirebaseFirestore? = null
  val gson = Gson()
  var model: DrScoreModel? = null
  var fpTag: String = ""
  var fpId: String = ""
  var clientId: String = ""
  var TAG = "FirestoreManager"
  const val COLLECTION_NAME = "drsMerchants"
  const val DRAFT_COLLECTION="postUpdateDrafts"
  var listener: (() -> Unit)? = null

  fun initData(fpTag: String, fpId: String, clientId: String) {
    this.fpTag = fpTag
    this.fpId = fpId
    this.clientId = clientId
    this.db = Firebase.firestore
    if (this.model == null) this.model = DrScoreModel()
    readDrScoreDocument()
  }

  fun reset() {
    Log.i(TAG, "reset called: ")
    this.fpTag = ""
    this.fpId = ""
    this.clientId = ""
    this.db = null
    this.model = null
    readDrScoreDocument()

  }
//    fun readCollection() {
//        this.db?.collection("drsMerchants")?.get()?.addOnSuccessListener { result ->
//            for (document in result) {
//
//                Log.d("FirestoreManager", "${document.id} => ${document.data}")
//            }
//        }
//                ?.addOnFailureListener { exception ->
//                    Log.w("FirestoreManager", "Error getting documents.", exception)
//                }
//    }

  fun readDrScoreDocument() {
    Log.e("readDrScoreDocument ", "readDrScoreDocument")
    getDocumentReference()?.addSnapshotListener(MetadataChanges.EXCLUDE) { snapshot, e ->
      if (e == null) {
        Log.d(TAG, "No Exception")
        Log.d(TAG, "Document Data is : " + snapshot?.data)
        model = snapshot?.data?.toDataClass<DrScoreModel>()
        updateDrScoreIfNull()
        listener?.invoke()
      } else {
        Log.d(TAG, "Exception$e")
      }
    }
  }

  fun readDraft(listener:((UpdateDraftBody?)->Unit)){
    db?.collection(DRAFT_COLLECTION)?.document(this.fpTag)?.
            get()?.addOnCompleteListener {
              if (it.isSuccessful){
                listener.invoke(it.result.toObject(UpdateDraftBody::class.java))
              }else{
                listener.invoke(null)
              }
    }
  }


  private fun getDocumentReference(): DocumentReference? {
    try {
      return db?.collection(COLLECTION_NAME)?.document(this.fpTag)
    } catch (e: Exception) {
      e.printStackTrace()
      SentryController.captureException(e)
      Log.e(TAG, "Firestore document reference")
    }
    return null
  }

  private fun updateDrScoreIfNull() {
    if (this.model == null) {
      this.model = DrScoreModel()
//      val docRef = getDocumentReference()
//      updateDocument(docRef, this.model.serializeToMap())
      if (this.fpTag.isNotEmpty()) DrScoreRepository.createDrScoreData(CreateDrRequest(fpTag = this.fpTag)).apiCreateUpdate()
    }
  }

  fun updateDocument(doc: DocumentReference?, map: Map<String, Any>) {
    doc?.set(map)?.addOnSuccessListener {
      Log.d(TAG, "document updated")
    }
  }

  fun getDrScoreData(): DrScoreModel? {
    return this.model
  }

  fun updateDrScoreData(model: DrScoreModel) {
    this.model = model
    updateDocument(getDocumentReference(), this.model.serializeToMap())
  }

  fun updateDocument() {
    if (this.model != null && !TextUtils.isEmpty(this.model?.client_id) && this.model?.metricdetail?.currentValueUpdate != null) {
//      updateDocument(getDocumentReference(), this.model.serializeToMap())
      DrScoreRepository.updateDrScoreData(
        UpdateDrRequest(
          this.model?.client_id,
          this.model?.fp_tag,
          this.model?.metricdetail?.currentValueUpdate?.key,
          this.model?.metricdetail?.currentValueUpdate?.value
        )
      ).apiCreateUpdate()
    }
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
    val json = gson.toJson(this)
    return gson.fromJson(json, object : TypeToken<O>() {}.type)
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