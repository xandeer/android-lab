package xandeer.android.lab.dpx

import com.dropbox.core.DbxRequestConfig
import com.dropbox.core.http.OkHttp3Requestor

object DbxRequestConfigFactory {
  private lateinit var config: DbxRequestConfig

  fun get(): DbxRequestConfig {
    if (!::config.isInitialized) {
      config = DbxRequestConfig.newBuilder("xandeer-lab")
        .withHttpRequestor(OkHttp3Requestor(OkHttp3Requestor.defaultOkHttpClient()))
        .build()
    }

    return config
  }
}