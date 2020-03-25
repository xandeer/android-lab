package xandeer.android.lab.dbx

import xandeer.android.lab.preference.Constants.DROPBOX_TOKEN
import xandeer.android.lab.preference.Constants.DROPBOX_USER_ID
import xandeer.android.lab.preference.Constants.DROPBOX_USER_NAME
import xandeer.android.lab.preference.DSP

object DbxData {
  private const val SYNC_TAG = "SYNC_TAG"

  fun getTag(path: String) = "$SYNC_TAG:$path"

  var token
    get() = DSP.get(DROPBOX_TOKEN, "")
    set(value) {
      DSP.set(DROPBOX_TOKEN, value)
    }

  var uid
    get() = DSP.get(DROPBOX_USER_ID, "unknown")
    set(value) {
      DSP.set(DROPBOX_USER_ID, value)
    }

  var name
    get() = DSP.get(DROPBOX_USER_NAME, "")
    set(value) {
      DSP.set(DROPBOX_USER_NAME, value)
    }
}