package com.framework.utils

import android.annotation.SuppressLint
import android.content.res.Resources
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.View
import com.framework.BaseApplication
import com.framework.helper.MemoryConstants
import com.framework.helper.TimeConstants
import java.io.*

object ConversionUtils {

  private val hexDigits = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F')

  /**
   * Chars to bytes.
   *
   * @param chars The chars.
   * @return bytes
   */
  fun chars2Bytes(chars: CharArray?): ByteArray? {
    if (chars == null || chars.size <= 0) return null
    val len = chars.size
    val bytes = ByteArray(len)
    for (i in 0 until len) {
      bytes[i] = chars[i].toByte()
    }
    return bytes
  }

  /**
   * Hex string to bytes.
   *
   *
   * e.g. hexString2Bytes("00A8") returns { 0, (byte) 0xA8 }
   *
   * @param hexString The hex string.
   * @return the bytes
   */
  fun hexString2Bytes(hexString: String): ByteArray? {
    var hexString = hexString
    if (isSpace(hexString)) return null
    var len = hexString.length
    if (len % 2 != 0) {
      hexString = "0$hexString"
      len = len + 1
    }
    val hexBytes = hexString.toUpperCase().toCharArray()
    val ret = ByteArray(len shr 1)
    var i = 0
    while (i < len) {
      ret[i shr 1] = (hex2Int(hexBytes[i]) shl 4 or hex2Int(
              hexBytes[i + 1]
      )).toByte()
      i += 2
    }
    return ret
  }

  private fun hex2Int(hexChar: Char): Int {
    return if (hexChar >= '0' && hexChar <= '9') {
      hexChar - '0'
    } else if (hexChar >= 'A' && hexChar <= 'F') {
      hexChar - 'A' + 10
    } else {
      throw IllegalArgumentException()
    }
  }

  /**
   * Size of memory in unit to size of byte.
   *
   * @param memorySize Size of memory.
   * @param unit The unit of memory size.
   *
   *  * [MemoryConstants.BYTE]
   *  * [MemoryConstants.KB]
   *  * [MemoryConstants.MB]
   *  * [MemoryConstants.GB]
   *
   *
   * @return size of byte
   */
  fun memorySize2Byte(memorySize: Long, @MemoryConstants.Unit unit: Int): Long {
    return if (memorySize < 0) -1 else memorySize * unit
  }

  /**
   * Size of byte to size of memory in unit.
   *
   * @param byteSize Size of byte.
   * @param unit The unit of memory size.
   *
   *  * [MemoryConstants.BYTE]
   *  * [MemoryConstants.KB]
   *  * [MemoryConstants.MB]
   *  * [MemoryConstants.GB]
   *
   *
   * @return size of memory in unit
   */
  fun byte2MemorySize(byteSize: Long, @MemoryConstants.Unit unit: Int): Double {
    return if (byteSize < 0) -1.0 else byteSize.toDouble() / unit
  }

  /**
   * Size of byte to fit size of memory.
   *
   *
   * to three decimal places
   *
   * @param byteSize Size of byte.
   * @return fit size of memory
   */
  @SuppressLint("DefaultLocale")
  fun byte2FitMemorySize(byteSize: Long): String {
    return if (byteSize < 0) {
      "shouldn't be less than zero!"
    } else if (byteSize < MemoryConstants.KB) {
      String.format("%.3fB", byteSize.toDouble())
    } else if (byteSize < MemoryConstants.MB) {
      String.format("%.3fKB", byteSize.toDouble() / MemoryConstants.KB)
    } else if (byteSize < MemoryConstants.GB) {
      String.format("%.3fMB", byteSize.toDouble() / MemoryConstants.MB)
    } else {
      String.format("%.3fGB", byteSize.toDouble() / MemoryConstants.GB)
    }
  }

  /**
   * Time span in unit to milliseconds.
   *
   * @param timeSpan The time span.
   * @param unit The unit of time span.
   *
   *  * [TimeConstants.MSEC]
   *  * [TimeConstants.SEC]
   *  * [TimeConstants.MIN]
   *  * [TimeConstants.HOUR]
   *  * [TimeConstants.DAY]
   *
   *
   * @return milliseconds
   */
  fun timeSpan2Millis(timeSpan: Long, @TimeConstants.Unit unit: Int): Long {
    return timeSpan * unit
  }

