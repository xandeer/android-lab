package xandeer.android.lab.pdftopng

import android.content.SharedPreferences
import android.net.Uri
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ViewModel : ViewModel() {
  companion object {
    private const val PDF_URI_KEY = "PDF_URI"
  }

  private lateinit var sharedPreferences: SharedPreferences

  private val scale: MutableLiveData<Int> = MutableLiveData<Int>().apply {
    value = 1
  }

  private val pdfUri: MutableLiveData<Uri> by lazy {
    MutableLiveData<Uri>().apply {
      val str = sharedPreferences.getString(PDF_URI_KEY, "") ?: ""
      value = Uri.parse(str)
    }
  }

  fun setSharedPreferences(sharedPreferences: SharedPreferences) {
    this.sharedPreferences = sharedPreferences
  }

  fun getScale(): LiveData<Int> = scale

  fun increaseScale() {
    val v = scale.value
    if (v != null) {
      scale.value = v.plus(1)
    }
  }

  fun decreaseScale() {
    val v = scale.value
    if (v != null && v > 1) {
      scale.value = v.minus(1)
    }
  }

  fun getPdfUri(): LiveData<Uri> = pdfUri

  fun setPdfPath(uri: Uri) {
    pdfUri.value = uri
    sharedPreferences.edit(true) {
      putString(PDF_URI_KEY, uri.toString())
    }
  }
}