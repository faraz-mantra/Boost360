package com.framework.utils

import android.content.*
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.net.Uri
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import com.framework.BaseApplication
import com.framework.R
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target

class ContentSharing {
    companion object {

        // product, service
        var targetMap: Target? = null
        fun shareProduct(
            name: String? = null,
            price: String? = null,
            link: String? = null,
            vmn: String? = null,
            imageUri: String? = null,
            isWhatsApp: Boolean? = false,
            isService: Boolean? = false,
            isFb:Boolean?=false
        ) {
            val orderAppointment = if (isService==true) "appointment" else "order"
            val productTemplate =
                """🆕 *Item name:* $name
🏷️ *Price:* Rs.$price
👉🏼 *Place your $orderAppointment here:* $link
📞 Feel free to call $vmn if you need any help. 
"""
            share(shareText = productTemplate, imageUri = imageUri, isWhatsApp = isWhatsApp, isFb = isFb)
        }


        private fun shareTextService(
            uri: Uri?,
            shareText: String,
            isWhatsApp: Boolean?,
            isFb: Boolean?,
            isLinkedin: Boolean?,
            isTwitter: Boolean?,
            intentChooserTitle: String? = BaseApplication.instance.getString(R.string.app_name)
        ) {
            val context = BaseApplication.instance
            val share = Intent(Intent.ACTION_SEND)
            share.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            share.putExtra(Intent.EXTRA_TEXT, shareText)
            uri?.let { share.putExtra(Intent.EXTRA_STREAM, uri) }
            share.type = if (uri != null) "image/*" else "text/plain"
            if (share.resolveActivity(context.packageManager!!) != null) {
                try {
                    if (isWhatsApp == true)
                        share.setPackage(context.getString(R.string.whatsapp_package))
                    if (isFb == true)
                        share.setPackage(context.getString(R.string.facebook_package))
                    if (isLinkedin == true)
                        share.setClassName(
                            "com.linkedin.android",
                            "com.linkedin.android.home.UpdateStatusActivity"
                        );
                    if (isTwitter == true)
                        share.setPackage(context.getString(R.string.twitter_package))
                    context.startActivity(Intent.createChooser(share, intentChooserTitle))
                } catch (e: ActivityNotFoundException) {
                    context.startActivity(Intent.createChooser(share, intentChooserTitle))
                }

            }
        }

        fun share(
            shareText: String,
            imageUri: String? = null,
            isWhatsApp: Boolean? = false,
            isFb: Boolean? = false,
            isTwitter: Boolean? = false,
            isLinkedin: Boolean? = false
        ) {
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
                            shareTextService(
                                uri,
                                shareText,
                                isWhatsApp,
                                isFb,
                                isLinkedin,
                                isTwitter
                            )
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
                    shareTextService(null, shareText, isWhatsApp, isFb, isLinkedin, isTwitter)
                }
            }
        }

        //todo updates
        fun shareUpdates(
            updateContent: String,
            link: String?,
            catalogLink: String,
            vmn: String?,
            isWhatsApp: Boolean?,
            isFb: Boolean?,
            imageUri: String? = null
            ) {
            val updateTemplate = """👋🏼 Hey there!
${truncateString(updateContent, 100)}: Read more $link
🏷️ Check our online catalogue, $catalogLink
📞 Feel free to call $vmn if you need any help. 
"""
            share(updateTemplate, isWhatsApp = isWhatsApp,isFb = isFb,imageUri = imageUri)
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

        fun shareWebsiteTheme(
            businessName: String,
            websiteLink: String,
            vmn: String,
            isWhatsApp: Boolean? = false,
            isFb: Boolean? = false,
            isTwitter: Boolean? = false,
            isLinkedin: Boolean? = false,
            isCopy: Boolean? = false
        ) {
            val webSiteThemeTemplate = "Greetings from $businessName.\n" +
                    "\uD83C\uDF10 Check our website for the latest updates and offers $websiteLink.\n" +
                    "\uD83D\uDCDE For any query, call: $vmn"
            if (isCopy == true) {
                setClipboard(context = BaseApplication.instance.baseContext, webSiteThemeTemplate)
                return
            }
            share(
                webSiteThemeTemplate, isWhatsApp = isWhatsApp,
                isFb = isFb, isTwitter = isTwitter, isLinkedin = isLinkedin
            )
        }

        private fun setClipboard(context: Context, text: String) {
            Toast.makeText(context,"Copied!",Toast.LENGTH_LONG).show()
            val clipboard =
                context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Copied Text", text)
            clipboard.setPrimaryClip(clip)
        }

    }


}