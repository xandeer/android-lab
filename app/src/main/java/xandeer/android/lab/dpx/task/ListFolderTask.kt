package xandeer.android.lab.dpx.task

import android.os.AsyncTask
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import com.dropbox.core.v2.files.DeletedMetadata
import com.dropbox.core.v2.files.FileMetadata
import com.dropbox.core.v2.files.FolderMetadata
import timber.log.Timber
import xandeer.android.lab.App.Companion.context
import xandeer.android.lab.dpx.DbxClientFactory
import xandeer.android.lab.dpx.Local
import xandeer.android.lab.dpx.worker.DownloadWorker

class ListFolderTask(
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
  }

  override fun doInBackground(vararg p0: Void?): Void? {
    try {
      val files = DbxClientFactory.get().files()

      val cursor = Local.getCursor(path)

      val res =
        if (cursor.isBlank()) files.listFolder(path)
        else files.listFolderContinue(cursor)

      var r = res

      while (r.hasMore) {
        Timber.d("Has more...")
        r = files.listFolderContinue(r.cursor)
        res.entries.addAll(r.entries)
      }

      Timber.d(res.toString())

      res.entries.forEach {
        when (it) {
          is FolderMetadata -> Local.createFolder(it.pathDisplay)
          is FileMetadata -> download(it.pathDisplay)
          is DeletedMetadata -> Local.delete(it.pathDisplay)
        }
      }

      Local.saveCursor(path, r.cursor)
    } catch (e: Exception) {
      exception = e
    }
    return null
  }

  private fun download(path: String) {
    WorkManager.getInstance(context)
      .beginUniqueWork(
        path,
        ExistingWorkPolicy.KEEP,
        DownloadWorker.get(path)
      ).enqueue()
  }
}