package com.framework.utils

import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.DocumentsContract
import com.framework.BaseApplication
import java.io.*

//supported below and above android 11
object FileUtils {

    val cRes = BaseApplication.instance.contentResolver



    @Throws(IOException::class)
    fun getInputStream(uri:Uri): InputStream? {
        return if (isVirtualFile(uri)){
            getInputStreamForVirtualFile(uri, getMimeType(uri))
        }else{
            cRes.openInputStream(uri)
        }
    }

    fun getMimeType(uri: Uri): String? {
        return cRes.getType(uri)
    }

    private fun isVirtualFile(uri: Uri): Boolean {

        if (!DocumentsContract.isDocumentUri(BaseApplication.instance, uri)) {
            return false
        }

        val cursor: Cursor? = cRes.query(
            uri,
            arrayOf(DocumentsContract.Document.COLUMN_FLAGS),
            null,
            null,
            null
        )

        val flags: Int = cursor?.use {
            if (cursor.moveToFirst()) {
                cursor.getInt(0)
            } else {
                0
            }
        } ?: 0

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            flags and DocumentsContract.Document.FLAG_VIRTUAL_DOCUMENT != 0
        } else {
            return false
        }
    }

    @Throws(IOException::class)
    private fun getInputStreamForVirtualFile(
        uri: Uri, mimeTypeFilter: String?): FileInputStream? {

        if (mimeTypeFilter==null){
            throw FileNotFoundException()
        }
        val openableMimeTypes: Array<String>? =
            cRes.getStreamTypes(uri, mimeTypeFilter)

        return if (openableMimeTypes?.isNotEmpty() == true) {
            cRes
                .openTypedAssetFileDescriptor(uri, openableMimeTypes[0], null)?.createInputStream()
        } else {
            throw FileNotFoundException()
        }
    }

    fun copyStreamToFile(inputStream: InputStream, outputFile: File) {
        inputStream.use { input ->
            val outputStream = FileOutputStream(outputFile)
            outputStream.use { output ->
                val buffer = ByteArray(4 * 1024) // buffer size
                while (true) {
                    val byteCount = input.read(buffer)
                    if (byteCount < 0) break
                    output.write(buffer, 0, byteCount)
                }
                output.flush()
            }
        }
    }
}