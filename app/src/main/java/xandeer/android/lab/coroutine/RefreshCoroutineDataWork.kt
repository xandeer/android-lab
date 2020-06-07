package xandeer.android.lab.coroutine

import android.content.Context
import androidx.work.*

/**
 * Worker job to refresh titles from the network while the app is in the background.
 *
 * WorkManager is a library used to enqueue work that is guaranteed to execute after its constraints
 * are met. It can run work even when the app is in the background, or not running.
 */
class RefreshCoroutineDataWork(
  context: Context,
  params: WorkerParameters,
  private val network: CoroutineNetwork
) :
  CoroutineWorker(context, params) {

  /**
   * Refresh the title from the network using [TitleRepository]
   *
   * WorkManager will call this method from a background thread. It may be called even
   * after our app has been terminated by the operating system, in which case [WorkManager] will
   * start just enough to run this [Worker].
   */
  override suspend fun doWork(): Result {
    val database = getDatabase(applicationContext)
    val repository = TitleRepository(network, database.titleDao)

    return try {
      repository.refreshTitle()
      Result.success()
    } catch (e: TitleRefreshError) {
      Result.failure()
    }
  }

  class Factory(val network: CoroutineNetwork = getNetworkService()) :
    WorkerFactory() {
    override fun createWorker(
      appContext: Context,
      workerClassName: String,
      workerParameters: WorkerParameters
    ): ListenableWorker? {
      return RefreshCoroutineDataWork(appContext, workerParameters, network)
    }
  }
}