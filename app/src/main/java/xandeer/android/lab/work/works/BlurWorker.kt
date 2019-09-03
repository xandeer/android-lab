package xandeer.android.lab.work.works

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.text.TextUtils
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import timber.log.Timber
import xandeer.android.lab.work.Constants
import java.io.FileNotFoundException
import java.lang.IllegalArgumentException
import java.lang.RuntimeException

class BlurWorker(appContext: Context, workerParameters: WorkerParameters) :
  Worker(appContext, workerParameters) {
  override fun doWork(): Result {
    val resourceUri = inputData.getString(Constants.PICTURE_URI_KEY)

    Utils.makeStatusNotification("Blurring picture", applicationContext)
    Utils.sleep()

    try {
      if (TextUtils.isEmpty(resourceUri)) {
        Timber.e("Invalid input uri")
        throw IllegalArgumentException("Invalid input uri")
      }

      val bitmap = BitmapFactory.decodeStream(
        applicationContext.contentResolver.openInputStream(
          Uri.parse(resourceUri)
        )
      )

      Timber.d("Blurring uri: $resourceUri")

      val outputBitmap = Utils.blurBitmap(bitmap, applicationContext)
      val uri = Utils.writeBitmapToFile(applicationContext, outputBitmap)

      bitmap.recycle()
      outputBitmap.recycle()

      val output = Data.Builder().putString(
        Constants.PICTURE_URI_KEY, uri.toString()
      ).build()

      return Result.success(output)
    } catch (e: FileNotFoundException) {
      Timber.e("Failed to decode input stream", e)
      throw RuntimeException("Failed to decode input stream", e)
    } catch (throwable: Throwable) {
      Timber.e("Error applying blur", throwable)
      return Result.failure()
    }
  }
}