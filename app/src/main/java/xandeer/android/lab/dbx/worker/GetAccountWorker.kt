package xandeer.android.lab.dbx.worker

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import timber.log.Timber
import xandeer.android.lab.dbx.DbxClientFactory
import xandeer.android.lab.dbx.DbxData

class GetAccountWorker(appContext: Context, workerParameters: WorkerParameters) :
  Worker(appContext, workerParameters) {

  override fun doWork(): Result {
    return try {
      val account = DbxClientFactory.get().users().currentAccount

      DbxData.name = account.name.displayName
      DbxData.uid = account.accountId.replace(":", "-")
      Result.success()
    } catch (e: Exception) {
      Timber.e("Failed to get dropbox account info.", e)
      Result.failure()
    }
  }
}