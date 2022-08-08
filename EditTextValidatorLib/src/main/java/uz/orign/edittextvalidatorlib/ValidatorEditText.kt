package uz.orign.edittextvalidatorlib

import android.widget.EditText

object ValidatorEditText {
    fun setError(editText: EditText, message: String){
        editText.error = message
    }
}