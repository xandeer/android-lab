package xandeer.android.lab.pdftopng

import android.net.Uri
import androidx.core.content.edit
import androidx.lifecycle.*
import androidx.lifecycle.ViewModel
import xandeer.android.lab.App

class ViewModel : ViewModel() {
  companion object {
    private const val PDF_URI_KEY = "PDF_TO_PNG:PDF_URI"
  }

  var scale
    get() = _scale.value!!
    set(v) {
      _scale.value = v
    }

  var pdfUri
    get() = _pdfUri.value
    set(uri) {
      _pdfUri.value = uri
      _app.defaultSharedPreferences.edit(true) {
        putString(PDF_URI_KEY, uri.toString())
      }
    }

  private lateinit var _app: App

  private val _scale: MutableLiveData<Int> = MutableLiveData<Int>().apply {
    value = 1
  }

  private val _pdfUri: MutableLiveData<Uri?> by lazy {
    MutableLiveData<Uri?>().apply {
      val str = _app.defaultSharedPreferences.getString(PDF_URI_KEY, "") ?: ""
      value = if (str.isEmpty()) null else Uri.parse(str)
    }
  }

  fun setApp(app: App) {
    _app = app
  }

  fun getScale(): LiveData<Int> = _scale

  fun increaseScale() {
    scale++
  }

  fun decreaseScale() {
    if (scale > 1) {
      scale--
    }
  }

  fun getPdfUri(): LiveData<Uri?> = _pdfUri
}