package uz.orign.edittextvalidatorlib

import java.text.NumberFormat
import java.util.*


object ValidationUtils {
    fun cashFormat(locale: Locale?, number: Double): String {
        val format: NumberFormat = NumberFormat.getCurrencyInstance(locale!!)
        return try {
            format.format(number)
        } catch (e: Exception) {
            number.toString()
        }
    }

    fun cashFormat(locale: Locale?, number: String): String {
        val format: NumberFormat = NumberFormat.getCurrencyInstance(locale!!)
        return try {
            val cast = java.lang.Double.valueOf(number)
            format.format(cast)
        } catch (e: Exception) {
            number
        }
    }

    fun cashFormat(locale: Locale?, number: Int): String {
        val format: NumberFormat = NumberFormat.getCurrencyInstance(locale!!)
        return try {
            format.format(number)
        } catch (e: Exception) {
            number.toString()
        }
    }

    fun cashFormat(locale: Locale?, number: Float): String {
        val format: NumberFormat = NumberFormat.getCurrencyInstance(locale!!)
        return try {
            format.format(number)
        } catch (e: Exception) {
            number.toString()
        }
    }

    fun parseCurrencyAmount(aMount: String): Double {
        return try {
            val cleanString = aMount.replace("[^\\d.]".toRegex(), "").replace("\\.".toRegex(), "")
            cleanString.toDouble()
        } catch (e: Exception) {
            0.0
        }
    }

    fun parseCurrencyAmountWithoutDecimal(aMount: String): Double {
        return try {
            val cleanString = aMount.replace("[^\\d.]".toRegex(), "")
            val splitedAmount = cleanString.split("\\.".toRegex()).toTypedArray()
            if (splitedAmount.size == 2) {
                var mount = splitedAmount[0]
                val decimals = splitedAmount[1]
                if (decimals.length == 1) {
                    mount = splitedAmount[0].replaceFirst(".$".toRegex(), "")
                }
                val cleanDecimals = decimals.replaceFirst("0".toRegex(), "").replaceFirst("0".toRegex(), "")
                return (mount + cleanDecimals).toDouble()
            }
            cleanString.toDouble()
        } catch (e: Exception) {
            return 0.0
        }
    }

    fun parseCurrencyAmountString(aMount: String): String {
        return try {
            val doubleMount = parseCurrencyAmount(aMount) / 100
            doubleMount.toString()
        } catch (e: Exception) {
            aMount
        }
    }
}