  /**
   * Milliseconds to time span in unit.
   *
   * @param millis The milliseconds.
   * @param unit The unit of time span.
   *
   *  * [TimeConstants.MSEC]
   *  * [TimeConstants.SEC]
   *  * [TimeConstants.MIN]
   *  * [TimeConstants.HOUR]
   *  * [TimeConstants.DAY]
   *
   *
   * @return time span in unit
   */
  fun millis2TimeSpan(millis: Long, @TimeConstants.Unit unit: Int): Long {
    return millis / unit
  }

  /**
   * Milliseconds to fit time span.
   *
   * @param millis The milliseconds.
   *
   * millis &lt;= 0, return null
   * @param precision The precision of time span.
   *
   *  * precision = 0, return null
   *  * precision = 1, return 天
   *  * precision = 2, return 天, 小时
   *  * precision = 3, return 天, 小时, 分钟
   *  * precision = 4, return 天, 小时, 分钟, 秒
   *  * precision &gt;= 5，return 天, 小时, 分钟, 秒, 毫秒
   *
   *
   * @return fit time span
   */
  @SuppressLint("DefaultLocale")
  fun millis2FitTimeSpan(millis: Long, precision: Int): String? {
    var millis = millis
    var precision = precision
    if (millis <= 0 || precision <= 0) return null
    val sb = StringBuilder()
    val units = arrayOf("天", "小时", "分钟", "秒", "毫秒")
    val unitLen = intArrayOf(86400000, 3600000, 60000, 1000, 1)
    precision = Math.min(precision, 5)
    for (i in 0 until precision) {
      if (millis >= unitLen[i]) {
        val mode = millis / unitLen[i]
        millis -= mode * unitLen[i]
        sb.append(mode).append(units[i])
      }
    }
    return sb.toString()
  }

  /**
   * Bytes to input stream.
   *
   * @param bytes The bytes.
   * @return input stream
   */
  fun bytes2InputStream(bytes: ByteArray?): InputStream? {
    return if (bytes == null || bytes.size <= 0) null else ByteArrayInputStream(bytes)
  }

  /**
   * Output stream to bytes.
   *
   * @param out The output stream.
   * @return bytes
   */
  fun outputStream2Bytes(out: OutputStream?): ByteArray? {
    return if (out == null) null else (out as ByteArrayOutputStream).toByteArray()
  }

  /**
   * Bytes to output stream.
   *
   * @param bytes The bytes.
   * @return output stream
   */
  fun bytes2OutputStream(bytes: ByteArray?): OutputStream? {
    if (bytes == null || bytes.size <= 0) return null
    var os: ByteArrayOutputStream? = null
    try {
      os = ByteArrayOutputStream()
      os.write(bytes)
      return os
    } catch (e: IOException) {
      e.printStackTrace()
      return null
    } finally {
      try {
        os?.close()
      } catch (e: IOException) {
        e.printStackTrace()
      }

    }
  }

  /**
   * String to input stream.
   *
   * @param string The string.
   * @param charsetName The name of charset.
   * @return input stream
   */
  fun string2InputStream(string: String?, charsetName: String): InputStream? {
    if (string == null || isSpace(charsetName)) return null
    try {
      return ByteArrayInputStream(string.toByteArray(charset(charsetName)))
    } catch (e: UnsupportedEncodingException) {
      e.printStackTrace()
      return null
    }

  }

  /**
   * String to output stream.
   *
   * @param string The string.
   * @param charsetName The name of charset.
   * @return output stream
   */
  fun string2OutputStream(string: String?, charsetName: String): OutputStream? {
    if (string == null || isSpace(charsetName)) return null
    try {
      return bytes2OutputStream(
              string.toByteArray(
                      charset(charsetName)
              )
      )
    } catch (e: UnsupportedEncodingException) {
      e.printStackTrace()
      return null
    }

  }

  /**
   * Bitmap to bytes.
   *
   * @param bitmap The bitmap.
   * @param format The format of bitmap.
   * @return bytes
   */
  fun bitmap2Bytes(bitmap: Bitmap?, format: Bitmap.CompressFormat): ByteArray? {
    if (bitmap == null) return null
    val baos = ByteArrayOutputStream()
    bitmap.compress(format, 100, baos)
    return baos.toByteArray()
  }

