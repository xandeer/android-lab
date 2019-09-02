package xandeer.android.lab.work.works

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import android.text.TextUtils
import android.provider.MediaStore
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.work.Data
import timber.log.Timber
import xandeer.android.lab.work.Constants
import java.text.SimpleDateFormat
import java.util.*


class SavePictureToFileWorker(
  appContext: Context, workerParams: WorkerParameters
) : Worker(appContext, workerParams) {
  companion object {
    private const val TITLE = "Blurred Picture"
    private val DATE_FORMATTER =
      SimpleDateFormat("yyyy.MM.dd 'at' HH:mm:ss z", Locale.getDefault())
  }

  override fun doWork(): Result {
    // Makes a notification when the work starts and slows down the work so that it's easier to
    // see each WorkRequest start, even on emulated devices
    Utils.makeStatusNotification("Saving picture", applicationContext)
    Utils.sleep()

    val resolver = applicationContext.contentResolver
    try {
      val resourceUri = inputData
        .getString(Constants.PICTURE_URI_KEY)
      val bitmap = BitmapFactory.decodeStream(
        resolver.openInputStream(Uri.parse(resourceUri))
      )
      val imageUrl = MediaStore.Images.Media.insertImage(
        resolver, bitmap, TITLE, DATE_FORMATTER.format(Date())
      )
      if (TextUtils.isEmpty(imageUrl)) {
        Timber.e("Writing to MediaStore failed")
        return Result.failure()
      }
      val outputData = Data.Builder()
        .putString(Constants.PICTURE_URI_KEY, imageUrl)
        .build()
      return Result.success(outputData)
    } catch (exception: Exception) {
      Timber.e("Unable to save image to Gallery", exception)
      return Result.failure()
    }
  }
}