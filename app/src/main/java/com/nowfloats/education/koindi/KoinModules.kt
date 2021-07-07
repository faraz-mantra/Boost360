package com.nowfloats.education.koindi

import com.nowfloats.education.batches.ui.batchesdetails.BatchesDetailsViewModel
import com.nowfloats.education.batches.ui.batchesfragment.BatchesViewModel
import com.nowfloats.education.faculty.ui.facultydetails.FacultyDetailsViewModel
import com.nowfloats.education.faculty.ui.facultymanagement.FacultyManagementViewModel
import com.nowfloats.education.helper.ExifInterfaceHelper
import com.nowfloats.education.helper.FileProvider
import com.nowfloats.education.helper.PictureUtils
import com.nowfloats.education.helper.SaveImageHelper
import com.nowfloats.education.toppers.ui.topperdetails.TopperDetailsViewModel
import com.nowfloats.education.toppers.ui.topperhome.ToppersViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val koinModules = module(override = true) {

  single {
    FileProvider(context = get())
  }

  single {
    SaveImageHelper(context = get())
  }

  single {
    PictureUtils()
  }

  single {
    ExifInterfaceHelper(pictureUtils = get())
  }

  viewModel {
    BatchesDetailsViewModel(service = get())
  }

  viewModel {
    BatchesViewModel(service = get())
  }

  viewModel {
    FacultyDetailsViewModel(
      service = get(),
      fileProvider = get(),
      saveImageHelper = get(),
      exifInterfaceHelper = get()
    )
  }

  viewModel {
    FacultyManagementViewModel(service = get())
  }

  viewModel {
    ToppersViewModel(service = get())
  }

  viewModel {
    TopperDetailsViewModel(
      service = get(),
      fileProvider = get(),
      saveImageHelper = get(),
      exifInterfaceHelper = get()
    )
  }
}