  /**
   * Bytes to bitmap.
   *
   * @param bytes The bytes.
   * @return bitmap
   */
  fun bytes2Bitmap(bytes: ByteArray?): Bitmap? {
    return if (bytes == null || bytes.size == 0)
      null
    else
      BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
  }

  /**
   * Drawable to bitmap.
   *
   * @param drawable The drawable.
   * @return bitmap
   */
  fun drawable2Bitmap(drawable: Drawable): Bitmap {
    if (drawable is BitmapDrawable) {
      if (drawable.bitmap != null) {
        return drawable.bitmap
      }
    }
    val bitmap: Bitmap
    if (drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0) {
      bitmap = Bitmap.createBitmap(
              1,
              1,
              if (drawable.opacity != PixelFormat.OPAQUE)
                Bitmap.Config.ARGB_8888
              else
                Bitmap.Config.RGB_565)
    } else {
      bitmap = Bitmap.createBitmap(
              drawable.intrinsicWidth,
              drawable.intrinsicHeight,
              if (drawable.opacity != PixelFormat.OPAQUE)
                Bitmap.Config.ARGB_8888
              else
                Bitmap.Config.RGB_565)
    }
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)
    return bitmap
  }

  /**
   * Bitmap to drawable.
   *
   * @param bitmap The bitmap.
   * @return drawable
   */
  fun bitmap2Drawable(bitmap: Bitmap?): Drawable? {
    return if (bitmap == null)
      null
    else
      BitmapDrawable(BaseApplication.instance.resources, bitmap)
  }

  /**
   * Drawable to bytes.
   *
   * @param drawable The drawable.
   * @param format The format of bitmap.
   * @return bytes
   */
  fun drawable2Bytes(drawable: Drawable?, format: Bitmap.CompressFormat): ByteArray? {
    return if (drawable == null) null else bitmap2Bytes(
            drawable2Bitmap(drawable),
            format
    )
  }

  /**
   * Bytes to drawable.
   *
   * @param bytes The bytes.
   * @return drawable
   */
  fun bytes2Drawable(bytes: ByteArray?): Drawable? {
    return if (bytes == null) null else bitmap2Drawable(
            bytes2Bitmap(bytes)
    )
  }

  /**
   * View to bitmap.
   *
   * @param view The view.
   * @return bitmap
   */
  fun view2Bitmap(view: View?): Bitmap? {
    if (view == null) return null
    val ret = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(ret)
    val bgDrawable = view.background
    if (bgDrawable != null) {
      bgDrawable.draw(canvas)
    } else {
      canvas.drawColor(Color.WHITE)
    }
    view.draw(canvas)
    return ret
  }

  /**
   * Value of dp to value of px.
   *
   * @param dpValue The value of dp.
   * @return value of px
   */
  fun dp2px(dpValue: Float): Int {
    val scale = Resources.getSystem().displayMetrics.density
    return (dpValue * scale + 0.5f).toInt()
  }

  fun dp2px(vararg dpValues: Float): Int {
    val scale = Resources.getSystem().displayMetrics.density
    var sum = 0
    for (dpValue in dpValues) {
      sum += (dpValue * scale + 0.5f).toInt()
    }
    return sum
  }

  /**
   * Value of px to value of dp.
   *
   * @param pxValue The value of px.
   * @return value of dp
   */
  fun px2dp(pxValue: Float): Int {
    val scale = Resources.getSystem().displayMetrics.density
    return (pxValue / scale + 0.5f).toInt()
  }

  /**
   * Value of sp to value of px.
   *
   * @param spValue The value of sp.
   * @return value of px
   */
  fun sp2px(spValue: Float): Int {
    val fontScale = Resources.getSystem().displayMetrics.scaledDensity
    return (spValue * fontScale + 0.5f).toInt()
  }

  /**
   * Value of px to value of sp.
   *
   * @param pxValue The value of px.
   * @return value of sp
   */
  fun px2sp(pxValue: Float): Int {
    val fontScale = Resources.getSystem().displayMetrics.scaledDensity
    return (pxValue / fontScale + 0.5f).toInt()
  }

  private fun isSpace(s: String?): Boolean {
    if (s == null) return true
    var i = 0
    val len = s.length
    while (i < len) {
      if (!Character.isWhitespace(s[i])) {
        return false
      }
      ++i
    }
    return true
  }
}
