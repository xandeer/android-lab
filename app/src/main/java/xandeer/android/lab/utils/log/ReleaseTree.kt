package xandeer.android.lab.utils.log

import android.util.Log
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable
import timber.log.Timber

class ReleaseTree : Timber.Tree() {
  override fun log(
    priority: Int,
    tag: @Nullable String?,
    message: @NotNull String,
    t: @Nullable Throwable?
  ) {
    if (priority == Log.ERROR || priority == Log.WARN) {
      //SEND ERROR REPORTS TO YOUR Crashlytics.
    }
  }
}