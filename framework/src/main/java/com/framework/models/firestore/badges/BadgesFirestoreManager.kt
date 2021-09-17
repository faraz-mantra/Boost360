package com.framework.models.firestore.badges

import android.util.Log
import com.framework.models.firestore.FirestoreManager.toDataClass
import com.framework.models.firestore.badges.BadgesModel.BadgesType.Companion.fromUrlCheck
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson

object BadgesFirestoreManager {

  var TAG = "BadgesFirestoreManager"
  var db: FirebaseFirestore? = null
  val gson = Gson()
  var badgesModel: ArrayList<BadgesModel>? = null
  var fpTag: String = ""
  var fpId: String = ""
  var clientId: String = ""
  private const val COLLECTION_NAME = "badges"
  var listenerBadges: (() -> Unit)? = null

  fun initDataBadges(fpTag: String, fpId: String, clientId: String) {
    this.fpTag = fpTag
    this.fpId = fpId
    this.clientId = clientId
    this.db = Firebase.firestore
    if (this.badgesModel.isNullOrEmpty()) this.badgesModel = ArrayList()
    readDrScoreDocument()
  }

  private fun readDrScoreDocument() {
    Log.e("readBadgesDocument ", "readBadgesDocument")
    getDocumentReference()?.addSnapshotListener(MetadataChanges.INCLUDE) { snapshot, e ->
      if (e == null && snapshot?.documentChanges != null) {
        Log.d(TAG, "Document Data is : ${snapshot.documentChanges}")
        badgesModel = ArrayList()
        for (documentChange in snapshot.documentChanges) {
          val badgesType: BadgesModel.BadgesType? = fromUrlCheck(documentChange.document.reference.path)
          if (badgesType != null) {
            val model = documentChange.document.data.toDataClass<BadgesModel>()
            model.badgesType = badgesType.name
            badgesModel!!.add(model)
          }
        }
        listenerBadges?.invoke()
      } else {
        Log.d(TAG, "Exception$e")
      }
    }
  }

  private fun getDocumentReference(): CollectionReference? {
    try {
      return db?.collection(COLLECTION_NAME)?.document(this.fpTag)?.collection("BADGE")
    } catch (e: Exception) {
      e.printStackTrace()
      Log.e(TAG, "Firestore document reference")
    }
    return null
  }

  fun getBadgesData(): ArrayList<BadgesModel>? {
    return this.badgesModel
  }
}