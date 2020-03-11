package xandeer.android.lab.dpx

import android.net.Uri
import android.os.AsyncTask
import com.dropbox.core.DbxException
import com.dropbox.core.v2.files.WriteMode
import timber.log.Timber
import xandeer.android.lab.App
import xandeer.android.lab.utils.fileName

class UploadTask(
  private val uri: Uri,
  private val cb: Callback
) :
  AsyncTask<Void, Void, Any>() {

  interface Callback {
    fun onComplete()
    fun onError(e: DbxException?)
  }

  private var exception: DbxException? = null
  override fun onPostExecute(o: Any?) {
    super.onPostExecute(o)

    if (exception == null) {
      cb.onComplete()
    } else {
      cb.onError(exception)
    }
  }

  override fun doInBackground(vararg p0: Void?): Any? {
    val name = uri.fileName
    Timber.d("Start uploading $name")

    try {
      App.context.contentResolver.openInputStream(uri)?.use {
        val size = it.available()
        DbxClientFactory.get().files().uploadBuilder("/$name")
          .withMode(WriteMode.OVERWRITE)
          .uploadAndFinish(it) { l ->
            Timber.d("Uploading... ${l * 100 / size}%")
          }
      }
    } catch (e: DbxException) {
      exception = e
    }
    return null
  }
}