package com.nowfloats.education.helper

import android.graphics.Bitmap
import com.nowfloats.education.batches.ui.batchesdetails.BatchesDetailsFragment
import com.nowfloats.education.batches.ui.batchesfragment.BatchesFragment
import com.nowfloats.education.faculty.ui.facultydetails.FacultyDetailsFragment
import com.nowfloats.education.faculty.ui.facultymanagement.FacultyManagementFragment
import com.nowfloats.education.toppers.ui.topperdetails.TopperDetailsFragment
import com.nowfloats.education.toppers.ui.topperhome.ToppersFragment
import com.nowfloats.education.unlockfeature.UnlockFeatureFragment
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

object Constants {

    const val EDUCATION_API_BASE_URL = "https://webaction.api.boostkit.dev/api/v1/"
    const val UPLOAD_OUR_FACULTY_IMAGE_URL = "our_faculty/upload-file?assetFileName="
    const val UPLOAD_OUR_TOPPER_IMAGE_URL = "our_toppers/upload-file?assetFileName="
    const val CONNECTION_TIMEOUT: Long = 60
    const val READ_TIMEOUT: Long = 60

    const val AUTH_CODE = "5a952ff2834fd804ecb798d5"

    const val WEBSITE_ID_EDUCATION = "EDUCATION"
    const val LIMIT = 1000
    const val SKIP = 0
    const val DATE_FORMAT = "MM-dd-YYYY"
    const val TIME_FORMAT = "hh:mm aa"
    private const val DATE_FORMAT_SERVER = "yyyy-MM-dd'T'HH:mm:ss'Z'"

    // Bitmap Format
    val JPEG: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG
    val PNG: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG
    val WEBP: Bitmap.CompressFormat = Bitmap.CompressFormat.WEBP
    val DEFAULT_BITMAP_FORMAT = JPEG

    const val JPEG_FORMAT = ".jpg"
    const val PNG_FORMAT = ".png"

    const val CAMERA_REQUEST_CODE = 111
    const val GALLERY_REQUEST_CODE = 112

    const val PICTURE_QUALITY_100 = 100
    const val PICTURE_QUALITY_90 = 90
    const val PICTURE_QUALITY_80 = 80
    const val PICTURE_QUALITY_70 = 70
    const val PICTURE_QUALITY_60 = 60
    const val PICTURE_QUALITY_50 = 50
    const val PICTURE_QUALITY_40 = 40

    const val TOPPER_FEATURE = "OUR-TOPPERS"
    const val UPCOMING_BATCH_FEATURE = "UPCOMING-BATCHES"
    const val FACULTY_MANAGEMENT_FEATURE = "FACULTY"

    val BATCHES_FRAGMENT: String = BatchesFragment::class.java.simpleName
    val BATCHES_DETAILS_FRAGMENT: String = BatchesDetailsFragment::class.java.simpleName
    val FACULTY_DETAILS_FRAGMENT: String = FacultyDetailsFragment::class.java.simpleName
    val FACULTY_MANAGEMENT_FRAGMENT: String = FacultyManagementFragment::class.java.simpleName
    val TOPPERS_FRAGMENT: String = ToppersFragment::class.java.simpleName
    val TOPPERS_DETAILS_FRAGMENT: String = TopperDetailsFragment::class.java.simpleName
    val TIME_PICKER_DIALOG_FRAGMENT: String = TimePickerDialogFragment::class.java.simpleName
    val UNLOCK_FEATURE_FRAGMENT: String = UnlockFeatureFragment::class.java.simpleName

    const val FACULTY_PROFILE_IMAGE = "faculty_profile_image"
    const val TOPPER_PROFILE_IMAGE = "topper_profile_image"
    const val TOPPER_TESTIMONIAL_IMAGE = "topper_testimonial_image"


    const val JSON_DOCUMENT_WAS_NOT_FULLY_CONSUMED = "JSON document was not fully consumed."
    const val SUCCESS = "success"
    const val SAVE = "Save"
    const val DELETE = "Delete"
    const val UPDATE = "Update"


    fun getDateFromString(dateString: String, format: String): String {
        if (!dateString.isNullOrBlank()) {
            return if (dateString.length > 10) {
                val originalFormat: DateFormat = SimpleDateFormat(DATE_FORMAT_SERVER, Locale.ENGLISH)
                val formatter = SimpleDateFormat(format)
                val date = originalFormat.parse(dateString)
                formatter.format(date)
            } else {
                dateString
            }
        }
        return ""
    }

    fun getFormattedDate(dateString: String) {
        SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(dateString)
    }
}