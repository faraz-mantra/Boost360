package com.inventoryorder.model.ordersdetails

import com.inventoryorder.utils.convertMinutesToDays
import java.io.Serializable

data class ExtraPropertiesN(
    val additionalProp1: AdditionalPropN,
    val additionalProp2: AdditionalPropN,
    val additionalProp3: AdditionalPropN,

    //TODO ExtraItemProductConsultation model value
    val age: String? = null,
    val appointmentMessage: String? = null,
    val businessLicense: String? = null,
    val businessName: String? = null,
    val consultationFor: String? = null,
    val doctorId: String? = null,
    val doctorName: String? = null,
    val doctorQualification: String? = null,
    val doctorSignature: String? = null,
    val doctorSpeciality: String? = null,
    val duration: String? = null,
    val endTime: String? = null,
    val gender: String? = null,
    val patientEmailId: String? = null,
    val patientMobileNumber: String? = null,
    val patientName: String? = null,
    val referenceId: String? = null,
    val scheduledDateTime: String? = null,
    val startTime: String? = null
    //TODO ExtraItemProductConsultation model value

) : Serializable {

  fun durationTxt(): String? {
    return duration?.toDoubleOrNull()?.let { convertMinutesToDays(it) }
  }

  fun detailsConsultation(): String {
    val ds = takeIf { doctorSpeciality.isNullOrEmpty().not() }?.let { "($doctorSpeciality)" } ?: ""
    val dn = takeIf { doctorName.isNullOrEmpty().not() }?.let { "$doctorName$ds" } ?: ""
    val c = takeIf { consultationFor.isNullOrEmpty().not() }?.let { "$consultationFor" } ?: ""
    return "$c\n$dn"
  }

  fun startTime(): String {
    return startTime ?: ""
  }

  fun endTime(): String {
    return endTime ?: ""
  }
}