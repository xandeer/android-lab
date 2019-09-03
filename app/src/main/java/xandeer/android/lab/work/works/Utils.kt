package xandeer.android.lab.work.works

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import timber.log.Timber
import xandeer.android.lab.work.Constants
import android.renderscript.Element.U8_4
import android.renderscript.ScriptIntrinsicBlur
import android.renderscript.Allocation
import android.renderscript.RenderScript
import androidx.annotation.WorkerThread
import java.io.File
import java.io.FileOutputStream
import java.util.UUID.randomUUID
import androidx.core.app.NotificationManagerCompat
import android.app.NotificationManager
import android.app.NotificationChannel
import android.os.Build
import androidx.core.app.NotificationCompat
import xandeer.android.lab.R


object Utils {
  fun makeStatusNotification(message: String, context: Context) {
    // Make a channel if necessary
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      // Create the NotificationChannel, but only on API 26+ because
      // the NotificationChannel class is new and not in the support library
      val name = Constants.VERBOSE_NOTIFICATION_CHANNEL_NAME
      val description = Constants.VERBOSE_NOTIFICATION_CHANNEL_DESCRIPTION
      val importance = NotificationManager.IMPORTANCE_HIGH
      val channel = NotificationChannel(Constants.CHANNEL_ID, name, importance)
      channel.description = description

      // Add the channel
      val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE)
            as NotificationManager

      notificationManager.createNotificationChannel(channel)
    }

    // Create the notification
    val builder = NotificationCompat.Builder(context, Constants.CHANNEL_ID)
      .setSmallIcon(R.drawable.ic_launcher_foreground)
      .setContentTitle(Constants.NOTIFICATION_TITLE)
      .setContentText(message)
      .setPriority(NotificationCompat.PRIORITY_HIGH)
      .setVibrate(LongArray(0))

    Timber.d("Notification message: $message")

    // Show the notification
    NotificationManagerCompat.from(context)
      .notify(Constants.NOTIFICATION_ID, builder.build())
  }

  fun sleep() {
    try {
      Thread.sleep(Constants.DELAY_MILLIS, 0)
    } catch (e: InterruptedException) {
      Timber.e(e.message)
    }
  }

  @WorkerThread
  fun blurBitmap(bitmap: Bitmap, appContext: Context): Bitmap {
    var rsContext: RenderScript? = null
    try {

      // Create the output bitmap
      val output = Bitmap.createBitmap(
        bitmap.width, bitmap.height, bitmap.config
      )

      // Blur the image
      rsContext = RenderScript.create(appContext, RenderScript.ContextType.DEBUG)
      val inAlloc = Allocation.createFromBitmap(rsContext, bitmap)
      val outAlloc = Allocation.createTyped(rsContext, inAlloc.type)
      val theIntrinsic = ScriptIntrinsicBlur.create(rsContext, U8_4(rsContext))
      theIntrinsic.setRadius(10f)
      theIntrinsic.setInput(inAlloc)
      theIntrinsic.forEach(outAlloc)
      outAlloc.copyTo(output)

      return output
    } finally {
      rsContext?.finish()
    }
  }

  fun writeBitmapToFile(appContext: Context, bitmap: Bitmap): Uri {
    val name = String.format("blur-output-%s.png", randomUUID().toString())
    val outputDir = File(appContext.filesDir, Constants.OUTPUT_PATH)
    if (!outputDir.exists()) {
      outputDir.mkdirs() // should succeed
    }
    val outputFile = File(outputDir, name)
    FileOutputStream(outputFile).use {
      bitmap.compress(Bitmap.CompressFormat.PNG, 0, it)
    }
    return Uri.fromFile(outputFile)
  }
}
