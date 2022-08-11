package uz.orign.edittextvalidatorlib

import android.widget.EditText
import java.util.regex.Matcher
import java.util.regex.Pattern

object ValidatorEditText {
    fun setError(editText: EditText, message: String){
        editText.error = message
    }

    fun isEmptyValid(message: String):Boolean{
        if (message.isEmpty()) return false
        return true
    }

    fun isEmailValid(email: String?): Boolean {
        val expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$"
        val pattern: Pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
        val matcher: Matcher = pattern.matcher(email)
        return matcher.matches()
    }

    fun isPasswordValid(pass:String, cpass:String): Boolean{
        if(pass != cpass) {
            return false
        }
        return true
    }

    fun isNameValid(name:String):Boolean{
        val ps = Pattern.compile("^[a-zA-Z ]+$")
        val ms = ps.matcher(name)
        val bs = ms.matches()
        if (!bs) {
           return false
        }
        return true
    }

    fun isPhoneNumberValid(phone:String):Boolean{

        val r = Pattern.compile("^s*+(d{1,3})?[-. (]*(d{3})[-. )]*(d{3})[-. ]*(d{4})(?: *x(d+))?s*\$")

        val m = r.matcher(phone.trim());

        return m.find()
    }

    fun isIpAddressValid(ipAddress:String):Boolean{

        val IP_ADDRESS = Pattern.compile(
            "((25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9])\\.(25[0-5]|2[0-4]"
                    + "[0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]"
                    + "[0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}"
                    + "|[1-9][0-9]|[0-9]))")
        val matcher = IP_ADDRESS.matcher(ipAddress);
        if (matcher.matches()) {
            return true
        }


        return false
    }

//    fun isZipCodeValid(zipCode:String):Boolean{
//        var isValidZip = /(^\d{5}$)|(^\d{5}-\d{4}$)/.test(zipCode);
//    }



    fun validate(date: String?): Boolean {
         var pattern: Pattern? = null
         var matcher: Matcher? = null

        val DATE_PATTERN = "(0?[1-9]|1[012]) [/.-] (0?[1-9]|[12][0-9]|3[01]) [/.-] ((19|20)\\d\\d)";

        pattern = Pattern.compile(DATE_PATTERN)
        matcher = pattern.matcher(date)
        return if (matcher.matches()) {
            matcher.reset()
            if (matcher.find()) {
                val day: String = matcher.group(1)
                val month: String = matcher.group(2)
                val year: Int = matcher.group(3).toInt()
                if (day == "31" && (month == "4" || month == "6" || month == "9" || month == "11" || month == "04" || month == "06" || month == "09")) { false // only 1,3,5,7,8,10,12 has 31 days
                } else
                    if (month == "2" || month == "02") {
                    //leap year
                    if (year % 4 == 0) {
                        !(day == "30" || day == "31")
                    } else {
                        !(day == "29" || day == "30" || day == "31")
                    }
                } else {
                    true
                }
            } else {
                false
            }
        } else {
            false
        }
    }
}
