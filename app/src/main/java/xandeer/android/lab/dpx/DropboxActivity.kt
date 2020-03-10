package xandeer.android.lab.dpx

import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import com.dropbox.core.android.Auth
import com.dropbox.core.v2.users.FullAccount
import kotlinx.android.synthetic.main.dropbox_activity.*
import timber.log.Timber
import xandeer.android.lab.AbstractActivity
import xandeer.android.lab.R
import xandeer.android.lab.utils.getVm
import xandeer.android.lab.utils.observe

class DropboxActivity : AbstractActivity() {
  private lateinit var vm: DropboxVM

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.dropbox_activity)

    vm = getVm(DropboxVM::class.java)

    subscribeVm()
    setCallbacks()
  }

  private fun subscribeVm() {
    observe(vm.token, ::updateBy)
    observe(vm.account, ::updateBy)
  }

  private fun updateBy(token: String) {
    if (token.isBlank()) {
      account_info.visibility = GONE
      login.visibility = VISIBLE
    } else {
      account_info.visibility = VISIBLE
      login.visibility = GONE
      DbxClientFactory.init(token)
      vm.getAccount()
    }
  }

  private fun updateBy(account: FullAccount) {
    name.text = account.name.displayName
    email.text = account.email
  }

  private var isUpdatingToken = false
  private fun setCallbacks() {
    login.setOnClickListener {
      isUpdatingToken = true
      Auth.startOAuth2Authentication(this, getString(R.string.dropbox_key))
    }

    logout.setOnClickListener { vm.update("") }
  }

  override fun onResume() {
    super.onResume()

    if (isUpdatingToken) {
      updateToken()
    }
  }

  private fun updateToken() {
    isUpdatingToken = false

    val token = Auth.getOAuth2Token()

    token?.let{
      vm.update(it)
    }

    Timber.d("token: $token")
  }
}