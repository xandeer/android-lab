package xandeer.android.lab.dpx

import android.content.Context.MODE_PRIVATE
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dropbox.core.DbxException
import com.dropbox.core.v2.users.FullAccount
import timber.log.Timber
import xandeer.android.lab.App.Companion.context
import java.io.File

class DropboxVM : ViewModel() {
  companion object {
    private const val TOKEN = "TOKEN"
  }

  private val sp =
    context.getSharedPreferences("dropbox", MODE_PRIVATE)

  val token = MutableLiveData<String>().apply {
    value = sp.getString(TOKEN, "")
  }

  fun update(v: String) {
    token.value = v
    sp.edit().putString(TOKEN, v).apply()
  }

  val account = MutableLiveData<FullAccount>()
  val exception = MutableLiveData<Exception>()

  fun getAccount() {
    GetCurrentAccountTask(
      object : GetCurrentAccountTask.Callback {
        override fun onComplete(result: FullAccount) {
          account.value = result
        }

        override fun onError(e: DbxException?) {
          exception.value = e
          Timber.e(e, "Failed to get dropbox account details")
        }
      }).execute()
  }

  val uriToUpload = MutableLiveData<Uri>()
}