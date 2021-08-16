package com.nowfloats.education.helper

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatSpinner
import androidx.fragment.app.DialogFragment
import com.thinksity.R
import java.text.SimpleDateFormat
import java.util.*

class TimePickerDialogFragment(
  private val timePickerId: Int,
  private val titleText: String,
  private val listener: CustomTimePickerDialog.OnTimeSetListener
) : DialogFragment() {

  private var hourOfDay: Int? = null
  private var minute: Int? = null
  private var amPmString = PM

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.time_picker_dialog_layout, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    val title = view.findViewById<TextView>(R.id.title_text)
    val timePicker = view.findViewById<TimePicker>(R.id.time_picker)
    val okButton = view.findViewById<AppCompatButton>(R.id.ok_button)
    val cancelButton = view.findViewById<AppCompatButton>(R.id.cancel_button)

    title.text = titleText
    setDefaultTime()

    okButton.setOnClickListener {
      this.dismiss()
      when {
        hourOfDay != null && minute != null -> {
          amPmString = (((timePicker.getChildAt(0) as ViewGroup)
            .getChildAt(3) as ViewGroup)
            .getChildAt(2) as AppCompatSpinner)
            .selectedItem.toString()
          amPmString = if (amPmString.toUpperCase() == PM) PM else AM

          val formattedTime = SimpleDateFormat(TIME_FORMAT).parse("$hourOfDay:$minute")
          val timeString = SimpleDateFormat(TIME_FORMAT).format(formattedTime) + " $amPmString"
          listener.onTimeSet(timePickerId, timeString)
        }
        else -> {
          Toast.makeText(
            requireContext(),
            getString(R.string.please_select_time),
            Toast.LENGTH_SHORT
          ).show()
        }
      }
    }

    cancelButton.setOnClickListener {
      this.dismiss()
    }

    timePicker.setOnTimeChangedListener { _, hourOfDay, minute ->
      this.hourOfDay = hourOfDay
      this.minute = minute
    }
  }

  private fun setDefaultTime() {
    val calendar = Calendar.getInstance()
    hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)
    minute = calendar.get(Calendar.MINUTE)
  }

  companion object {
    const val AM = "AM"
    const val PM = "PM"
    const val TIME_FORMAT = "hh:mm"

    fun newInstance(
      timePickerId: Int,
      title: String, listener:
      CustomTimePickerDialog.OnTimeSetListener
    ) = TimePickerDialogFragment(timePickerId, title, listener)
  }
}

class CustomTimePickerDialog {

  interface OnTimeSetListener {
    fun onTimeSet(timePickerId: Int, timeString: String)
  }

  companion object {
    const val START_TIME_PICKER_ID = 1
    const val END_TIME_PICKER_ID = 2
  }
}