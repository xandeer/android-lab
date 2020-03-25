package xandeer.android.lab.dbx

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.dropbox.core.v2.users.FullAccount
import xandeer.android.lab.App.Companion.context
import xandeer.android.lab.dbx.worker.DownloadWorker
import xandeer.android.lab.dbx.worker.GetAccountWorker
import xandeer.android.lab.dbx.worker.ListWorker
import xandeer.android.lab.dbx.worker.UploadWorker
import java.io.File

class DropboxVM : ViewModel() {
  companion object {
    const val UPDATE_TAG = "UPDATE_TAG"
    const val GET_ACCOUNT_TAG = "GET_ACCOUNT_TAG"
  }

  val token = MutableLiveData<String>().apply {
    value = DbxData.token
  }

  fun update(v: String) {
    token.value = v
    DbxData.token = v
  }

  val account = MutableLiveData<FullAccount>()
  val exception = MutableLiveData<Exception>()

  fun getAccount() {
    wm.beginUniqueWork(
      GET_ACCOUNT_TAG,
      ExistingWorkPolicy.KEEP,
      OneTimeWorkRequest.from(GetAccountWorker::class.java)
    ).enqueue()
  }

  val uriToUpload = MutableLiveData<Uri>()

  val files = MutableLiveData<Array<File>>()

  fun listFolder(path: String) {
    files.value = Local.listFolder(path)
  }

  fun pullFolder(path: String) {
    wm.beginUniqueWork(
      path,
      ExistingWorkPolicy.KEEP,
      ListWorker.get(path)
    ).enqueue()
  }

  val folderPath = MutableLiveData<String>()

  fun goto(path: String) {
    folderPath.value = path
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