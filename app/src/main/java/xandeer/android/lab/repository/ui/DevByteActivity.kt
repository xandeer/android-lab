package xandeer.android.lab.repository.ui

import android.os.Bundle
import xandeer.android.lab.AbstractActivity
import xandeer.android.lab.R

/**
 * This is a single activity application that uses the Navigation library. Content is displayed
 * by Fragments.
 */
class DevByteActivity : AbstractActivity() {

  /**
   * Called when the activity is starting.  This is where most initialization
   * should go
   */
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_dev_byte_viewer)
  }
}