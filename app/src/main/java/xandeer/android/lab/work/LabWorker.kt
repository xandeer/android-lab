package xandeer.android.lab.work

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class LabWorker(appContext: Context, workerParams: WorkerParameters) :
  Worker(appContext, workerParams) {
  override fun doWork(): Result {
    return Result.success()
  }
}