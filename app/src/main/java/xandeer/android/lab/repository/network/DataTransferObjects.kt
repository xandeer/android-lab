package xandeer.android.lab.repository.network

import com.squareup.moshi.JsonClass
import xandeer.android.lab.repository.database.DatabaseVideo
import xandeer.android.lab.repository.domain.DevByteVideo

/**
 * DataTransferObjects go in this file. These are responsible for parsing responses from the server
 * or formatting objects to send to the server. You should convert these to domain objects before
 * using them.
 *
 * @see domain package for
 */

/**
 * VideoHolder holds a list of Videos.
 *
 * This is to parse first level of our network result which looks like
 *
 * {
 *   "videos": []
 * }
 */
@JsonClass(generateAdapter = true)
data class NetworkVideoContainer(val videos: List<NetworkVideo>)

/**
 * Videos represent a devbyte that can be played.
 */
@JsonClass(generateAdapter = true)
data class NetworkVideo(
  val title: String,
  val description: String,
  val url: String,
  val updated: String,
  val thumbnail: String,
  val closedCaptions: String?
)

/**
 * Convert Network results to domain objects
 */
fun NetworkVideoContainer.asDomainModel(): List<DevByteVideo> {
  return videos.map {
    DevByteVideo(
      title = it.title,
      description = it.description,
      url = it.url,
      updated = it.updated,
      thumbnail = it.thumbnail
    )
  }
}

/**
 * Convert Network results to database objects
 */
fun NetworkVideoContainer.asDatabaseModel(): List<DatabaseVideo> {
  return videos.map {
    DatabaseVideo(
      title = it.title,
      description = it.description,
      url = it.url,
      updated = it.updated,
      thumbnail = it.thumbnail
    )
  }
}
