package uz.orign.edittextvalidatorlib.validator

interface BaseValidation {

    fun validate(text: String): Boolean

    fun errorMessage(): String
}