package com.framework.imagepicker

/**
 * Created by Sarvare Alam on 5/21/2018.
 */
interface ImagePickerBuilderBase {
    fun compressLevel(compressLevel: ImagePicker.ComperesLevel?): ImagePicker.Builder?
    fun mode(mode: ImagePicker.Mode?): ImagePicker.Builder?
    fun directory(directory: String?): ImagePicker.Builder?
    fun directory(directory: ImagePicker.Directory?): ImagePicker.Builder?
    fun extension(extension: ImagePicker.Extension?): ImagePicker.Builder?
    fun scale(minWidth: Int, minHeight: Int): ImagePicker.Builder?
    fun allowMultipleImages(allowMultiple: Boolean): ImagePicker.Builder?
    fun enableDebuggingMode(debug: Boolean): ImagePicker.Builder?
    fun build(): ImagePicker?
}