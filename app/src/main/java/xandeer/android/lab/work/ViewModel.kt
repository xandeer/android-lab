package xandeer.android.lab.work

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import xandeer.android.lab.work.works.BlurWorker
import xandeer.android.lab.work.works.CleanupWorker
import androidx.work.*
import xandeer.android.lab.work.works.SavePictureToFileWorker


class ViewModel(app: Application) : AndroidViewModel(app) {
  private val workManager = WorkManager.getInstance(app)

  val pictureUri: MutableLiveData<Uri?> by lazy {
    MutableLiveData<Uri?>().apply {
      value = null
    }
  }

  var blurredUri: Uri? = null

  val savedWorkIn =
    workManager.getWorkInfosByTagLiveData(Constants.TAG_OUTPUT)

  fun setPictureUri(uri: Uri) {
    pictureUri.value = uri
  }

  fun applyBlur(level: Int) {
    // Add WorkRequest to Cleanup temporary images
    var continuation = workManager
      .beginUniqueWork(
        Constants.PICTURE_MANIPULATION_WORK_NAME,
        ExistingWorkPolicy.REPLACE,
        OneTimeWorkRequest.from(CleanupWorker::class.java)
      )

    // Add WorkRequests to blur the image the number of times requested
    for (i in 0 until level) {
      val blurBuilder = OneTimeWorkRequest.Builder(BlurWorker::class.java)

      // Input the Uri if this is the first blur operation
      // After the first blur operation the input will be the output of previous
      // blur operations.
      if (i == 0) {
        blurBuilder.setInputData(createInputDataForUri())
      }

      continuation = continuation.then(blurBuilder.build())
    }

    // Create charging constraint
    val constraints = Constraints.Builder()
      .setRequiresCharging(true)
      .build()

    // Add WorkRequest to save the image to the filesystem
    val save = OneTimeWorkRequest.Builder(SavePictureToFileWorker::class.java)
      .setConstraints(constraints)
      .addTag(Constants.TAG_OUTPUT)
      .build()
    continuation = continuation.then(save)

    // Actually start the work
    continuation.enqueue()
  }

  private fun createInputDataForUri(): Data {
    val builder = Data.Builder()
    if (pictureUri.value != null) {
      builder.putString(Constants.PICTURE_URI_KEY, pictureUri.value.toString())
    }
    return builder.build()
  }

  fun cancelWork() {
    workManager.cancelUniqueWork(Constants.PICTURE_MANIPULATION_WORK_NAME)
  }
}