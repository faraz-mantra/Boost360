package com.framework.imagepicker

/**
 * Created by Sarvare Alam on 5/21/2018.
 */
class ImageTags {
  object Tags {
    const val TAG = "ImagePicker"
    const val LEVEL = "level"
    const val EXTENSION = "extension"
    const val MODE = "mode"
    const val DIRECTORY = "DIRECTORY"
    const val CAMERA_IMAGE_URI = "cameraImageUri"
    const val COMPRESS_LEVEL = "COMPRESS_LEVEL"
    const val REQUESTED_WIDTH = "REQUESTED_WIDTH"
    const val REQUESTED_HEIGHT = "REQUESTED_HEIGHT"
    const val IMAGE_PATH = "IMAGE_PATH"
    const val ALLOW_MULTIPLE = "ALLOW_MULTIPLE"
    const val DEBUG = "DEBUG"
    const val IMAGE_PICKER_DIR = "/mediapicker/images/"
    const val IMG_CONFIG = "IMG_CONFIG"
    const val PICK_ERROR = "PICK_ERROR"
  }

  object Action {
    const val SERVICE_ACTION = "mediapicker.image.service"
  }

  object IntentCode {
    const val REQUEST_CODE_SELECT_MULTI_PHOTO = 5341
    const val CAMERA_REQUEST = 1888
    const val REQUEST_CODE_ASK_PERMISSIONS = 123
    const val REQUEST_CODE_SELECT_PHOTO = 43
  }
}
