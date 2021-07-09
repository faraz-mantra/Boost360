package com.framework.imagepicker

import android.annotation.TargetApi
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Environment
import com.framework.BaseApplication
import java.lang.ref.WeakReference

class ImagePicker(builder: Builder) {

    private val imageConfig: ImageConfig

    enum class Extension(val value: String) {
        PNG(".png"), JPG(".jpg");
    }

    enum class ComperesLevel(val value: Int) {
        HARD(20), MEDIUM(50), SOFT(70), NONE(100);

        companion object {
            fun getEnum(value: Int): ComperesLevel {
                for (v in values()) if (v.value == value) return v
                throw IllegalArgumentException()
            }
        }
    }

    enum class Mode(val value: Int) {
        CAMERA(0), GALLERY(1), CAMERA_AND_GALLERY(2);

    }

    enum class Directory(val value: Int) {
        DEFAULT(0);

    }

    class Builder(context: Activity) : ImagePickerBuilderBase {
        // Required params
        val context: WeakReference<Activity> = WeakReference(context)
        val imageConfig: ImageConfig = ImageConfig()
        override fun compressLevel(compressLevel: ComperesLevel?): Builder {
            if (compressLevel != null) imageConfig.compressLevel = compressLevel
            return this
        }

        override fun mode(mode: Mode?): Builder {
            if (mode != null) imageConfig.mode = mode
            return this
        }

        override fun directory(directory: String?): Builder {
            if (directory != null) imageConfig.directory = directory
            return this
        }

        override fun directory(directory: Directory?): Builder {
            when (directory) {
                Directory.DEFAULT -> imageConfig.directory = BaseApplication.instance.applicationContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.absolutePath + ImageTags.Tags.IMAGE_PICKER_DIR
                else -> {
                }
            }
            return this
        }

        override fun extension(extension: Extension?): Builder {
            if (extension != null) imageConfig.extension = extension
            return this
        }

        override fun scale(minWidth: Int, minHeight: Int): Builder {
            imageConfig.reqHeight = minHeight
            imageConfig.reqWidth = minWidth
            return this
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
        override fun allowMultipleImages(allowMultiple: Boolean): Builder {
            imageConfig.allowMultiple = allowMultiple
            return this
        }

        override fun enableDebuggingMode(debug: Boolean): Builder {
            imageConfig.debug = debug
            return this
        }

        override fun build(): ImagePicker {
            return ImagePicker(this)
        }

        fun requireContext(): Activity? {
            return context.get()
        }

    }

    companion object {
        const val IMAGE_PICKER_REQUEST_CODE = 42141
        const val EXTRA_IMAGE_PATH = "EXTRA_IMAGE_PATH"
    }

    init {
        // Required
        val context = builder.context
        // Optional
        imageConfig = builder.imageConfig
        val callingIntent: Intent = ImageActivity.getCallingIntent(context.get(), imageConfig)
        context.get()?.startActivityForResult(callingIntent, IMAGE_PICKER_REQUEST_CODE)
    }
}
