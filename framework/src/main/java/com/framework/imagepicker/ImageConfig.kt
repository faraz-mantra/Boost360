package com.framework.imagepicker

import android.os.Environment
import com.framework.BaseApplication
import java.io.Serializable

/**
 * Created by Sarvare Alam on 3/21/2020.
 */
class ImageConfig : Serializable {
    var extension: ImagePicker.Extension = ImagePicker.Extension.PNG
    var compressLevel: ImagePicker.ComperesLevel = ImagePicker.ComperesLevel.MEDIUM
    var mode: ImagePicker.Mode = ImagePicker.Mode.CAMERA

    //    var directory: String = Environment.getExternalStorageDirectory().absolutePath + ImageTags.Tags.IMAGE_PICKER_DIR
    var directory: String = BaseApplication.instance.applicationContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.absolutePath + ImageTags.Tags.IMAGE_PICKER_DIR
    var reqHeight: Int = 0
    var reqWidth: Int = 0
    var allowMultiple: Boolean = false
    var isImgFromCamera = false
    var debug = false
    override fun toString(): String {
        return "ImageConfig{" +
                "extension=" + extension +
                ", compressLevel=" + compressLevel +
                ", mode=" + mode +
                ", directory='" + directory + '\'' +
                ", reqHeight=" + reqHeight +
                ", reqWidth=" + reqWidth +
                ", allowMultiple=" + allowMultiple +
                ", isImgFromCamera=" + isImgFromCamera +
                ", debug=" + debug +
                '}'
    }

}

