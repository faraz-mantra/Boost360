package com.nowfloats.BusinessProfile.UI.UI.New

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.View.OnTouchListener
import android.widget.*
import androidx.appcompat.widget.Toolbar
import com.afollestad.materialdialogs.MaterialDialog
import com.appservice.base.AppBaseActivity
import com.framework.firebaseUtils.firestore.FirestoreManager
import com.framework.models.BaseViewModel
import com.framework.webengageconstant.*
import com.nowfloats.BusinessProfile.UI.API.UploadProfileAsyncTask
import com.nowfloats.Login.UserSessionManager
import com.nowfloats.signup.UI.Model.Get_FP_Details_Event
import com.nowfloats.signup.UI.Service.Get_FP_Details_Service
import com.nowfloats.util.*
import com.squareup.otto.Bus
import com.squareup.otto.Subscribe
import com.thinksity.R
import com.thinksity.databinding.ActivityBusinessHoursNewBinding
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import kotlinx.android.synthetic.main.activity_business_hours_new.view.*
import kotlinx.android.synthetic.main.business_hours_layout.view.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class BusinessHoursNewActivity : AppBaseActivity<ActivityBusinessHoursNewBinding, BaseViewModel>(),
    OnTouchListener, TimePickerDialog.OnTimeSetListener,
    View.OnClickListener, AdapterView.OnItemSelectedListener {

    var userSession: UserSessionManager? = null
    var currentId = View.NO_ID
    var bus: Bus? = null
    var pd: ProgressDialog? = null
    override var TAG = "BusinessHoursActivity"
    var toolbar: Toolbar? = null
    var titleTextView: TextView? = null

    override fun getLayout(): Int {
        return R.layout.activity_business_hours_new
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayout())
        Methods.isOnline(this@BusinessHoursNewActivity)
        userSession = UserSessionManager(applicationContext, this@BusinessHoursNewActivity)
        bus = BusProvider.getInstance().bus
        pd = ProgressDialog.show(this, "", getString(R.string.loading))
        pd!!.setCancelable(false)
        toolbar = findViewById<View>(R.id.app_bar) as Toolbar
        titleTextView = toolbar!!.findViewById<View>(R.id.titleTextView) as TextView
        titleTextView!!.setText(R.string.business_timings_n)
        setSupportActionBar(toolbar)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayShowHomeEnabled(true)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }
        val saveText = toolbar!!.findViewById<View>(R.id.saveTextView) as TextView
        saveText.visibility = View.GONE

        binding!!.btnSaveInfo.setOnClickListener(this)
        binding!!.root.chbx_same_time.setOnClickListener(this)

        binding!!.root.switch_sun.setOnClickListener(this)
        binding!!.root.switch_mon.setOnClickListener(this)
        binding!!.root.switch_tue.setOnClickListener(this)
        binding!!.root.switch_wed.setOnClickListener(this)
        binding!!.root.switch_thu.setOnClickListener(this)
        binding!!.root.switch_fri.setOnClickListener(this)
        binding!!.root.switch_sat.setOnClickListener(this)

        binding!!.root.et_sun_open.setOnTouchListener(this)
        binding!!.root.et_mon_open.setOnTouchListener(this)
        binding!!.root.et_tue_open.setOnTouchListener(this)
        binding!!.root.et_wed_open.setOnTouchListener(this)
        binding!!.root.et_thu_open.setOnTouchListener(this)
        binding!!.root.et_fri_open.setOnTouchListener(this)
        binding!!.root.et_sat_open.setOnTouchListener(this)
        binding!!.root.et_sun_close.setOnTouchListener(this)
        binding!!.root.et_mon_close.setOnTouchListener(this)
        binding!!.root.et_tue_close.setOnTouchListener(this)
        binding!!.root.et_wed_close.setOnTouchListener(this)
        binding!!.root.et_thu_close.setOnTouchListener(this)
        binding!!.root.et_fri_close.setOnTouchListener(this)
        binding!!.root.et_sat_close.setOnTouchListener(this)
        getFPDetails(userSession!!.fpid, Constants.clientId, bus!!)
    }

    private fun uploadbusinessTimingsInfo() {
        if (!isValidInput()) {
            Toast.makeText(
                this,
                this.getString(R.string.enter_valid_open_close_timing),
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        val defaultTime = "00"
        val sundayTime: String
        val mondayTime: String
        val tuesdayTime: String
        val wednesdayTime: String
        val thursdayTime: String
        val fridayTime: String
        val saturdayTime: String
        val profilesattr = arrayOfNulls<String>(20)
        profilesattr[0] = "TIME"

        /*The clinic must be open for at least one day. If all days closed, then it means the business hours are not set.*/
        var openAtleastOneDayFlag = false
        if (binding!!.root.switch_sun.isChecked) {
            sundayTime =
                binding!!.root.et_sun_open.text.toString()
                    .trim { it <= ' ' } + "," + binding!!.root.et_sun_close.text
                    .toString().trim { it <= ' ' }
            session.storeFPDetails(
                Key_Preferences.GET_FP_DETAILS_SUNDAY_START_TIME,
                binding!!.root.et_sun_open.text.toString().trim { it <= ' ' })
            session.storeFPDetails(
                Key_Preferences.GET_FP_DETAILS_SUNDAY_END_TIME,
                binding!!.root.et_sun_close.text.toString().trim { it <= ' ' })
            openAtleastOneDayFlag = true
        } else {
            sundayTime = "$defaultTime,$defaultTime"
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_SUNDAY_START_TIME, "00")
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_SUNDAY_END_TIME, "00")
        }
        if (binding!!.root.switch_mon.isChecked) {
            mondayTime =
                binding!!.root.et_mon_open.text.toString()
                    .trim { it <= ' ' } + "," + binding!!.root.et_mon_close.text
                    .toString().trim { it <= ' ' }
            session.storeFPDetails(
                Key_Preferences.GET_FP_DETAILS_MONDAY_START_TIME,
                binding!!.root.et_mon_open.text.toString().trim { it <= ' ' })
            session.storeFPDetails(
                Key_Preferences.GET_FP_DETAILS_MONDAY_END_TIME,
                binding!!.root.et_mon_close.text.toString().trim { it <= ' ' })
            openAtleastOneDayFlag = true
        } else {
            mondayTime = "$defaultTime,$defaultTime"
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_MONDAY_START_TIME, "00")
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_MONDAY_END_TIME, "00")
        }
        if (binding!!.root.switch_tue.isChecked) {
            tuesdayTime =
                binding!!.root.et_tue_open.text.toString()
                    .trim { it <= ' ' } + "," + binding!!.root.et_tue_close.text
                    .toString().trim { it <= ' ' }
            session.storeFPDetails(
                Key_Preferences.GET_FP_DETAILS_TUESDAY_START_TIME,
                binding!!.root.et_tue_open.text.toString().trim { it <= ' ' })
            session.storeFPDetails(
                Key_Preferences.GET_FP_DETAILS_TUESDAY_END_TIME,
                binding!!.root.et_tue_close.text.toString().trim { it <= ' ' })
            openAtleastOneDayFlag = true
        } else {
            tuesdayTime = "$defaultTime,$defaultTime"
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_TUESDAY_START_TIME, "00")
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_TUESDAY_END_TIME, "00")
        }
        if (binding!!.root.switch_wed.isChecked) {
            wednesdayTime =
                binding!!.root.et_wed_open.text.toString()
                    .trim { it <= ' ' } + "," + binding!!.root.et_wed_close.text
                    .toString().trim { it <= ' ' }
            session.storeFPDetails(
                Key_Preferences.GET_FP_DETAILS_WEDNESDAY_START_TIME,
                binding!!.root.et_wed_open.text.toString().trim { it <= ' ' })
            session.storeFPDetails(
                Key_Preferences.GET_FP_DETAILS_WEDNESDAY_END_TIME,
                binding!!.root.et_wed_close.text.toString().trim { it <= ' ' })
            openAtleastOneDayFlag = true
        } else {
            wednesdayTime = "$defaultTime,$defaultTime"
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_WEDNESDAY_START_TIME, "00")
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_WEDNESDAY_END_TIME, "00")
        }
        if (binding!!.root.switch_thu.isChecked) {
            thursdayTime =
                binding!!.root.et_thu_open.text.toString()
                    .trim { it <= ' ' } + "," + binding!!.root.et_thu_close.text
                    .toString().trim { it <= ' ' }
            session.storeFPDetails(
                Key_Preferences.GET_FP_DETAILS_THURSDAY_START_TIME,
                binding!!.root.et_thu_open.text.toString().trim { it <= ' ' })
            session.storeFPDetails(
                Key_Preferences.GET_FP_DETAILS_THURSDAY_END_TIME,
                binding!!.root.et_thu_close.text.toString().trim { it <= ' ' })
            openAtleastOneDayFlag = true
        } else {
            thursdayTime = "$defaultTime,$defaultTime"
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_THURSDAY_START_TIME, "00")
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_THURSDAY_END_TIME, "00")
        }
        if (binding!!.root.switch_fri.isChecked) {
            fridayTime =
                binding!!.root.et_fri_open.text.toString()
                    .trim { it <= ' ' } + "," + binding!!.root.et_fri_close.text
                    .toString().trim { it <= ' ' }
            session.storeFPDetails(
                Key_Preferences.GET_FP_DETAILS_FRIDAY_START_TIME,
                binding!!.root.et_fri_open.text.toString().trim { it <= ' ' })
            session.storeFPDetails(
                Key_Preferences.GET_FP_DETAILS_FRIDAY_END_TIME,
                binding!!.root.et_fri_close.text.toString().trim { it <= ' ' })
            openAtleastOneDayFlag = true
        } else {
            fridayTime = "$defaultTime,$defaultTime"
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_FRIDAY_START_TIME, "00")
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_FRIDAY_END_TIME, "00")
        }
        if (binding!!.root.switch_sat.isChecked) {
            saturdayTime =
                binding!!.root.et_sat_open.text.toString()
                    .trim { it <= ' ' } + "," + binding!!.root.et_sat_close.text
                    .toString().trim { it <= ' ' }
            session.storeFPDetails(
                Key_Preferences.GET_FP_DETAILS_SATURDAY_START_TIME,
                binding!!.root.et_sat_open.text.toString().trim { it <= ' ' })
            session.storeFPDetails(
                Key_Preferences.GET_FP_DETAILS_SATURDAY_END_TIME,
                binding!!.root.et_sat_close.text.toString().trim { it <= ' ' })
            openAtleastOneDayFlag = true
        } else {
            saturdayTime = "$defaultTime,$defaultTime"
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_SATURDAY_START_TIME, "00")
            session.storeFPDetails(Key_Preferences.GET_FP_DETAILS_SATURDAY_END_TIME, "00")
        }
        val offerObj = JSONObject()
        try {
            offerObj.put("key", "TIMINGS")
            offerObj.put(
                "value",
                sundayTime + "#" +
                        mondayTime + "#" +
                        tuesdayTime + "#" +
                        wednesdayTime + "#" +
                        thursdayTime + "#" +
                        fridayTime + "#" +
                        saturdayTime
            )
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val ja = JSONArray()
        val dayData = JSONObject()
        ja.put(offerObj)
        try {
            dayData.put("clientId", Constants.clientId)
            dayData.put("fpTag", userSession!!.fpTag)
            dayData.put("updates", ja)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        UploadProfileAsyncTask(
            this@BusinessHoursNewActivity, dayData, profilesattr
        ) { isSuccess: Boolean ->
            if (isSuccess) binding!!.root.btn_save_info.visibility = View.GONE
        }.execute()
        userSession!!.businessHours = openAtleastOneDayFlag
        onBusinessHourAddedOrUpdated(openAtleastOneDayFlag)
    }

    private fun isValidInput(): Boolean {
        if (binding!!.root.switch_sun.isChecked) {
            if (binding!!.root.et_sun_open.text.toString() == "00:00" || binding!!.root.et_sun_close.text
                    .toString() == "00:00" || isStartTimeAndCloseTimeEqual(
                    binding!!.root.et_sun_open.text.toString(),
                    binding!!.root.et_sun_close.text.toString()
                )
            ) return false
        }
        if (binding!!.root.switch_mon.isChecked) {
            if (binding!!.root.et_mon_open.text.toString() == "00:00" || binding!!.root.et_mon_close.text
                    .toString() == "00:00" || isStartTimeAndCloseTimeEqual(
                    binding!!.root.et_mon_open.text.toString(),
                    binding!!.root.et_mon_close.text.toString()
                )
            ) return false
        }
        if (binding!!.root.switch_tue.isChecked) {
            if (binding!!.root.et_tue_open.text.toString() == "00:00" || binding!!.root.et_tue_close.text
                    .toString() == "00:00" || isStartTimeAndCloseTimeEqual(
                    binding!!.root.et_tue_open.text.toString(),
                    binding!!.root.et_tue_close.text.toString()
                )
            ) return false
        }
        if (binding!!.root.switch_wed.isChecked) {
            if (binding!!.root.et_wed_open.text.toString() == "00:00" || binding!!.root.et_wed_close.text
                    .toString() == "00:00" || isStartTimeAndCloseTimeEqual(
                    binding!!.root.et_wed_open.text.toString(),
                    binding!!.root.et_wed_close.text.toString()
                )
            ) return false
        }
        if (binding!!.root.switch_thu.isChecked) {
            if (binding!!.root.et_thu_open.text.toString() == "00:00" || binding!!.root.et_thu_close.text
                    .toString() == "00:00" || isStartTimeAndCloseTimeEqual(
                    binding!!.root.et_thu_open.text.toString(),
                    binding!!.root.et_thu_close.text.toString()
                )
            ) return false
        }
        if (binding!!.root.switch_fri.isChecked) {
            if (binding!!.root.et_fri_open.text.toString() == "00:00" || binding!!.root.et_fri_close.text
                    .toString() == "00:00" || isStartTimeAndCloseTimeEqual(
                    binding!!.root.et_fri_open.text.toString(),
                    binding!!.root.et_fri_close.text.toString()
                )
            ) return false
        }
        if (binding!!.root.switch_sat.isChecked) {
            if (binding!!.root.et_sat_open.text.toString() == "00:00" || binding!!.root.et_sat_close.text
                    .toString() == "00:00" || isStartTimeAndCloseTimeEqual(
                    binding!!.root.et_sat_open.text.toString(),
                    binding!!.root.et_sat_close.text.toString()
                )
            ) return false
        }
        return true
    }

    private fun isStartTimeAndCloseTimeEqual(startTime: String, endTime: String): Boolean {
        return startTime == endTime
    }

    private fun onBusinessHourAddedOrUpdated(isAdded: Boolean) {
        val instance = FirestoreManager
        if (instance.getDrScoreData() != null && instance.getDrScoreData()!!.metricdetail != null) {
            instance.getDrScoreData()!!.metricdetail!!.boolean_add_business_hours = isAdded
            instance.updateDocument()
        }
    }

    private fun updateTimings() {
        var openOneFlag = false
        if (userSession!!.getFPDetails(Key_Preferences.GET_FP_DETAILS_MONDAY_START_TIME)
                .lowercase(Locale.getDefault()).endsWith("am")
            || userSession!!.getFPDetails(Key_Preferences.GET_FP_DETAILS_MONDAY_START_TIME)
                .lowercase(Locale.getDefault()).endsWith("pm")
        ) {
            binding!!.root.et_mon_open.setText(userSession!!.getFPDetails(Key_Preferences.GET_FP_DETAILS_MONDAY_START_TIME))
            binding!!.root.et_mon_close.setText(userSession!!.getFPDetails(Key_Preferences.GET_FP_DETAILS_MONDAY_END_TIME))
            openOneFlag = true
        } else {
            binding!!.root.switch_mon.isChecked = false
            setTextTimeOnSwitch(R.id.et_mon_open, R.id.et_mon_close, 0, 0, 0, 0)
        }
        if (userSession!!.getFPDetails(Key_Preferences.GET_FP_DETAILS_TUESDAY_START_TIME)
                .lowercase(Locale.getDefault()).endsWith("am")
            || userSession!!.getFPDetails(Key_Preferences.GET_FP_DETAILS_TUESDAY_START_TIME)
                .lowercase(Locale.getDefault()).endsWith("pm")
        ) {
            binding!!.root.et_tue_open.setText(userSession!!.getFPDetails(Key_Preferences.GET_FP_DETAILS_TUESDAY_START_TIME))
            binding!!.root.et_tue_close.setText(userSession!!.getFPDetails(Key_Preferences.GET_FP_DETAILS_TUESDAY_END_TIME))
            openOneFlag = true
        } else {
            binding!!.root.switch_tue.isChecked = false
            setTextTimeOnSwitch(R.id.et_tue_open, R.id.et_tue_close, 0, 0, 0, 0)
        }
        if (userSession!!.getFPDetails(Key_Preferences.GET_FP_DETAILS_WEDNESDAY_START_TIME)
                .lowercase(Locale.getDefault()).endsWith("am")
            || userSession!!.getFPDetails(Key_Preferences.GET_FP_DETAILS_WEDNESDAY_START_TIME)
                .lowercase(Locale.getDefault()).endsWith("pm")
        ) {
            binding!!.root.et_wed_open.setText(userSession!!.getFPDetails(Key_Preferences.GET_FP_DETAILS_WEDNESDAY_START_TIME))
            binding!!.root.et_wed_close.setText(userSession!!.getFPDetails(Key_Preferences.GET_FP_DETAILS_WEDNESDAY_END_TIME))
            openOneFlag = true
        } else {
            binding!!.root.switch_wed.isChecked = false
            setTextTimeOnSwitch(R.id.et_wed_open, R.id.et_wed_close, 0, 0, 0, 0)
        }
        if (userSession!!.getFPDetails(Key_Preferences.GET_FP_DETAILS_THURSDAY_START_TIME)
                .lowercase(Locale.getDefault()).endsWith("am")
            || userSession!!.getFPDetails(Key_Preferences.GET_FP_DETAILS_THURSDAY_START_TIME)
                .lowercase(Locale.getDefault()).endsWith("pm")
        ) {
            binding!!.root.et_thu_open.setText(userSession!!.getFPDetails(Key_Preferences.GET_FP_DETAILS_THURSDAY_START_TIME))
            binding!!.root.et_thu_close.setText(userSession!!.getFPDetails(Key_Preferences.GET_FP_DETAILS_THURSDAY_END_TIME))
            openOneFlag = true
        } else {
            binding!!.root.switch_thu.isChecked = false
            setTextTimeOnSwitch(R.id.et_thu_open, R.id.et_thu_close, 0, 0, 0, 0)
        }
        if (userSession!!.getFPDetails(Key_Preferences.GET_FP_DETAILS_FRIDAY_START_TIME)
                .lowercase(Locale.getDefault()).endsWith("am")
            || userSession!!.getFPDetails(Key_Preferences.GET_FP_DETAILS_FRIDAY_START_TIME)
                .lowercase(Locale.getDefault()).endsWith("pm")
        ) {
            binding!!.root.et_fri_open.setText(userSession!!.getFPDetails(Key_Preferences.GET_FP_DETAILS_FRIDAY_START_TIME))
            binding!!.root.et_fri_close.setText(userSession!!.getFPDetails(Key_Preferences.GET_FP_DETAILS_FRIDAY_END_TIME))
            openOneFlag = true
        } else {
            binding!!.root.switch_fri.isChecked = false
            setTextTimeOnSwitch(R.id.et_fri_open, R.id.et_fri_close, 0, 0, 0, 0)
        }
        if (userSession!!.getFPDetails(Key_Preferences.GET_FP_DETAILS_SATURDAY_START_TIME)
                .lowercase(Locale.getDefault()).endsWith("am")
            || userSession!!.getFPDetails(Key_Preferences.GET_FP_DETAILS_SATURDAY_START_TIME)
                .lowercase(Locale.getDefault()).endsWith("pm")
        ) {
            binding!!.root.et_sat_open.setText(userSession!!.getFPDetails(Key_Preferences.GET_FP_DETAILS_SATURDAY_START_TIME))
            binding!!.root.et_sat_close.setText(userSession!!.getFPDetails(Key_Preferences.GET_FP_DETAILS_SATURDAY_END_TIME))
            openOneFlag = true
        } else {
            binding!!.root.switch_sat.isChecked = false
            setTextTimeOnSwitch(R.id.et_sat_open, R.id.et_sat_close, 0, 0, 0, 0)
        }
        if (userSession!!.getFPDetails(Key_Preferences.GET_FP_DETAILS_SUNDAY_START_TIME)
                .lowercase(Locale.getDefault()).endsWith("am")
            || userSession!!.getFPDetails(Key_Preferences.GET_FP_DETAILS_SUNDAY_START_TIME)
                .lowercase(Locale.getDefault()).endsWith("pm")
        ) {
            binding!!.root.et_sun_open.setText(userSession!!.getFPDetails(Key_Preferences.GET_FP_DETAILS_SUNDAY_START_TIME))
            binding!!.root.et_sun_close.setText(userSession!!.getFPDetails(Key_Preferences.GET_FP_DETAILS_SUNDAY_END_TIME))
            openOneFlag = true
        } else {
            binding!!.root.switch_sun.isChecked = false
            setTextTimeOnSwitch(R.id.et_sun_open, R.id.et_sun_close, 0, 0, 0, 0)
        }
        onBusinessHourAddedOrUpdated(openOneFlag)
    }

    private fun timePicker() {
        val parentView = LayoutInflater.from(this).inflate(R.layout.dialog_with_spinner, null)
        val openSpinner = parentView.findViewById<View>(R.id.spinner_all_open) as Spinner
        val closeSpinner = parentView.findViewById<View>(R.id.spinner_all_close) as Spinner
        val checkBox = parentView.findViewById<View>(R.id.chbx_same_time) as CheckBox
        openSpinner.onItemSelectedListener = this
        closeSpinner.onItemSelectedListener = this
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.business_hours_arrays, R.layout.layout_simple_text_item
        )
        openSpinner.adapter = adapter
        closeSpinner.adapter = adapter
        val dialog = MaterialDialog.Builder(this)
            .customView(parentView, true)
            .autoDismiss(false)
            .title("Select time")
            .positiveText("Set")
            .negativeText("Cancel")
            .positiveColorRes(R.color.colorAccentLight)
            .negativeColorRes(R.color.black_4a4a4a)
            .onPositive { dialog, _ ->
                setSelectedTime(
                    checkBox.isChecked,
                    openSpinner.selectedItem.toString(),
                    closeSpinner.selectedItem.toString()
                )
                dialog.dismiss()
                currentId = View.NO_ID
                binding!!.btnSaveInfo.visibility = View.VISIBLE
            }
            .onNegative { dialog, _ ->
                currentId = View.NO_ID
                dialog.dismiss()
            }
            .build()
        dialog.show()
    }

    private fun setSelectedTime(isChecked: Boolean, openTime: String, closeTime: String) {
        if (isChecked) {
            binding!!.root.et_mon_close.setText(closeTime)
            binding!!.root.et_mon_open.setText(openTime)
            binding!!.root.et_tue_close.setText(closeTime)
            binding!!.root.et_tue_open.setText(openTime)
            binding!!.root.et_wed_close.setText(closeTime)
            binding!!.root.et_wed_open.setText(openTime)
            binding!!.root.et_thu_close.setText(closeTime)
            binding!!.root.et_thu_open.setText(openTime)
            binding!!.root.et_fri_close.setText(closeTime)
            binding!!.root.et_fri_open.setText(openTime)
            binding!!.root.et_sat_close.setText(closeTime)
            binding!!.root.et_sat_open.setText(openTime)
            binding!!.root.et_sun_close.setText(closeTime)
            binding!!.root.et_sun_open.setText(openTime)
        } else {
            when (currentId) {
                0 -> {
                    binding!!.root.et_mon_close.setText(closeTime)
                    binding!!.root.et_mon_open.setText(openTime)
                }
                1 -> {
                    binding!!.root.et_tue_close.setText(closeTime)
                    binding!!.root.et_tue_open.setText(openTime)
                }
                2 -> {
                    binding!!.root.et_wed_close.setText(closeTime)
                    binding!!.root.et_wed_open.setText(openTime)
                }
                3 -> {
                    binding!!.root.et_thu_close.setText(closeTime)
                    binding!!.root.et_thu_open.setText(openTime)
                }
                4 -> {
                    binding!!.root.et_fri_close.setText(closeTime)
                    binding!!.root.et_fri_open.setText(openTime)
                }
                5 -> {
                    binding!!.root.et_sat_close.setText(closeTime)
                    binding!!.root.et_sat_open.setText(openTime)
                }
                6 -> {
                    binding!!.root.et_sun_close.setText(closeTime)
                    binding!!.root.et_sun_open.setText(openTime)
                }
            }
        }
    }

    override fun onTimeSet(view: RadialPickerLayout?, hourOfDay: Int, minute: Int) {
        val s = convertToString(hourOfDay, minute)
        (findViewById<View>(currentId) as EditText).setText(s)
        currentId = View.NO_ID
    }

    private fun convertToString(hourOfDay: Int, minute: Int): String {
        return String.format(
            Locale.ENGLISH,
            "%02d:%02d",
            if (hourOfDay > 12) hourOfDay - 12 else hourOfDay,
            minute
        )
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View, event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            when (v.id) {
                R.id.et_mon_open, R.id.et_mon_close -> currentId = 0
                R.id.et_tue_open, R.id.et_tue_close -> currentId = 1
                R.id.et_wed_open, R.id.et_wed_close -> currentId = 2
                R.id.et_thu_open, R.id.et_thu_close -> currentId = 3
                R.id.et_fri_open, R.id.et_fri_close -> currentId = 4
                R.id.et_sat_open, R.id.et_sat_close -> currentId = 5
                R.id.et_sun_open, R.id.et_sun_close -> currentId = 6
            }
            timePicker()
            return true
        }
        return false
    }

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.menu_business_hours_new, menu)
//        return super.onCreateOptionsMenu(menu!!)
//    }

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        menuInflater.inflate(R.menu.menu_business_hours_new, menu)
//        return true
//    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId
        if (id == android.R.id.home) {
            //  NavUtils.navigateUpFromSameTask(this);
            // getSupportFragmentManager().popBackStack();
            finish()
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setTextTimeOnSwitch(
        openId: Int,
        closeId: Int,
        openHoursTime: Int,
        openMinuteTime: Int,
        closeHoursTime: Int,
        closeMinuteTime: Int
    ) {
        val openEdt = findViewById<View>(openId) as EditText
        val closeEdt = findViewById<View>(closeId) as EditText
        if (openId != -1) openEdt.setText(convertToString(openHoursTime, openMinuteTime))
        if (closeId != -1) closeEdt.setText(convertToString(closeHoursTime, closeMinuteTime))
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btn_save_info -> {
                MixPanelController.track(EventKeysWL.SAVE_CONTACT_INFO, null)
                if (Methods.isOnline(this@BusinessHoursNewActivity)) {
                    WebEngageController.trackEvent(BUSINESS_HOURS_SAVED, CLICK, userSession!!.fpTag)
                    uploadbusinessTimingsInfo()
                } else {
                    Methods.snackbarNoInternet(this@BusinessHoursNewActivity)
                }
            }
            R.id.switch_sun -> {
                if (!binding!!.root.switch_sun.isChecked) {
                    WebEngageController.trackEvent(SUNDAY_MARKED_OFF, TOGGLE_SUNDAY, NULL)
                    setTextTimeOnSwitch(R.id.et_sun_open, R.id.et_sun_close, 0, 0, 0, 0)
                } else {
                    WebEngageController.trackEvent(SUNDAY_MARKED_ON, TOGGLE_SUNDAY, NULL)
                }
                binding!!.root.btn_save_info.visibility = View.VISIBLE
            }
            R.id.switch_mon -> {
                if (!binding!!.root.switch_mon.isChecked) {
                    WebEngageController.trackEvent(MONDAY_MARKED_OFF, TOGGLE_MONDAY, NULL)
                    setTextTimeOnSwitch(R.id.et_mon_open, R.id.et_mon_close, 0, 0, 0, 0)
                } else {
                    WebEngageController.trackEvent(MONDAY_MARKED_ON, TOGGLE_MONDAY, NULL)
                }
                binding!!.root.btn_save_info.visibility = View.VISIBLE
            }
            R.id.switch_tue -> {
                if (!binding!!.root.switch_tue.isChecked) {
                    setTextTimeOnSwitch(R.id.et_tue_open, R.id.et_tue_close, 0, 0, 0, 0)
                    WebEngageController.trackEvent(TUESDAY_MARKED_OFF, TOGGLE_TUESDAY, NULL)
                } else {
                    WebEngageController.trackEvent(TUESDAY_MARKED_ON, TOGGLE_TUESDAY, NULL)
                }
                binding!!.root.btn_save_info.visibility = View.VISIBLE
            }
            R.id.switch_wed -> {
                if (!binding!!.root.switch_wed.isChecked) {
                    setTextTimeOnSwitch(R.id.et_wed_open, R.id.et_wed_close, 0, 0, 0, 0)
                    WebEngageController.trackEvent(WEDNESDAY_MARKED_OFF, TOGGLE_WEDNESDAY, NULL)
                } else {
                    WebEngageController.trackEvent(WEDNESDAY_MARKED_ON, TOGGLE_WEDNESDAY, NULL)
                }
                binding!!.root.btn_save_info.visibility = View.VISIBLE
            }
            R.id.switch_thu -> {
                if (!binding!!.root.switch_thu.isChecked) {
                    setTextTimeOnSwitch(R.id.et_thu_open, R.id.et_thu_close, 0, 0, 0, 0)
                    WebEngageController.trackEvent(THURSDAY_MARKED_OFF, TOGGLE_THURSDAY, NULL)
                } else {
                    WebEngageController.trackEvent(THURSDAY_MARKED_ON, TOGGLE_THURSDAY, NULL)
                }
                binding!!.root.btn_save_info.visibility = View.VISIBLE
            }
            R.id.switch_fri -> {
                if (!binding!!.root.switch_fri.isChecked) {
                    setTextTimeOnSwitch(R.id.et_fri_open, R.id.et_fri_close, 0, 0, 0, 0)
                    WebEngageController.trackEvent(FRIDAY_MARKED_OFF, TOGGLE_FRIDAY, NULL)
                } else {
                    WebEngageController.trackEvent(FRIDAY_MARKED_ON, TOGGLE_FRIDAY, NULL)
                }
                binding!!.root.btn_save_info.visibility = View.VISIBLE
            }
            R.id.switch_sat -> {
                if (!binding!!.root.switch_sat.isChecked) {
                    setTextTimeOnSwitch(R.id.et_sat_open, R.id.et_sat_close, 0, 0, 0, 0)
                    WebEngageController.trackEvent(SATURDAY_MARKED_OFF, TOGGLE_SATURDAY, NULL)
                } else {
                    WebEngageController.trackEvent(SATURDAY_MARKED_ON, TOGGLE_SATURDAY, NULL)
                }
                binding!!.root.btn_save_info.visibility = View.VISIBLE
            }
            R.id.chbx_same_time -> {
                if (binding!!.root.chbx_same_time.isChecked) {
                    WebEngageController.trackEvent(
                        STORE_TIMINGS,
                        CLICKING_USE_SAME_TIME_FOR_WHOLE_WEEK,
                        NULL
                    )
                    binding!!.root.ll_same_time.visibility = View.VISIBLE
                    findViewById<View>(R.id.et_all_close).setOnTouchListener(this@BusinessHoursNewActivity)
                    findViewById<View>(R.id.et_all_open).setOnTouchListener(this@BusinessHoursNewActivity)
                }
                binding!!.root.btn_save_info.visibility = View.VISIBLE
            }
        }
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
    }

    private fun getFPDetails(fpId: String, clientId: String, bus: Bus) {
        pd!!.show()
        Get_FP_Details_Service(this, fpId, clientId, bus)
    }

    @Subscribe
    fun post_getFPDetails(response: Get_FP_Details_Event?) {
        Log.i(this.TAG, "post_getFPDetails: ")
        runOnUiThread { if (pd != null) pd!!.dismiss() }
        updateTimings()
    }

    @Subscribe
    fun post_Error(message: String?) {
        runOnUiThread {
            if (pd != null) pd!!.dismiss()
            Toast.makeText(this, "Business hour loading error!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        try {
            bus!!.register(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            bus!!.unregister(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onStop() {
        super.onStop()
        try {
            bus!!.unregister(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}