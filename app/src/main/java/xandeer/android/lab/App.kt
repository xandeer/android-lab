package xandeer.android.lab

import android.app.Application
import android.content.Context
import android.content.SharedPreferences

class App : Application() {
  companion object {
    private const val DEFAULT_SHARED_PREFERENCES_NAME = "default"
  }

  val defaultSharedPreferences: SharedPreferences
    get() = getSharedPreferences(DEFAULT_SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
}