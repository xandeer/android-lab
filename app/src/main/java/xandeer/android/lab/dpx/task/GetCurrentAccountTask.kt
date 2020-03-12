package xandeer.android.lab.dpx.task

import android.os.AsyncTask
import com.dropbox.core.DbxException
import com.dropbox.core.v2.users.FullAccount
import xandeer.android.lab.dpx.DbxClientFactory

class GetCurrentAccountTask(private val cb: Callback) :
  AsyncTask<Void, Void, FullAccount>() {

  interface Callback {
    fun onComplete(result: FullAccount)
    fun onError(e: DbxException?)
  }

  private var exception: DbxException? = null
  override fun onPostExecute(result: FullAccount?) {
    super.onPostExecute(result)

    if (exception == null && result != null) {
      cb.onComplete(result)
    } else {
      cb.onError(exception)
    }
  }

  override fun doInBackground(vararg p0: Void?): FullAccount? {
    try {
      return DbxClientFactory.get().users().currentAccount
    } catch (e: DbxException) {
      exception = e
    }
    return null
  }
}