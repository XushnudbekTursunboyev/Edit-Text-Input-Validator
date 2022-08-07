package uz.orign.edittextvalidatorlib

import uz.orign.edittextvalidatorlib.validator.BaseValidation
import uz.orign.edittextvalidatorlib.validator.EmailValidation
import uz.orign.edittextvalidatorlib.validator.PhoneValidation

class Validator(private val text: String) {

    private val validationsList = ArrayList<BaseValidation>()
    private var errorMessage: String = ""

    private var validatorCallback: ValidatorCallback? = null

    fun validate(): Boolean {
        for (validation in validationsList){
            if (!validation.validate(text)){
                setError(validation.errorMessage())
                validatorCallback?.onFailure(errorMessage)
                return false
            }
        }

        validatorCallback?.onSuccess()
        return true
    }

    private fun addValidation(validation: BaseValidation): Validator {
        validationsList.add(validation)
        return this
    }

    private fun setError(errorMessage: String): Validator {
        this.errorMessage = errorMessage
        return this
    }

    fun addCallback(callback: ValidatorCallback): Validator {
        validatorCallback = callback
        return this
    }

    fun email(errorMessage: String? = null): Validator {
        val validation = errorMessage?.let { EmailValidation(it) }?: EmailValidation()
        addValidation(validation)
        return this
    }

    fun phone(errorMessage: String? = null): Validator {
        val validation = errorMessage?.let { PhoneValidation(it) }?: PhoneValidation()
        addValidation(validation)
        return this
    }
}