package com.framework.utils

import java.math.RoundingMode
import java.text.DecimalFormat

object MathUtils {

    fun calculateGST(amount: Double,includedGst:Int): Double {
        val df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.CEILING
        return df.format((amount - (df.format(amount / (1+includedGst.toFloat().div(100.00))).toDouble()))).toDouble()
    }
}