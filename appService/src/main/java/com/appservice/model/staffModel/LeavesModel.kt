package com.appservice.model.staffModel

data class LeavesModel(
  var startingFromDate: String,
  var startTime: String,
  var tillDate: String,
  var tillTime: String,
  var additionalBreakInfo: String,
)