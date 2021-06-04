package com.framework.utils

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.net.Uri
import android.provider.MediaStore
import android.view.View
import com.framework.BaseApplication
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target

class ContentSharing {
    companion object {

        // product, service
        var targetMap: Target? = null
        fun shareProduct(
            name: String?= null,
            price: String?= null,
            link: String?= null,
            vmn: String?= null,
            imageUri: String?= null,
            isWhatsApp: Boolean? = false
        ) {
            val productTemplate =
                """🆕 *Item name:* $name
🏷️ *Price:* Rs.$price
👉🏼 *Place your order/appointment here:* $link
📞 Feel free to call $vmn if you need any help. 
"""
            share(productTemplate, imageUri, isWhatsApp)
        }


        private fun shareTextService(uri: Uri?, shareText: String,isWhatsApp: Boolean?) {
            val share = Intent(Intent.ACTION_SEND)
            share.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            share.putExtra(Intent.EXTRA_TEXT, shareText)
            uri?.let { share.putExtra(Intent.EXTRA_STREAM, uri) }
            share.type = if (uri != null) "image/*" else "text/plain"
            if (share.resolveActivity(BaseApplication.instance.packageManager!!) != null) {
                if (isWhatsApp==true)
                share.setPackage("com.whatsapp")
                BaseApplication.instance.startActivity(Intent.createChooser(share, "share product"))
            }
        }

        fun share(shareText: String, imageUri: String? = null, isWhatsApp: Boolean? = false) {
            if (NetworkUtils.isNetworkConnected()) {

                val target: Target = object : Target {
                    override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom) {
                        targetMap = null
                        try {
                            val mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
                            val view = View(BaseApplication.instance)
                            view.draw(Canvas(mutableBitmap))
                            val path = MediaStore.Images.Media.insertImage(
                                BaseApplication.instance.contentResolver,
                                mutableBitmap,
                                "boost_360",
                                ""
                            )
                            val uri = Uri.parse(path)
                            shareTextService(uri, shareText,isWhatsApp)
                        } catch (e: OutOfMemoryError) {
                        } catch (e: Exception) {
                        }
                    }

                    override fun onBitmapFailed(e: Exception, errorDrawable: Drawable) {
                        targetMap = null
                    }

                    override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                    }
                }
                if (imageUri.isNullOrEmpty().not()) {
                    targetMap = target
                    Picasso.get().load(imageUri ?: "").into(target)
                } else {
                    shareTextService(null, shareText,isWhatsApp)
                }
            }
        }

        //todo updates
        fun shareUpdates(
            updateContent: String,
            link: String?,
            catalogLink: String,
            vmn: String?,
            isWhatsApp: Boolean?
        ) {
            val updateTemplate = """👋🏼 Hey there!
${truncateString(updateContent, 100)}: Read more $link
🏷️ Check our online catalogue, $catalogLink
📞 Feel free to call $vmn if you need any help. 
"""
            share(updateTemplate, isWhatsApp = isWhatsApp)
        }

        fun truncateString(string: String, maxChar: Int): String {
            return if (string.length < maxChar) {
                string
            } else {
                string.substring(0, maxChar) + "..."
            }
        }

        //todo customPages
        fun shareCustomPages(
            pageName: String,
            link: String?,
            vmn: String?,
            catalogLink: String,
            isWhatsApp: Boolean?
        ) {
            val customPagesTemplate = """🆕 Read about $pageName. $link
🏷️ Check our online catalogue, $catalogLink
📞 Feel free to call $vmn if you need any help. 
"""
            share(customPagesTemplate, isWhatsApp = isWhatsApp)
        }

        fun shareTestimonial(
            testimonialContent: String,
            customerName: String,
            link: String?,
            catalogLink: String,
            vmn: String,
            isWhatsApp: Boolean?
        ) {
            val testimonialTemplate = """*${truncateString(testimonialContent, 100)}*
💁 See what *$customerName* has to say about us $link
🏷️ Check our online catalogue, $catalogLink
📞 Feel free to call $vmn if you need any help. 
"""
            share(testimonialTemplate, isWhatsApp = isWhatsApp)
        }

    }
}