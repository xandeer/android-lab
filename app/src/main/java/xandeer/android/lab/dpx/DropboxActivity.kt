package xandeer.android.lab.dpx

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import com.dropbox.core.DbxException
import com.dropbox.core.android.Auth
import com.dropbox.core.v2.users.FullAccount
import kotlinx.android.synthetic.main.dropbox_activity.*
import timber.log.Timber
import xandeer.android.lab.AbstractActivity
import xandeer.android.lab.R
import xandeer.android.lab.utils.fileName
import xandeer.android.lab.utils.getVm
import xandeer.android.lab.utils.observe

class DropboxActivity : AbstractActivity() {
  companion object {
    private const val PICK_FILE_CODE = 0
  }

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
    observe(vm.uriToUpload, ::updateBy)
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

  private fun updateBy(uri: Uri) {
    UploadTask(uri, object : UploadTask.Callback {
      override fun onComplete() {
        val msg = "Upload ${uri.fileName} successfully."
        Timber.d(msg)
        Toast.makeText(
          this@DropboxActivity,
          msg,
          Toast.LENGTH_SHORT
        )
          .show()
      }

      override fun onError(e: DbxException?) {
        Timber.e(e, "Failed to upload file ${uri.fileName}.")
      }
    }).execute()
  }

  private var isUpdatingToken = false
  private fun setCallbacks() {
    login.setOnClickListener {
      isUpdatingToken = true
      Auth.startOAuth2Authentication(this, getString(R.string.dropbox_key))
    }

    logout.setOnClickListener { vm.update("") }

    upload.setOnClickListener { pickFile() }
  }

  private fun pickFile() {
    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
      addCategory(Intent.CATEGORY_OPENABLE)
      type = "*/*"
    }

    startActivityForResult(intent, PICK_FILE_CODE)
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

    token?.let {
      vm.update(it)
    }

    Timber.d("token: $token")
  }

  override fun onActivityResult(
    requestCode: Int,
    resultCode: Int,
    data: Intent?
  ) {
    super.onActivityResult(requestCode, resultCode, data)

    if (resultCode != Activity.RESULT_OK) return

    val uri = data?.data
    if (requestCode == PICK_FILE_CODE
      && uri != null
    ) {
      vm.uriToUpload.value = uri
    }
  }
}