package xandeer.android.lab.dpx

import android.content.Context.MODE_PRIVATE
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import com.dropbox.core.DbxException
import com.dropbox.core.v2.users.FullAccount
import timber.log.Timber
import xandeer.android.lab.App.Companion.context
import xandeer.android.lab.dpx.task.DownloadTask
import xandeer.android.lab.dpx.task.GetCurrentAccountTask
import xandeer.android.lab.dpx.task.ListFolderTask
import xandeer.android.lab.dpx.worker.DownloadWorker
import xandeer.android.lab.dpx.worker.UploadWorker
import java.io.File

class DropboxVM : ViewModel() {
  companion object {
    private const val TOKEN = "TOKEN"
    const val UPDATE_TAG = "UPDATE_TAG"
  }

  private val sp =
    context.getSharedPreferences("dropbox", MODE_PRIVATE)

  val token = MutableLiveData<String>().apply {
    value = sp.getString(TOKEN, "")
  }

  fun update(v: String) {
    token.value = v
    sp.edit().putString(TOKEN, v).apply()
  }

  val account = MutableLiveData<FullAccount>()
  val exception = MutableLiveData<Exception>()

  fun getAccount() {
    GetCurrentAccountTask(
      object : GetCurrentAccountTask.Callback {
        override fun onComplete(result: FullAccount) {
          account.value = result
        }

        override fun onError(e: DbxException?) {
          exception.value = e
          Timber.e(e, "Failed to get dropbox account details")
        }
      }).execute()
  }

  val uriToUpload = MutableLiveData<Uri>()

  val files = MutableLiveData<Array<File>>()

  fun listFolder(path: String) {
    files.value = Local.listFolder(path)
  }

  fun pullFolder(path: String) {
    ListFolderTask(path, object : ListFolderTask.Callback {
      override fun onComplete() {
      }

      override fun onError(e: Exception?) {
        Timber.e(e, "Failed to list folder: $path")
      }
    }).execute()
  }

  val folderPath = MutableLiveData<String>()

  fun goto(path: String) {
    folderPath.value = path
  }

  fun download(path: String) {
    DownloadTask(path, object : DownloadTask.Callback {
      override fun onComplete() {
      }

      override fun onError(e: Exception?) {
        Timber.e(e, "Failed to download $path")
      }
    }).execute()
  }

  private val wm = WorkManager.getInstance(context)

  fun downloadByWorker(path: String) {
    wm.beginUniqueWork(
      path,
      ExistingWorkPolicy.KEEP,
      DownloadWorker.get(path)
    ).enqueue()
  }

  fun upload(files: Array<File>?) {
    files?.forEach {
      val path = Local.getPath(it)
      wm.beginUniqueWork(
        path,
        ExistingWorkPolicy.REPLACE,
        UploadWorker.get(path)
      ).enqueue()
    }
  }

  val workInfos =
    wm.getWorkInfosByTagLiveData(UPDATE_TAG)
}