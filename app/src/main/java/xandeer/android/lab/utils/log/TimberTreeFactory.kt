package xandeer.android.lab.utils.log

import timber.log.Timber
import xandeer.android.lab.BuildConfig

object TimberTreeFactory {
  fun getTree(): Timber.Tree {
    return if (BuildConfig.DEBUG) DebugTree()
    else ReleaseTree()
  }
}