package uz.orign.edittextvalidatorlib.validator

import android.util.Patterns

class PhoneValidation(private val error: String = "Illegal phone number"): BaseValidation {
    override fun validate(text: String): Boolean = text.matches(Regex("^[+]?[0-9]{10,13}\$"))
    override fun errorMessage(): String = error
}