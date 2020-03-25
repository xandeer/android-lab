package xandeer.android.lab.dbx

import com.dropbox.core.DbxRequestConfig
import com.dropbox.core.v2.DbxClientV2

object DbxClientFactory {
  private lateinit var client: DbxClientV2

  fun init(token: String) {
    if (!::client.isInitialized) {
      client = DbxClientV2(DbxRequestConfig("xandeer-lab"), token)
    }
  }

  fun get(): DbxClientV2 {
    if (::client.isInitialized) {
      return client
    } else {
      throw IllegalStateException("Dropbox client not initialized.")
    }
  }
}