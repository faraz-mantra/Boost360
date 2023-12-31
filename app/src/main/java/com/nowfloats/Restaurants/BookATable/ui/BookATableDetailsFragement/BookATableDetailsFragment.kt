package com.nowfloats.Restaurants.BookATable.ui.BookATableDetailsFragement


import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.nowfloats.Login.UserSessionManager
import com.nowfloats.Restaurants.API.RestaurantsAPIInterfaces
import com.nowfloats.Restaurants.API.model.AddBookTable.ActionData
import com.nowfloats.Restaurants.API.model.AddBookTable.AddBookTableData
import com.nowfloats.Restaurants.API.model.DeleteBookTable.DeleteBookTableData
import com.nowfloats.Restaurants.API.model.GetBookTable.Data
import com.nowfloats.Restaurants.API.model.UpdateBookTable.UpdateBookTableData
import com.nowfloats.util.Key_Preferences
import com.nowfloats.util.Methods
import com.thinksity.R
import kotlinx.android.synthetic.main.book_a_table_details_fragment.*
import kotlinx.android.synthetic.main.new_custome_app_bar.*
import retrofit.Callback
import retrofit.RestAdapter
import retrofit.RetrofitError
import retrofit.android.AndroidLog
import retrofit.client.Response
import retrofit.converter.GsonConverter
import java.text.SimpleDateFormat
import java.util.*

class BookATableDetailsFragment : Fragment() {

  private var dayString: String = ""
  private var isSelectedWorkingDay: Boolean = false
  private var isSelectedWorkingHour: Boolean = false
  var ScreenType = ""
  var itemId = ""
  val countList = arrayOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10")
  var session: UserSessionManager? = null
  lateinit var existingItemData: Data
  var totalPeople = "1"
  val myCalendar = Calendar.getInstance()

