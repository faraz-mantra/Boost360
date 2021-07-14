package com.dashboard.model.live.drScore.siteMeter

import java.io.Serializable

data class SiteMeterScoreDetails(
  var businessProfile: ArrayList<SiteMeterModel> = ArrayList(),
  var contentManagement: ArrayList<SiteMeterModel> = ArrayList(),
  var channelSync: ArrayList<SiteMeterModel> = ArrayList(),
  var siteMeterTotalWeight: Int = 0,
) : Serializable

fun ArrayList<SiteMeterModel>.getCompletePercentage(): Int {
  var count = 0
  forEach { if (it.status == true || it.isPost) count += it.getValue() }
  return count
}

fun ArrayList<SiteMeterModel>.getRemainingPercentage(): Int {
  var count = 0
  forEach { if (it.status == false) count += it.getValue() }
  return count
}

fun SiteMeterModel.getValue(): Int {
  return Percentage?.replace("+", "")?.replace("%", "")?.trim()?.toIntOrNull() ?: 0
}

