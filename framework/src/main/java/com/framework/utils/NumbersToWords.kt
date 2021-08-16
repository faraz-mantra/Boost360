package com.framework.utils

object NumbersToWords {

  private const val ZERO = "zero"

  private val oneToNine = arrayOf(
    "one", "two", "three", "four", "five", "six", "seven", "eight", "nine"
  )
  private val tenToNinteen = arrayOf(
    "ten",
    "eleven",
    "twelve",
    "thirteen",
    "fourteen",
    "fifteen",
    "sixteen",
    "seventeen",
    "eighteen",
    "nineteen"
  )
  private val dozens = arrayOf(
    "ten", "twenty", "thirty", "forty", "fifty", "sixty", "seventy", "eighty", "ninety"
  )

  fun solution(number: Int): String {
    return if (number == 0) ZERO else generate(number).trim { it <= ' ' }
  }

  fun generate(number: Int): String {
    return when {
      number >= 1000000000 -> {
        generate(number / 1000000000) + " billion " + generate(number % 1000000000)
      }
      number >= 1000000 -> {
        generate(number / 1000000) + " million " + generate(number % 1000000)
      }
      number >= 1000 -> {
        generate(number / 1000) + " thousand " + generate(number % 1000)
      }
      number >= 100 -> {
        generate(number / 100) + " hundred " + generate(number % 100)
      }
      else -> generate1To99(number)
    }
  }

  private fun generate1To99(number: Int): String {
    if (number == 0) return ""
    return when {
      number <= 9 -> oneToNine[number - 1]
      number <= 19 -> tenToNinteen[number % 10]
      else -> dozens[number / 10 - 1] + " " + generate1To99(number % 10)
    }
  }
}