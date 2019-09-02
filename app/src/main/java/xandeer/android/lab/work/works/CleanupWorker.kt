package xandeer.android.lab.work.works

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import android.text.TextUtils
import timber.log.Timber
import xandeer.android.lab.work.Constants
import java.io.File

class CleanupWorker(appContext: Context, workerParams: WorkerParameters) :
  Worker(appContext, workerParams) {
  override fun doWork(): Result {
    // Makes a notification when the work starts and slows down the work so that it's easier to
    // see each WorkRequest start, even on emulated devices
    Utils.makeStatusNotification(
      "Cleaning up old temporary files",
      applicationContext
    )
    Utils.sleep()

    try {
      val outputDirectory = File(
        applicationContext.filesDir,
        Constants.OUTPUT_PATH
      )
      if (outputDirectory.exists()) {
        val entries = outputDirectory.listFiles()
        if (entries != null && entries.isNotEmpty()) {
          for (entry in entries) {
            val name = entry.name
            if (!TextUtils.isEmpty(name) && name.endsWith(".png")) {
              val deleted = entry.delete()
              Timber.i(String.format("Deleted %s - %s", name, deleted))
            }
          }
        }
      }
      return Result.success()
    } catch (exception: Exception) {
      Timber.e("Error cleaning up", exception)
      return Result.failure()
    }
  }
}