package xandeer.android.lab.dpx.worker

import android.content.Context
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.dropbox.core.v2.files.WriteMode
import timber.log.Timber
import xandeer.android.lab.dpx.DbxClientFactory
import xandeer.android.lab.dpx.DropboxVM.Companion.UPDATE_TAG
import xandeer.android.lab.dpx.Local

class UploadWorker(appContext: Context, workerParameters: WorkerParameters) :
  Worker(appContext, workerParameters) {

  companion object {
    private const val PATH = "PATH"

    fun get(path: String): OneTimeWorkRequest {
      val data = Data.Builder()
        .putString(PATH, path)
        .build()
      return OneTimeWorkRequest.Builder(UploadWorker::class.java)
        .setInputData(data)
        .addTag(UPDATE_TAG)
        .build()
    }
  }

  override fun doWork(): Result {
    val path = inputData.getString(PATH) ?: ""

    try {
      Timber.d("Start uploading $path")
      val files = DbxClientFactory.get().files()

      val file = Local.getFile(path)

      file.inputStream().use {
        val v = files.uploadBuilder(path)
          .withMode(WriteMode.OVERWRITE)
          .uploadAndFinish(it) { l ->
            Timber.d("Uploading... ${l * 100 / file.length()}%")
          }

        Local.saveRev(path, v.rev)
      }
      Local.uploaded(path)
      Timber.d("Upload $path finished.")
      return Result.success()
    } catch (e: Exception) {
      Timber.d(e, "Failed to upload file $path")
      return Result.failure()
    }
  }
}