package dev.patrickgold.florisboard.customization.util

import com.framework.pref.UserSessionManager
import com.framework.pref.getDomainName
import com.framework.utils.ContentSharing
import com.framework.utils.isService
import dev.patrickgold.florisboard.customization.model.response.staff.DataItem

fun shareProduct(name: String? = null, price: String? = null, link: String? = null, vmn: String? = null): String {
  val templateBuilder = StringBuilder()
  if (name.isNullOrBlank().not()) {
    templateBuilder.append("🆕 *Item name:* $name").append("\n")
  }
  if (price.isNullOrBlank().not()) {
    templateBuilder.append("🏷️ *Price:* Rs.$price").append("\n")
  }
  if (vmn.isNullOrBlank().not()) {
    templateBuilder.append("📞 Feel free to call $vmn if you need any help. ").append("\n")
  }
  if (link.isNullOrBlank().not()) {
    templateBuilder.append("👉🏼 *Place your order here:* $link")
  }
  return templateBuilder.toString();
}

fun UserSessionManager.shareUpdates(updateContent: String, link: String?, vmn: String?, imageUri: String? = null): String {
  val subDomain = if (isService(this.fP_AppExperienceCode)) "all-services" else "all-products"
  val catalogLink = this.getDomainName() + "/" + subDomain
  val templateBuilder = StringBuilder()
  if (updateContent.isBlank().not() && link.isNullOrBlank().not()) {
    templateBuilder.append("👋🏼 Hey there!")
    templateBuilder.append("${ContentSharing.truncateString(updateContent, 100)}: Read more $link").append("\n")
  }
  if (catalogLink.isBlank().not()) {
    templateBuilder.append("🏷️ Check our online catalogue, $catalogLink").append("\n")
  }
  if (vmn.isNullOrBlank().not()) {
    templateBuilder.append("📞 Feel free to call $vmn if you need any help. ")
  }
  return templateBuilder.toString()
}

fun getStaffShare(staff: DataItem?): String {
  return String.format(
    "\uD83D\uDC81 *%s*%s\n\n%s",
    staff?.name,
    "\n${if (staff?.description.isNullOrEmpty()) "" else "_" + staff?.description + "_"}",
    if (staff?.specialData().isNullOrEmpty()) "" else "*Specialization*:\n_${staff?.specialData()}_"
  )
}