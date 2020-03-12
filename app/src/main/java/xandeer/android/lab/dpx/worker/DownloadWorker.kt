package xandeer.android.lab.dpx.worker

import android.content.Context
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.dropbox.core.v2.files.FileMetadata
import timber.log.Timber
import xandeer.android.lab.dpx.DbxClientFactory
import xandeer.android.lab.dpx.DropboxVM.Companion.UPDATE_TAG
import xandeer.android.lab.dpx.Local

class DownloadWorker(appContext: Context, workerParameters: WorkerParameters) :
  Worker(appContext, workerParameters) {

  companion object {
    private const val PATH = "PATH"

    fun get(path: String): OneTimeWorkRequest {
      val data = Data.Builder()
        .putString(PATH, path)
        .build()
      return OneTimeWorkRequest.Builder(DownloadWorker::class.java)
        .setInputData(data)
        .addTag(UPDATE_TAG)
        .build()
    }
  }

  override fun doWork(): Result {
    val path = inputData.getString(PATH) ?: ""
    Timber.d("Start downloading $path")
    Local.startDownloading(path)

    val e =
      try {
        val files = DbxClientFactory.get().files()
        if ((files.getMetadata(path) as FileMetadata).rev == Local.getRev(path)) {
          Timber.d("The file is updated.")
        } else {
          val download = files.download(path)
          val file = Local.getFile(path)

          file.outputStream().use {
            download.download(it)
          }

          Local.saveRev(path, download.result.rev)
          Timber.d("Download $path finished.")
        }
        null
      } catch (e: Exception) {
        Timber.d(e, "Failed to download file $path")
        e
      }

    Local.quitDownloading(path)
    return if (e == null) Result.success()
    else Result.failure()
  }
}