package xandeer.android.lab.dbx

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.FileObserver
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.WorkInfo
import com.dropbox.core.android.Auth
import com.dropbox.core.v2.users.FullAccount
import kotlinx.android.synthetic.main.dropbox_activity.*
import timber.log.Timber
import xandeer.android.lab.AbstractActivity
import xandeer.android.lab.R
import xandeer.android.lab.dbx.Local.CURSOR
import xandeer.android.lab.utils.getVm
import xandeer.android.lab.utils.observe
import java.io.File

class DropboxActivity : AbstractActivity() {
  companion object {
    private const val PICK_FILE_CODE = 0
  }

  private lateinit var vm: DropboxVM

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.dropbox_activity)

    Local.init()
    vm = getVm(DropboxVM::class.java)

    initViews()
    subscribeVm()
    setCallbacks()
  }

  private fun initViews() {
    files_view.layoutManager = LinearLayoutManager(this)
  }

  private fun subscribeVm() {
    observe(vm.token, ::updateBy)
    observe(vm.account, ::updateBy)
    observe(vm.uriToUpload, ::updateBy)
    observe(vm.files, ::updateBy)
    observe(vm.workInfos, ::updateBy)
    observe(vm.folderPath, ::updateByPath)
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
      vm.goto("")
    }
  }

  private fun updateBy(account: FullAccount) {
    name.text = account.name.displayName
    email.text = account.email
  }

  private fun updateBy(uri: Uri) {
    Local.addFile(vm.folderPath.value!!, uri)
  }

  private fun updateBy(files: Array<File>) {
    files_view.adapter = FileAdapter(files)
  }

  private fun updateBy(workInfos: List<WorkInfo>) {
    Timber.d(workInfos.toString())
    files_view.adapter?.notifyDataSetChanged()
  }

  private var fileObserver: FileObserver? = null
  private fun updateByPath(path: String) {
    path_view.text = if (path == "") "/" else path
    observeFile(path)
    observeUpload(path)
    vm.pullFolder(path)
  }

  private fun observeFile(p: String) {
    fileObserver?.stopWatching()
    fileObserver = object : FileObserver(
      Local.getFile(p),
      CLOSE_WRITE.or(MOVED_TO).or(MOVED_FROM).or(DELETE).or(CREATE)
    ) {
      override fun onEvent(event: Int, path: String?) {
        if (path != CURSOR) {
          runOnUiThread {
            if (p == vm.folderPath.value) {
              vm.listFolder(p)
            }
          }
        }
      }
    }
    fileObserver?.startWatching()
    vm.listFolder(p)
  }

  var uploadObserver: FileObserver? = null
  private fun observeUpload(p: String) {
    uploadObserver?.stopWatching()
    uploadObserver = object : FileObserver(
      Local.getUploadFolder(p),
      CREATE or CLOSE_WRITE
    ) {
      override fun onEvent(event: Int, path: String?) {
        runOnUiThread {
          if (p == vm.folderPath.value) {
            vm.upload(Local.getUploadFiles(p))
          }
        }
      }
    }
    uploadObserver?.startWatching()
    vm.upload(Local.getUploadFiles(p))
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

  override fun onBackPressed() {
    if (vm.folderPath.value == "") {
      super.onBackPressed()
    } else {
      vm.goto(vm.folderPath.value!!.substringBeforeLast("/"))
    }
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

  override fun onDestroy() {
    fileObserver?.stopWatching()
    uploadObserver?.stopWatching()
    super.onDestroy()
  }
}