package uz.orign.edittextvalidatorlib

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface


object AlertFactory {
    fun showPickerDialg(context: Context?, title: String?, items: Array<String?>?, callback: DialogInterface.OnClickListener?) {
        if (items == null || context == null) {
            return
        }
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setTitle(title)
            .setCancelable(true)
            .setItems(items, callback)
            .create()
            .show()
    }
}