  companion object {
    fun newInstance() = BookATableDetailsFragment()
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.book_a_table_details_fragment, container, false)
  }


  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    session = UserSessionManager(requireContext(), requireActivity())

    val adapter: ArrayAdapter<String> =
      ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, countList)
    table_count.setAdapter(adapter)

    table_count.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
      override fun onNothingSelected(parent: AdapterView<*>?) {

      }

      override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        totalPeople = countList[position]
      }

    }

    val date = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
      myCalendar.set(Calendar.YEAR, year)
      myCalendar.set(Calendar.MONTH, monthOfYear)
      myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

      val simpleDateFormat = SimpleDateFormat("EEEE")
      val date = Date(year, monthOfYear, dayOfMonth - 1)
      dayString = simpleDateFormat.format(date) //returns true day name for current month only

      isSelectedWorkingDay = checkIfWorkingDay(dayString)

      if (isSelectedWorkingDay.not())
        Toast.makeText(activity, "Selected day is not working day.", Toast.LENGTH_LONG).show()

      updateLabel()
    }

    date_value.setOnClickListener {
      Methods.hideKeyboard(requireContext())
      val datePickerDialog = DatePickerDialog(
        requireContext(), date, myCalendar
          .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
        myCalendar.get(Calendar.DAY_OF_MONTH)
      )
      datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
      datePickerDialog.show()
    }

    val time = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
      val c = Calendar.getInstance()
      myCalendar[Calendar.HOUR_OF_DAY] = hour
      myCalendar[Calendar.MINUTE] = minute

      if (myCalendar.timeInMillis >= c.timeInMillis) {
        //it's after current
        // hour = 15
         val string = session!!.getFPDetails(Key_Preferences.GET_FP_DETAILS_MONDAY_START_TIME)
        isSelectedWorkingHour = checkIfTimeIsInWorkingHour(hour, minute)
        if (isSelectedWorkingHour)
          time_value.setText(SimpleDateFormat("hh:mm aa").format(myCalendar.time))
        else
          Toast.makeText(activity, "Selected time is not working hour.", Toast.LENGTH_LONG).show()
      } else {
        //it's before current'
        Toast.makeText(context, "Please select valid timings.", Toast.LENGTH_LONG).show()
      }

    }

    time_value.setOnClickListener {
      Methods.hideKeyboard(requireContext())
      TimePickerDialog(
        context,
        time,
        myCalendar.get(Calendar.HOUR_OF_DAY),
        myCalendar.get(Calendar.MINUTE),
        false
      ).show()
    }

    //setheader
    setHeader()

    save_button.setOnClickListener {
      if (isSelectedWorkingDay && isSelectedWorkingHour) {
        if (ScreenType != null && ScreenType.equals("edit")) {
          updateExistingTeamsAPI()
        } else {
          createNewTeamsAPI()
        }
      } else {
        Toast.makeText(activity, "Selected date or time is not valid", Toast.LENGTH_LONG).show()
      }
    }

    ScreenType = arguments?.getString("ScreenState")?:""
    if (ScreenType != null && ScreenType.equals("edit")) {
      displayData()
    }
  }

  private fun checkIfTimeIsInWorkingHour(hour: Int, minute: Int): Boolean {
    var returnValue = false
    val pattern = "hh:mma"
    val patternSelected = "HH:mm"
    val sdfSelected = SimpleDateFormat(patternSelected)
    val dateStartTimeSElected = sdfSelected.parse("$hour:$minute")

    try {
      when(dayString) {
        "Monday" -> {
          val startTime = session!!.getFPDetails(Key_Preferences.GET_FP_DETAILS_MONDAY_START_TIME)
          val sdf = SimpleDateFormat(pattern)
          val dateStartTime = sdf.parse(startTime)

          val endTime = session!!.getFPDetails(Key_Preferences.GET_FP_DETAILS_MONDAY_END_TIME)
          val dateEndTime = sdf.parse(endTime)

          returnValue =  !(dateStartTimeSElected!!.before(dateStartTime) || dateStartTimeSElected.after(dateEndTime))
        }

        "Tuesday" -> {
          val startTime = session!!.getFPDetails(Key_Preferences.GET_FP_DETAILS_TUESDAY_START_TIME)
          val sdf = SimpleDateFormat(pattern)
          val dateStartTime = sdf.parse(startTime)

          val endTime = session!!.getFPDetails(Key_Preferences.GET_FP_DETAILS_TUESDAY_END_TIME)
          val dateEndTime = sdf.parse(endTime)

          returnValue =  !(dateStartTimeSElected!!.before(dateStartTime) || dateStartTimeSElected.after(dateEndTime))
        }

        "Wednesday" -> {
          val startTime = session!!.getFPDetails(Key_Preferences.GET_FP_DETAILS_WEDNESDAY_START_TIME)
          val sdf = SimpleDateFormat(pattern)
          val dateStartTime = sdf.parse(startTime)

          val endTime = session!!.getFPDetails(Key_Preferences.GET_FP_DETAILS_WEDNESDAY_END_TIME)
          val dateEndTime = sdf.parse(endTime)

          returnValue =  !(dateStartTimeSElected!!.before(dateStartTime) || dateStartTimeSElected.after(dateEndTime))
        }

        "Thursday" -> {
          val startTime = session!!.getFPDetails(Key_Preferences.GET_FP_DETAILS_THURSDAY_START_TIME)
          val sdf = SimpleDateFormat(pattern)
          val dateStartTime = sdf.parse(startTime)

          val endTime = session!!.getFPDetails(Key_Preferences.GET_FP_DETAILS_THURSDAY_END_TIME)
          val dateEndTime = sdf.parse(endTime)

          returnValue =  !(dateStartTimeSElected!!.before(dateStartTime) || dateStartTimeSElected.after(dateEndTime))
        }

        "Friday" -> {
          val startTime = session!!.getFPDetails(Key_Preferences.GET_FP_DETAILS_FRIDAY_START_TIME)
          val sdf = SimpleDateFormat(pattern)
          val dateStartTime = sdf.parse(startTime)

          val endTime = session!!.getFPDetails(Key_Preferences.GET_FP_DETAILS_FRIDAY_END_TIME)
          val dateEndTime = sdf.parse(endTime)

          returnValue =  !(dateStartTimeSElected!!.before(dateStartTime) || dateStartTimeSElected.after(dateEndTime))
        }

        "Saturday" -> {
          val startTime = session!!.getFPDetails(Key_Preferences.GET_FP_DETAILS_SATURDAY_START_TIME)
          val sdf = SimpleDateFormat(pattern)
          val dateStartTime = sdf.parse(startTime)

          val endTime = session!!.getFPDetails(Key_Preferences.GET_FP_DETAILS_SATURDAY_END_TIME)
          val dateEndTime = sdf.parse(endTime)

          returnValue =  !(dateStartTimeSElected!!.before(dateStartTime) || dateStartTimeSElected.after(dateEndTime))
        }

        "Sunday" -> {
          val startTime = session!!.getFPDetails(Key_Preferences.GET_FP_DETAILS_SUNDAY_START_TIME)
          val sdf = SimpleDateFormat(pattern)
          val dateStartTime = sdf.parse(startTime)

          val endTime = session!!.getFPDetails(Key_Preferences.GET_FP_DETAILS_SUNDAY_END_TIME)
          val dateEndTime = sdf.parse(endTime)

          returnValue =  !(dateStartTimeSElected!!.before(dateStartTime) || dateStartTimeSElected.after(dateEndTime))
        }
      }
    } catch (ex: Exception) {
      Log.d("Exception", ex.message.toString())
    }

    return returnValue
  }

  private fun checkIfWorkingDay(dayOfMonth: String): Boolean {
    var returnValue = false
    when(dayOfMonth) {
      "Monday" -> {
        val string = session!!.getFPDetails(Key_Preferences.GET_FP_DETAILS_MONDAY_START_TIME)
        returnValue = string.toLowerCase().endsWith("am") || string.endsWith("pm")
      }

      "Tuesday" -> {
        val string = session!!.getFPDetails(Key_Preferences.GET_FP_DETAILS_TUESDAY_START_TIME)
        returnValue = string.toLowerCase().endsWith("am") || string.endsWith("pm")
      }

      "Wednesday" -> {
        val string = session!!.getFPDetails(Key_Preferences.GET_FP_DETAILS_WEDNESDAY_START_TIME)
        returnValue = string.toLowerCase().endsWith("am") || string.endsWith("pm")
      }

      "Thursday" -> {
        val string = session!!.getFPDetails(Key_Preferences.GET_FP_DETAILS_THURSDAY_START_TIME)
        returnValue = string.toLowerCase().endsWith("am") || string.endsWith("pm")
      }

      "Friday" -> {
        val string = session!!.getFPDetails(Key_Preferences.GET_FP_DETAILS_FRIDAY_START_TIME)
        returnValue = string.toLowerCase().endsWith("am") || string.endsWith("pm")
      }

      "Saturday" -> {
        val string = session!!.getFPDetails(Key_Preferences.GET_FP_DETAILS_SATURDAY_START_TIME)
        returnValue = string.toLowerCase().endsWith("am") || string.endsWith("pm")
      }

      "Sunday" -> {
        val string = session!!.getFPDetails(Key_Preferences.GET_FP_DETAILS_SUNDAY_START_TIME)
        returnValue = string.toLowerCase().endsWith("am") || string.endsWith("pm")
      }
    }
    return returnValue
  }

  fun displayData() {
    existingItemData = Gson().fromJson(arguments?.getString("data"), Data::class.java)

    itemId = existingItemData._id?:""

    user_name.setText(existingItemData.name)
    contact_number.setText(existingItemData.number)
    table_count.setSelection(Integer.parseInt(existingItemData.getTotalPeopleN()))
    date_value.setText(existingItemData.date)
    time_value.setText(existingItemData.time)
    message_value.setText(existingItemData.message)
  }

  private fun updateLabel() {
    val myFormat = "dd-MM-yy"
    val sdf = SimpleDateFormat(myFormat, Locale.US)
    date_value.setText(sdf.format(myCalendar.time))
  }

  fun setHeader() {
    title.text = "Table Details"
    right_icon.setImageResource(R.drawable.ic_delete_white_outerline)
    right_icon_layout.setOnClickListener(View.OnClickListener {
      if (ScreenType == "edit") {
        deleteRecord(itemId)
        return@OnClickListener
      }
      activity?.onBackPressed()
    })
    back_button.setOnClickListener { activity?.onBackPressed() }
  }

  fun createNewTeamsAPI() {
    try {
      if (validateInput()) {
        val actionData = ActionData(
          date_value.text.toString(),
          message_value.text.toString(),
          user_name.text.toString(),
          contact_number.text.toString(),
          time_value.text.toString(),
          totalPeople
        )

        val request = AddBookTableData(
          actionData,
          session!!.getFpTag()
        )
        val APICalls = RestAdapter.Builder()
          .setEndpoint("https://webaction.api.boostkit.dev")
          .setLogLevel(RestAdapter.LogLevel.FULL)
          .setLog(AndroidLog("ggg"))
          .build()
          .create(RestaurantsAPIInterfaces::class.java)

        APICalls.addBookTable(request, object : Callback<String?> {
          override fun success(s: String?, response: Response) {
            if (response.status != 200) {
              Toast.makeText(
                requireContext(),
                getString(R.string.something_went_wrong),
                Toast.LENGTH_SHORT
              ).show()
              return
            }
            Methods.showSnackBarPositive(requireActivity(), "Successfully Added Book Table Details")
            activity!!.onBackPressed()
          }

          override fun failure(error: RetrofitError) {
            Methods.showSnackBarNegative(
              requireActivity(),
              getString(R.string.something_went_wrong)
            )
          }
        })
      }
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  private fun validateInput(): Boolean {
    if (user_name.text.toString().isEmpty() || contact_number.text.toString().isEmpty()
      || date_value.text.toString().isEmpty() || time_value.text.toString().isEmpty()
    ) {
      Toast.makeText(requireContext(), "Fields are empty!!..", Toast.LENGTH_SHORT).show()
      return false
    }

    if (contact_number.text.toString().length != 10) {
      Toast.makeText(requireContext(), "Please provide 10 digit Mobile Number.", Toast.LENGTH_SHORT)
        .show()
      return false
    }

    if (totalPeople == "0"){
      Toast.makeText(requireContext(), "Please select Table for.", Toast.LENGTH_SHORT)
        .show()
      return false
    }


    return true
  }


  fun updateExistingTeamsAPI() {
    try {
      if (validateInput()) {
        val actionData = ActionData(
          date_value.text.toString(),
          message_value.text.toString(),
          user_name.text.toString(),
          contact_number.text.toString(),
          time_value.text.toString(),
          totalPeople
        )


        val requestBody = UpdateBookTableData(
          true,
          "{_id:'" + existingItemData._id + "'}",
          "{\$set :" + Gson().toJson(actionData) + "}"
        )
        val APICalls = RestAdapter.Builder()
          .setEndpoint("https://webaction.api.boostkit.dev")
          .setLogLevel(RestAdapter.LogLevel.FULL)
          .setLog(AndroidLog("ggg"))
          .build()
          .create(RestaurantsAPIInterfaces::class.java)

        APICalls.updateBookTable(requestBody, object : Callback<String?> {
          override fun success(s: String?, response: Response) {
            if (response.status != 200) {
              Toast.makeText(
                requireContext(),
                getString(R.string.something_went_wrong),
                Toast.LENGTH_SHORT
              ).show()
              return
            }
            Methods.showSnackBarPositive(
              requireActivity(),
              getString(R.string.successfully_updated_book_table_details)
            )
            requireActivity().onBackPressed()
          }

          override fun failure(error: RetrofitError) {
            if (error.response.status == 200) {
              Methods.showSnackBarPositive(
                requireActivity(),
                getString(R.string.successfully_updated_book_table_details)
              )
              requireActivity().onBackPressed()
            } else {
              Methods.showSnackBarNegative(
                requireActivity(),
                getString(R.string.something_went_wrong)
              )
            }
          }
        })
      }
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  fun deleteRecord(itemId: String) {
    try {
      val requestBody = DeleteBookTableData(
        "{_id:'" + itemId + "'}",
        "{\$set : {IsArchived: true }}"
      )

      val APICalls = RestAdapter.Builder()
        .setEndpoint("https://webaction.api.boostkit.dev")
        .setLogLevel(RestAdapter.LogLevel.FULL)
        .setLog(AndroidLog("ggg"))
        .setConverter(GsonConverter(GsonBuilder().setLenient().create()))
        .build()
        .create(RestaurantsAPIInterfaces::class.java)

      APICalls.deleteBookTable(requestBody, object : Callback<String?> {
        override fun success(data: String?, response: Response) {
          if (response != null && response.status == 200) {
            Log.d("deletePlacesAround ->", response.body.toString())
            Methods.showSnackBarPositive(requireActivity(), "Successfully Deleted.")
            activity!!.onBackPressed()
          } else {
            Methods.showSnackBarNegative(
              requireActivity(),
              getString(R.string.something_went_wrong)
            )
          }
        }

        override fun failure(error: RetrofitError) {
          if (error.response.status == 200) {
            Methods.showSnackBarPositive(
              requireActivity(),
              getString(R.string.successfully_deleted_)
            )
            activity!!.onBackPressed()
          } else {
            Methods.showSnackBarNegative(
              requireActivity(),
              getString(R.string.something_went_wrong)
            )
          }
        }
      })
    } catch (e: java.lang.Exception) {
      e.printStackTrace()
    }
  }

}