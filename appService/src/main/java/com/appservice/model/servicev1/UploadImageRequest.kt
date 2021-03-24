package com.appservice.model.servicev1

import android.util.Base64
import java.io.File
import java.io.Serializable

data class UploadImageRequest(
        var ImageType: Int? = null,
        var ServiceId: String? = null,
        var Image: ImageBase64Data? = null,
) : Serializable {

    companion object {
        fun getInstance(imageType: Int, serviceId: String, file: File): UploadImageRequest {
            val obj = UploadImageRequest();
            obj.ImageType = imageType;
            obj.ServiceId = serviceId;
            obj.Image = ImageBase64Data.getInstanceFromFile(file);
            return obj;
        }
    }
}

data class ImageBase64Data(
        var ImageFileType: String? = null,
        var Image: String? = null,
        var FileName: String? = null
) : Serializable {
    companion object {
        fun getInstanceFromFile(file: File): ImageBase64Data {
            val obj = ImageBase64Data();
            obj.FileName = file.name;
            obj.ImageFileType = file.extension;
            obj.Image = "data:image/"+obj.ImageFileType+";base64,"+getBase64Image(file);
            return obj;
        }

        fun getBase64Image(file: File): String? {
            return Base64.encodeToString(file.readBytes(), Base64.DEFAULT)
        }
    }
}