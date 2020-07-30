package xandeer.android.lab.repository.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import xandeer.android.lab.repository.database.VideosDatabase
import xandeer.android.lab.repository.database.asDomainModel
import xandeer.android.lab.repository.domain.DevByteVideo
import xandeer.android.lab.repository.network.DevByteNetwork
import xandeer.android.lab.repository.network.asDatabaseModel

/**
 * Repository for fetching devbyte videos from the network and storing them
 * on disk.
 */
class VideosRepository(private val database: VideosDatabase) {
  /**
   * Refresh the video stored in the offline cache.
   *
   * This function uses the IO dispatcher to ensure the database insert
   * database operation happens on the IO dispatcher. By switching to the
   * IO dispatcher using `withContext` this function is now safe to call
   * from any thread including the Main thread.
   *
   */
  suspend fun refreshVideos() {
    withContext(Dispatchers.IO) {
      Timber.d("refresh videos is called")
      val playlist = DevByteNetwork.devbytes.getPlaylist().await()
      database.videoDao.insertAll(playlist.asDatabaseModel())
    }
  }

  val videos: LiveData<List<DevByteVideo>> =
    Transformations.map(database.videoDao.getVideos()) {
      it.asDomainModel()
    }
}
