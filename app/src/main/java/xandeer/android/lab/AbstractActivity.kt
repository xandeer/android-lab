package xandeer.android.lab

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import timber.log.Timber

abstract class AbstractActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    Timber.i("Start activity ${javaClass.simpleName}")
  }

  override fun onResume() {
    super.onResume()
    Timber.i("Resume activity ${javaClass.simpleName}")
  }

  override fun onStop() {
    super.onStop()
    Timber.i("Stop activity ${javaClass.simpleName}")
  }
}