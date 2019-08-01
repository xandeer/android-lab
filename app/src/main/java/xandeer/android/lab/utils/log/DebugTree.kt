package xandeer.android.lab.utils.log

import timber.log.Timber

class DebugTree : Timber.DebugTree() {
  override fun createStackElementTag(element: StackTraceElement?): String {
    return String.format("Xandeer:${super.createStackElementTag(element)}")
  }
}