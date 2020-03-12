package xandeer.android.lab.dpx.task

import android.os.AsyncTask
import com.dropbox.core.v2.files.FileMetadata
import timber.log.Timber
import xandeer.android.lab.dpx.DbxClientFactory
import xandeer.android.lab.dpx.Local

class DownloadTask(
  private val path: String,
  private val cb: Callback
) : AsyncTask<Void, Void, Void>() {

  interface Callback {
    fun onComplete()
    fun onError(e: Exception?)
  }

  private var exception: Exception? = null
  override fun onPostExecute(o: Void?) {
    super.onPostExecute(o)

    if (exception == null) {
      cb.onComplete()
    } else {
      cb.onError(exception)
    }
    Local.quitDownloading(path)
  }

  override fun doInBackground(vararg p0: Void?): Void? {
    try {
      Timber.d("Start downloading $path")
      Local.startDownloading(path)
      val files = DbxClientFactory.get().files()
      if ((files.getMetadata(path) as FileMetadata).rev == Local.getRev(path)) {
        Timber.d("The file is updated.")
        return null
      }

      val download = files.download(path)
      val file = Local.getFile(path)

      file.outputStream().use {
        download.download(it)
      }

      Local.saveRev(path, download.result.rev)
      Timber.d("Download $path finished.")
    } catch (e: Exception) {
      exception = e
    }
    return null
  }
}