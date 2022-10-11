package com.framework.utils

import android.app.Activity
import android.util.Base64
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.framework.BuildConfig
import com.google.android.play.core.integrity.IntegrityManagerFactory
import com.google.android.play.core.integrity.IntegrityTokenRequest
import com.google.gson.JsonObject
import org.jose4j.jwe.JsonWebEncryption
import org.jose4j.jws.JsonWebSignature
import org.jose4j.jwx.JsonWebStructure
import org.jose4j.lang.JoseException
import org.json.JSONObject
import java.security.KeyFactory
import java.security.NoSuchAlgorithmException
import java.security.PublicKey
import java.security.spec.InvalidKeySpecException
import java.security.spec.X509EncodedKeySpec
import java.util.*
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec
import kotlin.collections.ArrayList

object DeviceVerificationUtils {

    private const val TAG = "DeviceVerificationUtils"
    fun isDeviceTrustable(activity: Activity, onComplete:(Boolean)->Unit){
        if (BuildConfig.BUILD_TYPE=="qa"||BuildConfig.BUILD_TYPE=="debug"){
            onComplete.invoke(true)
            return
        }
        val DECRYPTION_KEY="1wTdL1UkV9ZykyY2vTklUUQdPbI9qVKXyiESpDH/DOs="
        val VERIFICATION_KEY="MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEaWRdHVXmNBpU0XOMveJcUsVOD2kbZKKthJ8HMgZhueaw/mq5bHuKxONSLWgGIhqyYDFFHBPP+gN4eODL0CoH9Q=="
        val integrityManager =
            IntegrityManagerFactory.create(activity)
        val nonce = UUID.randomUUID().toString()
        // Request the integrity token by providing a nonce
        // Request the integrity token by providing a nonce
        val integrityTokenResponse = integrityManager
            .requestIntegrityToken(
                IntegrityTokenRequest.builder()
                    .setNonce(nonce)
                    .build()
            )
            .addOnSuccessListener { response ->
                val integrityToken: String = response.token()
                Log.d(TAG, integrityToken)
                val decryptionKeyBytes: ByteArray = Base64.decode(DECRYPTION_KEY, Base64.DEFAULT)

                // SecretKey
                val decryptionKey: SecretKey = SecretKeySpec(
                    decryptionKeyBytes,
                    0,
                    decryptionKeyBytes.size,
                    "AES"
                )
                val encodedVerificationKey: ByteArray = Base64.decode(VERIFICATION_KEY, Base64.DEFAULT)

                // PublicKey
                var verificationKey: PublicKey? = null
                try {
                    verificationKey = KeyFactory.getInstance("EC")
                        .generatePublic(X509EncodedKeySpec(encodedVerificationKey))
                } catch (e: InvalidKeySpecException) {
                    Log.d(TAG, e.message?:"")
                } catch (e: NoSuchAlgorithmException) {
                    Log.d(TAG, e.message?:"")
                }

                // some error occurred so return
                if (null == verificationKey) {
                    return@addOnSuccessListener
                }

                // JsonWebEncryption
                var jwe: JsonWebEncryption? = null
                try {
                    jwe = JsonWebStructure
                        .fromCompactSerialization(integrityToken) as JsonWebEncryption
                } catch (e: JoseException) {
                    e.printStackTrace()
                }

                // some error occurred so return
                if (null == jwe) {
                    return@addOnSuccessListener
                }
                jwe.setKey(decryptionKey)
                var compactJws: String? = null
                try {
                    compactJws = jwe.getPayload()
                } catch (e: JoseException) {
                    Log.d(TAG, e.message?:"")
                }

                // JsonWebSignature
                var jws: JsonWebSignature? = null
                try {
                    jws = JsonWebStructure
                        .fromCompactSerialization(compactJws) as JsonWebSignature
                } catch (e: JoseException) {
                    Log.d(TAG, e.message?:"")
                }

                // some error occurred so return
                if (null == jws) {
                    return@addOnSuccessListener
                }
                jws?.setKey(verificationKey)

                // get the json human readable string
                var jsonPlainVerdict = ""
                jsonPlainVerdict = try {
                    jws.getPayload()
                } catch (e: JoseException) {
                    Log.d(TAG, e.message?:"")
                    return@addOnSuccessListener
                }

                // payload is available in json format
                // plain text, can be processed as per needs
                try {
                    val requestDetails = JSONObject(jws.payload).getJSONObject("deviceIntegrity")
                    val deviceRecognitionVerdict =
                        requestDetails.getJSONArray("deviceRecognitionVerdict")
                    val verdictList =ArrayList<String>()
                    for (i in 0 until deviceRecognitionVerdict.length()){
                        verdictList.add(deviceRecognitionVerdict.getString(i))
                    }

                    if (verdictList.find { it=="MEETS_DEVICE_INTEGRITY" }!=null) {
                        // Looks good!
                        onComplete(true)
                    }else{
                        onComplete(false)
                    }
                }catch (e:Exception){
                    onComplete(false)
                }

                Log.d(TAG, jsonPlainVerdict)
            }
            .addOnFailureListener { ex ->
                val status =RootUtil.isDeviceRooted
                onComplete(status)
            }
    }

}