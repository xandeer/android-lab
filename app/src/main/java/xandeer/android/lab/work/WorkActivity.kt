package xandeer.android.lab.work

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.work.Data
import androidx.work.WorkInfo
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_work.*
import timber.log.Timber
import xandeer.android.lab.AbstractActivity
import xandeer.android.lab.R
import xandeer.android.lab.utils.PermissionUtil
import xandeer.android.lab.work.works.Utils

/**
 * Learn from https://codelabs.developers.google.com/codelabs/android-workmanager-kt/#0
 */
class WorkActivity : AbstractActivity() {
  companion object {
    private const val PICK_PICTURE_CODE = 2
  }

  private lateinit var vm: ViewModel

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_work)

    vm = ViewModelProviders.of(this).get(ViewModel::class.java)

    subscribe(vm)
    setClickCallbacks()
  }

  private fun subscribe(vm: ViewModel) {
    vm.pictureUri.observe(this, Observer {
      Timber.d("Observed picture uri: $it")
      it?.let {
        goBtn.visibility = VISIBLE
        Glide.with(this).load(it).into(imageView)
      }
    })

    vm.savedWorkIn.observe(this, Observer {
      if (it == null || it.isEmpty()) {
        return@Observer
      }

      val workInfo = it[0]
      Timber.d("Observed work info: $workInfo")
      if (workInfo.state.isFinished) {
        updateBlurredUri(vm, workInfo.outputData)
        showWorkFinished()
        Utils.makeStatusNotification("Finished", applicationContext)
      } else {
        showWorkInProgress()
      }

      updateGoButton(workInfo.state, vm.pictureUri.value)
    })
  }

  private fun updateBlurredUri(vm: ViewModel, data: Data) {
    val uriStr = data.getString(Constants.PICTURE_URI_KEY)
    if (!TextUtils.isEmpty(uriStr)) {
      val uri = Uri.parse(uriStr)
      vm.blurredUri = uri
      viewBlurredBtn.visibility = VISIBLE
    }
  }

  private fun showWorkInProgress() {
    progressBar.visibility = VISIBLE
    cancelBtn.visibility = VISIBLE
    viewBlurredBtn.visibility = GONE
  }

  private fun showWorkFinished() {
    progressBar.visibility = GONE
    cancelBtn.visibility = GONE
  }

  private fun updateGoButton(state: WorkInfo.State, uri: Uri?) {
    goBtn.visibility = when (state) {
      WorkInfo.State.BLOCKED,
      WorkInfo.State.ENQUEUED,
      WorkInfo.State.RUNNING -> GONE
      else -> if (uri == null) GONE else VISIBLE
    }
  }

  private fun setClickCallbacks() {
    pickPictureBtn.setOnClickListener { pickPicture() }

    cancelBtn.setOnClickListener { vm.cancelWork() }
    goBtn.setOnClickListener { startBlur() }
    viewBlurredBtn.setOnClickListener { view(vm.blurredUri) }
  }

  private fun pickPicture() {
    val intent = Intent(
      Intent.ACTION_PICK,
      MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    )
    startActivityForResult(intent, PICK_PICTURE_CODE)
  }

  private fun startBlur() {
    if (!PermissionUtil.requestExternalStorageIfNeed(this)) {
      vm.applyBlur(getBlurLevel())
    }
  }

  private fun getBlurLevel(): Int {
    return when (lvs.checkedRadioButtonId) {
      R.id.lv1 -> 1
      R.id.lv2 -> 2
      R.id.lv3 -> 3
      else -> 1
    }
  }

  private fun view(uri: Uri?) {
    if (uri != null) {
      val intent = Intent(Intent.ACTION_VIEW, uri)
      intent.resolveActivity(packageManager)?.let { startActivity(intent) }
    }
  }

  override fun onActivityResult(
    requestCode: Int, resultCode: Int, data: Intent?
  ) {
    super.onActivityResult(requestCode, resultCode, data)

    if (resultCode == Activity.RESULT_OK) {
      when (requestCode) {
        PICK_PICTURE_CODE -> {
          data?.let(::handlePickPictureResult)
        }
        else -> {
          Timber.d("Unknown request code.")
        }
      }
    } else {
      Timber.e("Unexpected Result code $resultCode")
    }
  }

  private fun handlePickPictureResult(data: Intent) {
    val clipData = data.clipData
    val uri = when {
      clipData != null -> clipData.getItemAt(0).uri
      data.data != null -> data.data
      else -> null
    }

    if (uri == null) {
      Timber.e("Invalid input picture url.")
      return
    }

    vm.setPictureUri(uri)
  }

  override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<out String>,
    grantResults: IntArray
  ) {
    startBlur()
  }
}
