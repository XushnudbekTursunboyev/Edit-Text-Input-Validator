package uz.orign.edittextvalidatorlib

interface ValidatorCallback {
    fun onSuccess()
    fun onFailure(error: String)
}