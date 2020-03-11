package xandeer.android.lab.utils

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*
import xandeer.android.lab.App

// ViewModel {
fun <T> AppCompatActivity.observe(data: LiveData<T>, fn: (it: T) -> Unit) {
  data.observe(this, Observer { it?.let(fn) })
}

fun <T> Context.observe(data: LiveData<T>, fn: (it: T) -> Unit) =
  (this as AppCompatActivity).observe(data, fn)

fun <T> View.observe(data: LiveData<T>, fn: (it: T) -> Unit) =
  context.observe(data, fn)

fun <T> MutableLiveData<T>.notify() {
  value = value
}

fun <T : ViewModel> Context.getVm(clazz: Class<T>) =
  ViewModelProviders.of(this as AppCompatActivity).get(clazz)
// ViewModel }

// Uri {
val Uri.fileName: String
  get() {
    var result: String? = null
    if (scheme == "content") {
      App.context.contentResolver.query(this, null, null, null, null)
        ?.use {
          if (it.moveToFirst()) {
            result =
              it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
          }
        }
    }

    if (result == null) {
      result = path
      val cut = result?.lastIndexOf('/')
      if (cut != null && cut != -1) {
        result = result?.substring(cut + 1)
      }
    }

    return result ?: "untitled"
  }
// Uri }