
import com.google.gson.annotations.SerializedName

data class GetStaffListingRequest(
	@field:SerializedName("FilterBy")
	val filterBy: StaffFilterBy? = null,
	@field:SerializedName("FloatingPointTag")
	val floatingPointTag: String? = null,
	@field:SerializedName("ServiceId")
	val serviceId: String? = null
)

data class StaffFilterBy(
	@field:SerializedName("ServiceType")
	val serviceType: String? = null,
	@field:SerializedName("Limit")
	val limit: Int? = null,
	@field:SerializedName("Offset")
	var offset: Int? = null,
)