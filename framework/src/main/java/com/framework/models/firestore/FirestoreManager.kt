package com.framework.models.firestore

import android.util.Log
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object FirestoreManager {
    var db: FirebaseFirestore? = null;
    val gson = Gson();
    var model: DrScoreModel? = null;
    var fpTag: String = "";
    var fpId: String = "";
    var clientId: String = "";
    var TAG = "FirestoreManager";
    val COLLECTION_NAME = "drsMerchants";
    var listener: (()->Unit)? = null

    fun initData(fpTag: String, fpId: String, clientId: String) {
        this.fpTag = fpTag;
        this.fpId = fpId;
        this.clientId = clientId;
        this.db = Firebase.firestore;
        if (this.model == null) {
            this.model = DrScoreModel();
        }
        readDrScoreDocument();
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
        Log.e("readDrScoreDocument ","readDrScoreDocument")
        val docRef = getDocumentReference();
        docRef?.addSnapshotListener(MetadataChanges.INCLUDE) { snapshot, e ->
            if (e == null) {
                Log.d(TAG, "No Exception")
                model = snapshot?.data?.toDataClass<DrScoreModel>();
                updateDrScoreIfNull()
                listener?.invoke()
            } else {
                Log.d(TAG, "Exception" + e);
            }
        }
    }

    fun getDocumentReference(): DocumentReference? {
        return db?.collection(COLLECTION_NAME)?.document(this.fpTag);
    }

    fun updateDrScoreIfNull() {
        if (this.model == null) {
            this.model = DrScoreModel();
            val docRef = getDocumentReference();
            updateDocument(docRef, this.model.serializeToMap());
        }
    }

    fun updateDocument(doc: DocumentReference?, map: Map<String, Any>) {
        doc?.set(map)?.addOnSuccessListener {
            Log.d(TAG, "document updated");
        }
    }

    fun getDrScoreData(): DrScoreModel? {
        return this.model;
    }

    fun updateDrScoreData(model: DrScoreModel) {
        this.model = model;
        updateDocument(getDocumentReference(), this.model.serializeToMap());
    }

    fun updateDocument() {
        updateDocument(getDocumentReference(), this.model.serializeToMap());
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
        val json = gson.toJson(this)
        return gson.fromJson(json, object : TypeToken<O>() {}.type)
    }

}