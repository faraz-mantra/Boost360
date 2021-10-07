package com.festive.poster.utils

import android.graphics.Bitmap
import android.util.Log
import com.bumptech.glide.Glide
import com.festive.poster.FestivePosterApplication
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

object FileUtils {

    private const val TAG = "FileUtils"
    fun getPathOfImages(): String {
        return FestivePosterApplication.instance.getExternalFilesDir(null)?.path+ "/"+"SVGIMAGES/"
    }

    suspend fun saveImage(url: String?,dest:File){

        val bmp= Glide.with(FestivePosterApplication.instance).asBitmap()
            .load(url)
            .submit()
            .get()
        try {
            if (dest.createNewFile()){
                val fOut: OutputStream = FileOutputStream(dest)
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, fOut)
                fOut.flush()
                fOut.close()
            }else{
                throw Exception("Unable to create file")
            }

        } catch (e: Exception) {
            Log.e(TAG, "saveImage: $e" )
            e.printStackTrace()
        }
        Log.i(TAG, "saveImage: ${dest.path}")
    }
}