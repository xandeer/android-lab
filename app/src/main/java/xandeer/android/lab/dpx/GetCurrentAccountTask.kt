package xandeer.android.lab.dpx

import android.os.AsyncTask
import com.dropbox.core.DbxException
import com.dropbox.core.v2.DbxClientV2
import com.dropbox.core.v2.users.FullAccount

class GetCurrentAccountTask(
  private val client: DbxClientV2,
  private val cb: Callback
) : AsyncTask<Void, Void, FullAccount>() {

  interface Callback {
    fun onComplete(result: FullAccount)
    fun onError(e: DbxException)
  }

  private var exception: DbxException? = null
  override fun onPostExecute(result: FullAccount?) {
    super.onPostExecute(result)

    val e = exception
    if (e != null) {
      cb.onError(e)
    } else {
      result?.let { cb.onComplete(it) }
    }
  }

  override fun doInBackground(vararg p0: Void?): FullAccount? {
    try {
      return client.users().currentAccount
    } catch (e: DbxException) {
      exception = e
    }
    return null
  }
}