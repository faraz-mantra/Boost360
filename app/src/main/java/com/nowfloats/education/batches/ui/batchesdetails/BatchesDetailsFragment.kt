package com.nowfloats.education.batches.ui.batchesdetails

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.nowfloats.education.batches.BatchesActivity
import com.nowfloats.education.batches.model.Data
import com.nowfloats.education.helper.BaseFragment
import com.nowfloats.education.helper.Constants.SAVE
import com.nowfloats.education.helper.Constants.SUCCESS
import com.nowfloats.education.helper.Constants.TIME_PICKER_DIALOG_FRAGMENT
import com.nowfloats.education.helper.Constants.UPDATE
import com.nowfloats.education.helper.CustomTimePickerDialog
import com.nowfloats.education.helper.TimePickerDialogFragment
import com.nowfloats.util.Methods
import com.thinksity.R
import com.thinksity.databinding.BatchesDetailsBinding
import org.koin.android.ext.android.inject
import java.util.*

class BatchesDetailsFragment(private val batchesData: Data?) : BaseFragment(),
  CustomTimePickerDialog.OnTimeSetListener {

  constructor() : this(null)

  private val viewModel by inject<BatchesDetailsViewModel>()
  private lateinit var binding: BatchesDetailsBinding
  private var addUpdateBatch = false
  private var batchTiming: String = ""

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    binding = BatchesDetailsBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    when {
      batchesData != null -> {
        binding.batchedData = batchesData
        binding.addUpdateBatchButton.text = UPDATE
        addUpdateBatch = true
      }
      else -> {
        binding.batchedData = Data()
        binding.addUpdateBatchButton.text = SAVE
        addUpdateBatch = false
      }
    }

    setHeader(view)

    binding.commencementDate.setOnClickListener {
      openCalendar()
    }

    binding.courseTimingEditText.setOnClickListener {
      openTimePickerDialog(CustomTimePickerDialog.START_TIME_PICKER_ID, "Select start time", this)
    }


    binding.addUpdateBatchButton.setOnClickListener {
      Methods.hideKeyboard(requireActivity())
      when {
        validate() -> when {
          addUpdateBatch -> {
            showLoader("Updating batch")
            viewModel.updateUpcomingBatch(binding.batchedData as Data)
          }
          else -> {
            showLoader("Adding batch")
            viewModel.addUpcomingBatch(binding.batchedData as Data)
          }
        }
      }
    }

    initLiveDataObservables()
  }

  private fun setHeader(view: View) {
    val title: TextView = view.findViewById(R.id.title)
    val backButton: LinearLayout = view.findViewById(R.id.back_button)
    val rightButton: LinearLayout = view.findViewById(R.id.right_icon_layout)
    val rightIcon: ImageView = view.findViewById(R.id.right_icon)

    title.text = getString(R.string.batch_details)
    rightIcon.setImageResource(R.drawable.ic_delete_white_outerline)
    rightButton.setOnClickListener {
      if (addUpdateBatch) {
        showLoader(getString(R.string.deleting_batch))
        viewModel.deleteUpcomingBatch(batchesData as Data)
      } else {
        Toast.makeText(requireContext(), getString(R.string.no_batch_found), Toast.LENGTH_SHORT)
          .show()
      }
    }
    backButton.setOnClickListener { requireActivity().onBackPressed() }
  }

  private fun openCalendar() {
    val calendar = viewModel.calendar
    val dialog = DatePickerDialog(
      requireContext(), date, calendar
        .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
      calendar.get(Calendar.DAY_OF_MONTH)
    )
    dialog.datePicker.minDate = calendar.timeInMillis
    dialog.show()
  }

  private fun openTimePickerDialog(
    timePickerId: Int,
    title: String,
    listener: CustomTimePickerDialog.OnTimeSetListener
  ) {
    val dialogFragment = TimePickerDialogFragment.newInstance(timePickerId, title, listener)
    dialogFragment.show(requireActivity().supportFragmentManager, TIME_PICKER_DIALOG_FRAGMENT)
  }

  private fun initLiveDataObservables() {
    viewModel.apply {
      onDateSelected.observe(viewLifecycleOwner, androidx.lifecycle.Observer { dateString ->
        dateString?.let {
          binding.commencementDate.setText(it)
        }
      })

      errorResponse.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
        hideLoader()
        Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
      })

      addBatchResponse.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
        hideLoader()
        if (!it.isNullOrBlank()) {
          Toast.makeText(
            requireContext(),
            getString(R.string.batch_added_successfully),
            Toast.LENGTH_SHORT
          ).show()
          (activity as BatchesActivity).popFragmentFromBackStack()
        }
      })

      updateBatchResponse.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
        hideLoader()
        if (!it.isNullOrBlank()) {
          Toast.makeText(
            requireContext(),
            getString(R.string.batch_updated_successfully),
            Toast.LENGTH_SHORT
          ).show()
          (activity as BatchesActivity).popFragmentFromBackStack()
        }
      })

      deleteBatchResponse.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
        if (!it.isNullOrBlank()) {
          if (it == SUCCESS) {
            hideLoader()
            Toast.makeText(
              requireContext(),
              getString(R.string.batch_deleted_successfully),
              Toast.LENGTH_SHORT
            ).show()
            (activity as BatchesActivity).popFragmentFromBackStack()
          }
        }
      })
    }
  }

  private fun validate(): Boolean {
    when {
      binding.courseNameEditText.text.isNullOrBlank() -> {
        binding.courseNameEditText.error = getString(R.string.enter_valid_course_name)
        binding.courseNameEditText.requestFocus()
        return false
      }
      else -> {
        binding.courseNameEditText.error = null
      }
    }

    when {
      binding.courseTimingEditText.text.isNullOrBlank() -> {
        binding.courseTimingEditText.error = getString(R.string.enter_course_timing)
        showToast(getString(R.string.enter_course_timing))
        return false
      }
      else -> {
        binding.courseTimingEditText.error = null
      }
    }

    when {
      binding.courseDurationEditText.text.isNullOrBlank() -> {
        binding.courseDurationEditText.error = getString(R.string.enter_course_duration)
        binding.courseDurationEditText.requestFocus()
        return false
      }
      else -> {
        binding.courseDurationEditText.error = null
      }
    }

    when {
      binding.commencementDate.text.isNullOrBlank() -> {
        binding.commencementDate.error = getString(R.string.enter_valid_commencement_date)
        showToast(getString(R.string.enter_commencement_date))
        return false
      }
      else -> {
        binding.commencementDate.error = null
      }
    }
    return true
  }

  private val date = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
    viewModel.apply {
      calendar.set(Calendar.YEAR, year)
      calendar.set(Calendar.MONTH, monthOfYear)
      calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
      getDate()
    }
  }

  override fun onTimeSet(timePickerId: Int, timeString: String) {
    when (timePickerId) {
      CustomTimePickerDialog.START_TIME_PICKER_ID -> {
        batchTiming = timeString
        openTimePickerDialog(CustomTimePickerDialog.END_TIME_PICKER_ID, "Select end time", this)
      }
      CustomTimePickerDialog.END_TIME_PICKER_ID -> {
        if (viewModel.isTimeAfter(batchTiming, timeString)) {
          if (batchTiming != timeString) {
            batchTiming += " to $timeString"
            binding.courseTimingEditText.setText(batchTiming)
          } else {
            showToast(getString(R.string.start_time_and_end_time_should_not_be_same))
          }
        } else {
          showToast(getString(R.string.end_time_should_be_grater_than_start_time))
        }
      }
    }
  }

  companion object {
    fun newInstance(): BatchesDetailsFragment = BatchesDetailsFragment()
    fun newInstance(batchesData: Data): BatchesDetailsFragment = BatchesDetailsFragment(batchesData)
  }
}