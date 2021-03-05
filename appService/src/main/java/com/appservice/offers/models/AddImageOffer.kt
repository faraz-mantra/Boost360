package com.appservice.offers.models

import android.util.Base64
import com.framework.base.BaseResponse
import com.google.gson.annotations.SerializedName
import java.io.File
import java.io.Serializable

data class AddImageOffer(

		@field:SerializedName("OfferId")
		var offerId: String? = null,

		@field:SerializedName("ImageFileType")
		var imageFileType: String? = null,

		@field:SerializedName("FileName")
		var fileName: String? = null,

		@field:SerializedName("ClientId")
		var clientId: String? = null,

		@field:SerializedName("Image")
		var image: String? = null,

		@field:SerializedName("OfferImageType")
		var offerImageType: String? = null,
) : BaseResponse() {
	companion object {
		fun getInstance(clientId: String?,imageType: Int, offerId: String, file: File): AddImageOffer {
			val obj = AddImageOffer();
			obj.offerId = offerId
			obj.clientId = clientId
			obj.offerImageType = imageType.toString()
			obj.image = ImageBase64Data.getInstanceFromFile(file).Image
			obj.fileName = ImageBase64Data.getInstanceFromFile(file).FileName
			obj.imageFileType = ImageBase64Data.getInstanceFromFile(file).ImageFileType
			return obj;
		}
	}
}

data class ImageBase64Data(
		var ImageFileType: String? = null,
		var Image: String? = null,
		var FileName: String? = null,
) : Serializable {
	companion object {
		fun getInstanceFromFile(file: File): ImageBase64Data {
			val obj = ImageBase64Data();
			obj.FileName = file.name;
			obj.ImageFileType = file.extension;
			obj.Image = "data:image/" + obj.ImageFileType + ";base64," + getBase64Image(file);
			return obj;
		}

		private fun getBase64Image(file: File): String? {
			return Base64.encodeToString(file.readBytes(), Base64.DEFAULT)
		}
	}
}
