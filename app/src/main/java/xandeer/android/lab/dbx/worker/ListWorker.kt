package xandeer.android.lab.dbx.worker

import android.content.Context
import androidx.work.*
import com.dropbox.core.v2.files.DeletedMetadata
import com.dropbox.core.v2.files.FileMetadata
import com.dropbox.core.v2.files.FolderMetadata
import timber.log.Timber
import xandeer.android.lab.App.Companion.context
import xandeer.android.lab.dbx.DbxClientFactory
import xandeer.android.lab.dbx.Local

class ListWorker(appContext: Context, workerParameters: WorkerParameters) :
  Worker(appContext, workerParameters) {

  companion object {
    private const val PATH = "PATH"

    fun get(path: String): OneTimeWorkRequest {
      val data = Data.Builder()
        .putString(PATH, path)
        .build()
      return OneTimeWorkRequest.Builder(ListWorker::class.java)
        .setInputData(data)
        .build()
    }
  }

  override fun doWork(): Result {
    val path = inputData.getString(PATH) ?: ""

    return try {
      val files = DbxClientFactory.get().files()

      val cursor = Local.getCursor(path)

      val res =
        if (cursor.isBlank()) files.listFolder(path)
        else files.listFolderContinue(cursor)

      var r = res

      while (r.hasMore) {
        r = files.listFolderContinue(r.cursor)
        res.entries.addAll(r.entries)
      }

      res.entries.forEach {
        when (it) {
          is FolderMetadata -> Local.createFolder(it.pathDisplay)
          is FileMetadata -> download(it.pathDisplay)
          is DeletedMetadata -> Local.delete(it.pathDisplay)
        }
      }

      Local.saveCursor(path, r.cursor)

      Result.success()
    } catch (e: Exception) {
      Timber.e("Failed to list folder: $path", e)
      Result.failure()
    }
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