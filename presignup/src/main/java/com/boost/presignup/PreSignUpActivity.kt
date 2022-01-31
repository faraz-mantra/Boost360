package com.boost.presignup

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.PopupWindow
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.boost.presignup.adapter.LanguageDropDownAdapter
import com.boost.presignup.databinding.ActivityPreSignUpLibBinding
import com.boost.presignup.datamodel.SharedViewModel
import com.boost.presignup.locale.LocaleManager
import com.boost.presignup.ui.MainFragment
import com.boost.presignup.ui.PopUpDialogFragment
import com.boost.presignup.utils.AppConstants.Companion.ENABLE_BOTTOM_VIEW
import com.boost.presignup.utils.AppConstants.Companion.SINGLE_LANGUAGE_BUTTON_VIEW
import com.boost.presignup.utils.WebEngageController
import com.framework.webengageconstant.*


class PreSignUpActivity : AppCompatActivity() {

  var initialLoad = true
  lateinit var binding: ActivityPreSignUpLibBinding
  private lateinit var viewModel: SharedViewModel

  lateinit var navHostFragment: NavHostFragment

  lateinit var langList: MutableList<String>
  lateinit var mPopupWindow: PopupWindow
  var dropDownStatus = false
  val popUpDialogFragment = PopUpDialogFragment()
  lateinit var localeManager: LocaleManager


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = DataBindingUtil.setContentView(this, R.layout.activity_pre_sign_up_lib)
    viewModel = ViewModelProviders.of(this).get(SharedViewModel::class.java)
    binding.viewModel = viewModel
    navHostFragment =
      supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment

    if(!packageName.equals(getString(R.string.package_name_sign_up_visibility))){
      binding.createAccountButton.visibility = View.VISIBLE
    }
    //custome navigation controller after language selection
    if (intent.hasExtra("fragmentState")) {
      val fragmetState = intent.getStringExtra("fragmentState")
      val inflater = navHostFragment.navController.navInflater
      val graph = inflater.inflate(R.navigation.main_navigation)
      if (fragmetState!!.equals("dropdown")) {
        graph.setStartDestination(R.id.videoPlayerFragment)
        navHostFragment.navController.graph = graph
        navHostFragment.navController.navigate(R.id.introFragment)
      } else {
        graph.setStartDestination(R.id.videoPlayerFragment)
        navHostFragment.navController.graph = graph
      }

    }

    langList = mutableListOf<String>(
      resources.getString(R.string.english),
      resources.getString(R.string.hindi),
      resources.getString(R.string.telugu),
      resources.getString(R.string.tamil)
//            resources.getString(R.string.kannada),
//            resources.getString(R.string.malayalam),
//            resources.getString(R.string.marathi)
    )
    binding.languageDropdown.setOnClickListener {
      dropDownStatus = !dropDownStatus
      if (dropDownStatus) {
        mPopupWindow = getPopupWindow()
        mPopupWindow.showAsDropDown(it, it.width / 4, -(it.height * langList.size))
      } else {
        if (::mPopupWindow.isInitialized) {
          mPopupWindow.dismiss()
          dropDownStatus = false
        }
      }
    }

    binding.createAccountButton.setOnClickListener {
      createNewEmailSignUp()
//      WebEngageController.trackEvent("PS_Clicked Create account", "create account clicked", "")
//      popUpDialogFragment.show(supportFragmentManager, "popUpDialogFragment_tag")
    }

    binding.loginButton.setOnClickListener {
      WebEngageController.trackEvent(PS_INTRO_SCREEN_START, GET_START_CLICKED, NO_EVENT_VALUE)
      val intent =
        Intent(applicationContext, Class.forName("com.nowfloats.Login.Login_MainActivity"))
      startActivity(intent)
      //PresignupManager.getListener()?.loginClicked(activityContext)
    }

