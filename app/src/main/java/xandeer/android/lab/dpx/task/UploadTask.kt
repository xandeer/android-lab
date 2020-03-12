package xandeer.android.lab.dpx.task

import android.net.Uri
import android.os.AsyncTask
import com.dropbox.core.v2.files.WriteMode
import timber.log.Timber
import xandeer.android.lab.App
import xandeer.android.lab.dpx.DbxClientFactory
import xandeer.android.lab.dpx.Local
import xandeer.android.lab.utils.fileName

class UploadTask(
  private val parent: String,
  private val uri: Uri,
  private val cb: Callback
) :
  AsyncTask<Void, Void, Any>() {

  interface Callback {
    fun onComplete()
    fun onError(e: Exception?)
  }

  private var exception: Exception? = null
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
    val path = "$parent/$name"
    Timber.d("Start uploading $path")

    try {
      App.context.contentResolver.openInputStream(uri)?.use {
        val size = it.available()
        val v = DbxClientFactory.get().files().uploadBuilder(path)
          .withMode(WriteMode.OVERWRITE)
          .uploadAndFinish(it) { l ->
            Timber.d("Uploading... ${l * 100 / size}%")
          }

        val file = Local.getFile(path)

        if (!file.exists()) file.createNewFile()
        file.writeBytes(it.readBytes())

        Local.saveRev(path, v.rev)
        Timber.d("Upload $path finished.")
      }
    } catch (e: Exception) {
      exception = e
    }
    return null
  }
}