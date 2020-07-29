package xandeer.android.lab

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import timber.log.Timber
import xandeer.android.lab.utils.log.TimberTreeFactory

class App : Application() {
  companion object {
    private const val DEFAULT_SHARED_PREFERENCES_NAME = "default"
    lateinit var context: App
  }

  override fun onCreate() {
    super.onCreate()
    context = this

    Timber.plant(TimberTreeFactory.getTree())
  }

  val defaultSharedPreferences: SharedPreferences
    get() = getSharedPreferences(
      DEFAULT_SHARED_PREFERENCES_NAME,
      Context.MODE_PRIVATE
    )
}