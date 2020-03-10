package xandeer.android.lab.dpx

import android.content.Context.MODE_PRIVATE
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dropbox.core.DbxException
import com.dropbox.core.v2.users.FullAccount
import timber.log.Timber
import xandeer.android.lab.App.Companion.context

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
    GetCurrentAccountTask(DbxClientFactory.get(), object : GetCurrentAccountTask.Callback {
      override fun onComplete(result: FullAccount) {
        account.value = result
      }

      override fun onError(e: DbxException) {
        exception.value = e
        Timber.w("Failed to get dropbox account details", e)
        Timber.e(e)
      }
    }).execute()
  }
}