    setUpMvvm()
    languageButtonSelection()
    checkIsBottomSheetSignUpOpen(intent.extras)

  }

  private fun checkIsBottomSheetSignUpOpen(extras: Bundle?) {
    if (extras != null) {
      val isSignUpBottomSheet = extras.getBoolean("isSignUpBottomSheet")
      if (isSignUpBottomSheet) {
        createNewEmailSignUp()
//        popUpDialogFragment.show(supportFragmentManager, "popUpDialogFragment_tag")
      }
    }
  }

  private fun createNewEmailSignUp() {
    val intent = Intent(this, SignUpActivity::class.java)
    intent.putExtra("provider", "EMAIL")
    startActivity(intent)
    WebEngageController.trackEvent(
      PS_AUTH_PROVIDER_SUCCESS_EMAIL,
      PROVIDER_SUCCESS_EMAIL,
      NO_EVENT_VALUE
    )
  }

  private fun getPopupWindow(): PopupWindow {
    val popupWindow: PopupWindow
    val listView: View = LayoutInflater.from(baseContext)
      .inflate(R.layout.language_recyclerview, null)
    val mAdapter = LanguageDropDownAdapter(this, langList,
      object : LanguageDropDownAdapter.RecyclerViewClickListener {
        override fun onClick(viewHolder: LanguageDropDownAdapter.ViewHolder, itemPos: Int) {
          if (::mPopupWindow.isInitialized) {
            mPopupWindow.dismiss()
            dropDownStatus = false
          }
          binding.languageDropdownText.text = langList.get(itemPos)
          binding.viewModel?.LanguageSelection(langList.get(itemPos))
          WebEngageController.trackEvent(
            PS_LANGUAGE_CHANGED_TO + langList.get(itemPos),
            LANGUAGE_CHANGED_TO + langList.get(itemPos),
            NO_EVENT_VALUE
          )
        }
      })
    listView.setOnClickListener {
      if (::mPopupWindow.isInitialized) {
        mPopupWindow.dismiss()
        dropDownStatus = false
      }
    }
    val rvPopup: RecyclerView = listView.findViewById(R.id.language_recyclerview_list)

    rvPopup.setHasFixedSize(false)
    rvPopup.layoutManager = LinearLayoutManager(applicationContext)
    rvPopup.adapter = mAdapter
    popupWindow = PopupWindow(
      listView,
      WindowManager.LayoutParams.MATCH_PARENT,
      WindowManager.LayoutParams.MATCH_PARENT
    )
    if (Build.VERSION.SDK_INT >= 21) {
      popupWindow.elevation = 5.0f
    }
    return popupWindow
  }

  private fun getCreateAccountPopupWindow(): PopupWindow {
    val popupWindow: PopupWindow
    val listView: View = LayoutInflater.from(baseContext)
      .inflate(R.layout.curve_popup_layout, null)
    val mAdapter = LanguageDropDownAdapter(this, langList,
      object : LanguageDropDownAdapter.RecyclerViewClickListener {
        override fun onClick(viewHolder: LanguageDropDownAdapter.ViewHolder, itemPos: Int) {
          if (::mPopupWindow.isInitialized) {
            mPopupWindow.dismiss()
            dropDownStatus = false
          }
          binding.languageDropdownText.text = langList.get(itemPos)
          binding.viewModel?.LanguageSelection(langList.get(itemPos))
        }
      })
    listView.setOnClickListener {
      if (::mPopupWindow.isInitialized) {
        mPopupWindow.dismiss()
        dropDownStatus = false
      }
    }
    val rvPopup: RecyclerView = listView.findViewById(R.id.language_recyclerview_list)

    rvPopup.setHasFixedSize(false)
    rvPopup.layoutManager = LinearLayoutManager(applicationContext)
    rvPopup.adapter = mAdapter
    popupWindow = PopupWindow(
      listView,
      WindowManager.LayoutParams.MATCH_PARENT,
      WindowManager.LayoutParams.MATCH_PARENT
    )
    if (Build.VERSION.SDK_INT >= 21) {
      popupWindow.elevation = 5.0f
    }
    return popupWindow
  }

  fun languageButtonSelection() {
    binding.englishButton.setOnClickListener {
      WebEngageController.trackEvent(
        PS_LANGUAGE_CHANGED_TO + langList.get(0),
        LANGUAGE_CHANGED_TO + langList.get(0),
        ""
      )
      if (!binding.viewModel!!.languageSelected.equals(langList.get(0))) {
        styleLanguageButton(it.id)
        binding.viewModel?.LanguageSelection(langList.get(0))
      }
    }
    binding.hindiButton.setOnClickListener {
      WebEngageController.trackEvent(
        PS_LANGUAGE_CHANGED_TO + langList.get(1),
        LANGUAGE_CHANGED_TO + langList.get(1),
        ""
      )
      if (!binding.viewModel!!.languageSelected.equals(langList.get(1))) {
        styleLanguageButton(it.id)
        binding.viewModel?.LanguageSelection(langList.get(1))
      }
    }
    binding.teluguButton.setOnClickListener {
      WebEngageController.trackEvent(
        PS_LANGUAGE_CHANGED_TO + langList.get(2),
        LANGUAGE_CHANGED_TO + langList.get(2),
        ""
      )
      if (!binding.viewModel!!.languageSelected.equals(langList.get(2))) {
        styleLanguageButton(it.id)
        binding.viewModel?.LanguageSelection(langList.get(2))
      }
    }
    binding.tamilButton.setOnClickListener {
      WebEngageController.trackEvent(
        PS_LANGUAGE_CHANGED_TO + langList.get(3),
        LANGUAGE_CHANGED_TO + langList.get(3),
        ""
      )
      if (!binding.viewModel!!.languageSelected.equals(langList.get(3))) {
        styleLanguageButton(it.id)
        binding.viewModel?.LanguageSelection(langList.get(3))
      }
    }
    binding.kannadaButton.setOnClickListener {
      if (!binding.viewModel!!.languageSelected.equals(langList.get(4))) {
        styleLanguageButton(it.id)
        binding.viewModel?.LanguageSelection(langList.get(4))
      }
    }
    binding.malayalamButton.setOnClickListener {
      if (!binding.viewModel!!.languageSelected.equals(langList.get(5))) {
        styleLanguageButton(it.id)
        binding.viewModel?.LanguageSelection(langList.get(5))
      }
    }
    binding.maratiButton.setOnClickListener {
      if (!binding.viewModel!!.languageSelected.equals(langList.get(6))) {
        styleLanguageButton(it.id)
        binding.viewModel?.LanguageSelection(langList.get(6))
      }
    }
  }


  fun styleLanguageButton(button: Int) {

    when (button) {
      R.id.english_button -> {
        binding.englishButton.background =
          ResourcesCompat.getDrawable(resources, R.drawable.selected_language_button_style, theme)
        binding.englishButton.setTextColor(
          ResourcesCompat.getColor(
            resources,
            R.color.red_color,
            theme
          )
        )

        binding.hindiButton.background =
          ResourcesCompat.getDrawable(
            resources,
            R.drawable.un_selected_language_button_style,
            theme
          )
        binding.hindiButton.setTextColor(ResourcesCompat.getColor(resources, R.color.black, theme))

        binding.kannadaButton.background =
          ResourcesCompat.getDrawable(
            resources,
            R.drawable.un_selected_language_button_style,
            theme
          )
        binding.kannadaButton.setTextColor(
          ResourcesCompat.getColor(
            resources,
            R.color.black,
            theme
          )
        )

        binding.teluguButton.background =
          ResourcesCompat.getDrawable(
            resources,
            R.drawable.un_selected_language_button_style,
            theme
          )
        binding.teluguButton.setTextColor(ResourcesCompat.getColor(resources, R.color.black, theme))

        binding.malayalamButton.background =
          ResourcesCompat.getDrawable(
            resources,
            R.drawable.un_selected_language_button_style,
            theme
          )
        binding.malayalamButton.setTextColor(
          ResourcesCompat.getColor(
            resources,
            R.color.black,
            theme
          )
        )

        binding.tamilButton.background =
          ResourcesCompat.getDrawable(
            resources,
            R.drawable.un_selected_language_button_style,
            theme
          )
        binding.tamilButton.setTextColor(ResourcesCompat.getColor(resources, R.color.black, theme))

        binding.maratiButton.background =
          ResourcesCompat.getDrawable(
            resources,
            R.drawable.un_selected_language_button_style,
            theme
          )
        binding.maratiButton.setTextColor(ResourcesCompat.getColor(resources, R.color.black, theme))
      }
      R.id.hindi_button -> {
        binding.englishButton.background =
          ResourcesCompat.getDrawable(
            resources,
            R.drawable.un_selected_language_button_style,
            theme
          )
        binding.englishButton.setTextColor(
          ResourcesCompat.getColor(
            resources,
            R.color.black,
            theme
          )
        )

        binding.hindiButton.background =
          ResourcesCompat.getDrawable(resources, R.drawable.selected_language_button_style, theme)
        binding.hindiButton.setTextColor(
          ResourcesCompat.getColor(
            resources,
            R.color.red_color,
            theme
          )
        )

        binding.kannadaButton.background =
          ResourcesCompat.getDrawable(
            resources,
            R.drawable.un_selected_language_button_style,
            theme
          )
        binding.kannadaButton.setTextColor(
          ResourcesCompat.getColor(
            resources,
            R.color.black,
            theme
          )
        )

        binding.teluguButton.background =
          ResourcesCompat.getDrawable(
            resources,
            R.drawable.un_selected_language_button_style,
            theme
          )
        binding.teluguButton.setTextColor(ResourcesCompat.getColor(resources, R.color.black, theme))

        binding.malayalamButton.background =
          ResourcesCompat.getDrawable(
            resources,
            R.drawable.un_selected_language_button_style,
            theme
          )
        binding.malayalamButton.setTextColor(
          ResourcesCompat.getColor(
            resources,
            R.color.black,
            theme
          )
        )

        binding.tamilButton.background =
          ResourcesCompat.getDrawable(
            resources,
            R.drawable.un_selected_language_button_style,
            theme
          )
        binding.tamilButton.setTextColor(ResourcesCompat.getColor(resources, R.color.black, theme))

        binding.maratiButton.background =
          ResourcesCompat.getDrawable(
            resources,
            R.drawable.un_selected_language_button_style,
            theme
          )
        binding.maratiButton.setTextColor(ResourcesCompat.getColor(resources, R.color.black, theme))
      }
      R.id.kannada_button -> {
        binding.englishButton.background =
          ResourcesCompat.getDrawable(
            resources,
            R.drawable.un_selected_language_button_style,
            theme
          )
        binding.englishButton.setTextColor(
          ResourcesCompat.getColor(
            resources,
            R.color.black,
            theme
          )
        )

        binding.hindiButton.background =
          ResourcesCompat.getDrawable(
            resources,
            R.drawable.un_selected_language_button_style,
            theme
          )
        binding.hindiButton.setTextColor(ResourcesCompat.getColor(resources, R.color.black, theme))

        binding.kannadaButton.background =
          ResourcesCompat.getDrawable(resources, R.drawable.selected_language_button_style, theme)
        binding.kannadaButton.setTextColor(
          ResourcesCompat.getColor(
            resources,
            R.color.red_color,
            theme
          )
        )

        binding.teluguButton.background =
          ResourcesCompat.getDrawable(
            resources,
            R.drawable.un_selected_language_button_style,
            theme
          )
        binding.teluguButton.setTextColor(ResourcesCompat.getColor(resources, R.color.black, theme))

        binding.malayalamButton.background =
          ResourcesCompat.getDrawable(
            resources,
            R.drawable.un_selected_language_button_style,
            theme
          )
        binding.malayalamButton.setTextColor(
          ResourcesCompat.getColor(
            resources,
            R.color.black,
            theme
          )
        )

        binding.tamilButton.background =
          ResourcesCompat.getDrawable(
            resources,
            R.drawable.un_selected_language_button_style,
            theme
          )
        binding.tamilButton.setTextColor(ResourcesCompat.getColor(resources, R.color.black, theme))

        binding.maratiButton.background =
          ResourcesCompat.getDrawable(
            resources,
            R.drawable.un_selected_language_button_style,
            theme
          )
        binding.maratiButton.setTextColor(ResourcesCompat.getColor(resources, R.color.black, theme))
      }
      R.id.telugu_button -> {
        binding.englishButton.background =
          ResourcesCompat.getDrawable(
            resources,
            R.drawable.un_selected_language_button_style,
            theme
          )
        binding.englishButton.setTextColor(
          ResourcesCompat.getColor(
            resources,
            R.color.black,
            theme
          )
        )

        binding.hindiButton.background =
          ResourcesCompat.getDrawable(
            resources,
            R.drawable.un_selected_language_button_style,
            theme
          )
        binding.hindiButton.setTextColor(ResourcesCompat.getColor(resources, R.color.black, theme))

        binding.kannadaButton.background =
          ResourcesCompat.getDrawable(
            resources,
            R.drawable.un_selected_language_button_style,
            theme
          )
        binding.kannadaButton.setTextColor(
          ResourcesCompat.getColor(
            resources,
            R.color.black,
            theme
          )
        )

        binding.teluguButton.background =
          ResourcesCompat.getDrawable(resources, R.drawable.selected_language_button_style, theme)
        binding.teluguButton.setTextColor(
          ResourcesCompat.getColor(
            resources,
            R.color.red_color,
            theme
          )
        )

        binding.malayalamButton.background =
          ResourcesCompat.getDrawable(
            resources,
            R.drawable.un_selected_language_button_style,
            theme
          )
        binding.malayalamButton.setTextColor(
          ResourcesCompat.getColor(
            resources,
            R.color.black,
            theme
          )
        )

        binding.tamilButton.background =
          ResourcesCompat.getDrawable(
            resources,
            R.drawable.un_selected_language_button_style,
            theme
          )
        binding.tamilButton.setTextColor(ResourcesCompat.getColor(resources, R.color.black, theme))

        binding.maratiButton.background =
          ResourcesCompat.getDrawable(
            resources,
            R.drawable.un_selected_language_button_style,
            theme
          )
        binding.maratiButton.setTextColor(ResourcesCompat.getColor(resources, R.color.black, theme))
      }
      R.id.malayalam_button -> {
        binding.englishButton.background =
          ResourcesCompat.getDrawable(
            resources,
            R.drawable.un_selected_language_button_style,
            theme
          )
        binding.englishButton.setTextColor(
          ResourcesCompat.getColor(
            resources,
            R.color.black,
            theme
          )
        )

        binding.hindiButton.background =
          ResourcesCompat.getDrawable(
            resources,
            R.drawable.un_selected_language_button_style,
            theme
          )
        binding.hindiButton.setTextColor(ResourcesCompat.getColor(resources, R.color.black, theme))

        binding.kannadaButton.background =
          ResourcesCompat.getDrawable(
            resources,
            R.drawable.un_selected_language_button_style,
            theme
          )
        binding.kannadaButton.setTextColor(
          ResourcesCompat.getColor(
            resources,
            R.color.black,
            theme
          )
        )

        binding.teluguButton.background =
          ResourcesCompat.getDrawable(
            resources,
            R.drawable.un_selected_language_button_style,
            theme
          )
        binding.teluguButton.setTextColor(ResourcesCompat.getColor(resources, R.color.black, theme))

        binding.malayalamButton.background =
          ResourcesCompat.getDrawable(resources, R.drawable.selected_language_button_style, theme)
        binding.malayalamButton.setTextColor(
          ResourcesCompat.getColor(
            resources,
            R.color.red_color,
            theme
          )
        )

        binding.tamilButton.background =
          ResourcesCompat.getDrawable(
            resources,
            R.drawable.un_selected_language_button_style,
            theme
          )
        binding.tamilButton.setTextColor(ResourcesCompat.getColor(resources, R.color.black, theme))

        binding.maratiButton.background =
          ResourcesCompat.getDrawable(
            resources,
            R.drawable.un_selected_language_button_style,
            theme
          )
        binding.maratiButton.setTextColor(ResourcesCompat.getColor(resources, R.color.black, theme))

      }
      R.id.tamil_button -> {
        binding.englishButton.background =
          ResourcesCompat.getDrawable(
            resources,
            R.drawable.un_selected_language_button_style,
            theme
          )
        binding.englishButton.setTextColor(
          ResourcesCompat.getColor(
            resources,
            R.color.black,
            theme
          )
        )

        binding.hindiButton.background =
          ResourcesCompat.getDrawable(
            resources,
            R.drawable.un_selected_language_button_style,
            theme
          )
        binding.hindiButton.setTextColor(ResourcesCompat.getColor(resources, R.color.black, theme))

        binding.kannadaButton.background =
          ResourcesCompat.getDrawable(
            resources,
            R.drawable.un_selected_language_button_style,
            theme
          )
        binding.kannadaButton.setTextColor(
          ResourcesCompat.getColor(
            resources,
            R.color.black,
            theme
          )
        )

        binding.teluguButton.background =
          ResourcesCompat.getDrawable(
            resources,
            R.drawable.un_selected_language_button_style,
            theme
          )
        binding.teluguButton.setTextColor(ResourcesCompat.getColor(resources, R.color.black, theme))

        binding.malayalamButton.background =
          ResourcesCompat.getDrawable(
            resources,
            R.drawable.un_selected_language_button_style,
            theme
          )
        binding.malayalamButton.setTextColor(
          ResourcesCompat.getColor(
            resources,
            R.color.black,
            theme
          )
        )

        binding.tamilButton.background =
          ResourcesCompat.getDrawable(resources, R.drawable.selected_language_button_style, theme)
        binding.tamilButton.setTextColor(
          ResourcesCompat.getColor(
            resources,
            R.color.red_color,
            theme
          )
        )

        binding.maratiButton.background =
          ResourcesCompat.getDrawable(
            resources,
            R.drawable.un_selected_language_button_style,
            theme
          )
        binding.maratiButton.setTextColor(ResourcesCompat.getColor(resources, R.color.black, theme))

      }
      R.id.marati_button -> {
        binding.englishButton.background =
          ResourcesCompat.getDrawable(
            resources,
            R.drawable.un_selected_language_button_style,
            theme
          )
        binding.englishButton.setTextColor(
          ResourcesCompat.getColor(
            resources,
            R.color.black,
            theme
          )
        )

        binding.hindiButton.background =
          ResourcesCompat.getDrawable(
            resources,
            R.drawable.un_selected_language_button_style,
            theme
          )
        binding.hindiButton.setTextColor(ResourcesCompat.getColor(resources, R.color.black, theme))

        binding.kannadaButton.background =
          ResourcesCompat.getDrawable(
            resources,
            R.drawable.un_selected_language_button_style,
            theme
          )
        binding.kannadaButton.setTextColor(
          ResourcesCompat.getColor(
            resources,
            R.color.black,
            theme
          )
        )

        binding.teluguButton.background =
          ResourcesCompat.getDrawable(
            resources,
            R.drawable.un_selected_language_button_style,
            theme
          )
        binding.teluguButton.setTextColor(ResourcesCompat.getColor(resources, R.color.black, theme))

        binding.malayalamButton.background =
          ResourcesCompat.getDrawable(
            resources,
            R.drawable.un_selected_language_button_style,
            theme
          )
        binding.malayalamButton.setTextColor(
          ResourcesCompat.getColor(
            resources,
            R.color.black,
            theme
          )
        )

        binding.tamilButton.background =
          ResourcesCompat.getDrawable(
            resources,
            R.drawable.un_selected_language_button_style,
            theme
          )
        binding.tamilButton.setTextColor(ResourcesCompat.getColor(resources, R.color.black, theme))

        binding.maratiButton.background =
          ResourcesCompat.getDrawable(resources, R.drawable.selected_language_button_style, theme)
        binding.maratiButton.setTextColor(
          ResourcesCompat.getColor(
            resources,
            R.color.red_color,
            theme
          )
        )

      }

    }

  }


  private fun setUpMvvm() {
    viewModel.navigation.observe(this, Observer {
      if (::mPopupWindow.isInitialized) {
        mPopupWindow.dismiss()
        dropDownStatus = false
      }
      it?.let {
        Log.e(it, ">>>>@@@@@@@@@@@@@@@@@@@")
        if (it.equals(ENABLE_BOTTOM_VIEW)) {
          binding.loginButton.visibility = View.VISIBLE
          binding.languageView.visibility = View.VISIBLE
          binding.languageDropdown.visibility = View.GONE
          binding.languageLayout.visibility = View.VISIBLE
        } else if (it.equals(SINGLE_LANGUAGE_BUTTON_VIEW)) {
          binding.languageDropdown.visibility = View.GONE
          binding.languageLayout.visibility = View.GONE
          binding.languageView.visibility = View.VISIBLE
          binding.loginButton.visibility = View.VISIBLE
        }
      }

    })
    viewModel.languageSelected.observe(this, Observer {
      binding.languageDropdownText.text = it
      when (it) {
        langList.get(0) -> {
          changeLanguage(applicationContext, "en")
          styleLanguageButton(R.id.english_button)
        }
        langList.get(1) -> {
          changeLanguage(applicationContext, "hi")
          styleLanguageButton(R.id.hindi_button)
        }
        langList.get(2) -> {
          changeLanguage(applicationContext, "te")
          styleLanguageButton(R.id.telugu_button)
        }
        langList.get(3) -> {
          changeLanguage(applicationContext, "ta")
          styleLanguageButton(R.id.tamil_button)
        }
        langList.get(4) -> {
          styleLanguageButton(R.id.kannada_button)
        }
        langList.get(5) -> {
          styleLanguageButton(R.id.malayalam_button)
        }
        langList.get(6) -> {
          styleLanguageButton(R.id.marati_button)
        }
      }
    })

    viewModel.inActivityUpdateButtonStyle.observe(this, Observer {
      //        viewModel.changeLocaleLag.observe(this, Observer {
      Log.e("Activity: " + it, ">>>>")
      binding.viewModel?.LanguageSelection(getString(it))
      when (it) {
        R.string.english -> {
          styleLanguageButton(R.id.english_button)
        }
        R.string.hindi -> {
          styleLanguageButton(R.id.hindi_button)
        }
        R.string.tamil -> {
          styleLanguageButton(R.id.tamil_button)
        }
        R.string.kannada -> {
          styleLanguageButton(R.id.kannada_button)
        }
        R.string.telugu -> {
          styleLanguageButton(R.id.telugu_button)
        }
        R.string.malayalam -> {
          styleLanguageButton(R.id.malayalam_button)
        }
        R.string.marathi -> {
          styleLanguageButton(R.id.marati_button)
        }
      }
    })
  }


  override fun onBackPressed() {
    Log.e("onBackPressed", ">>>>>>>>>77")
    if (::mPopupWindow.isInitialized) {
      mPopupWindow.dismiss()
      dropDownStatus = false
    }
    if (navHostFragment.childFragmentManager.fragments[0] is MainFragment) {
      finish()
    } else {
      super.onBackPressed()
    }
  }

  override fun onResume() {
    super.onResume()
    //language change reflection
    if (::localeManager.isInitialized && !localeManager.getLanguage()!!.isEmpty()) {
      val langType = localeManager.getLanguage()
      when (langType) {
        "en" -> {
          styleLanguageButton(R.id.english_button)
          binding.languageDropdownText.text = langList.get(0)
        }
        "hi" -> {
          styleLanguageButton(R.id.hindi_button)
          binding.languageDropdownText.text = langList.get(1)
        }
        "te" -> {
          styleLanguageButton(R.id.telugu_button)
          binding.languageDropdownText.text = langList.get(2)
        }
        "ta" -> {
          styleLanguageButton(R.id.tamil_button)
          binding.languageDropdownText.text = langList.get(3)
        }
      }
    }
  }

  override fun attachBaseContext(newBase: Context?) {
    localeManager = LocaleManager(newBase)
    super.attachBaseContext(localeManager.setLocale(newBase!!))
  }

  fun changeLanguage(context: Context, lang: String) {
    localeManager.setNewLocale(context, lang)
//        recreate()
    //custome selection for navigation fragment
    val intent = Intent(applicationContext, PreSignUpActivity::class.java)
    var fragmentState = ""
    if (binding.languageDropdown.visibility == View.VISIBLE) {
      fragmentState = "dropdown"
    } else {
      fragmentState = "langbuttons"
    }
    intent.putExtra("fragmentState", fragmentState)
    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
    //terminate the activity
    finish()
    //recreate the activity
    startActivity(intent)
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
//    popUpDialogFragment.onActivityResult(requestCode, resultCode, data)
  }


}
