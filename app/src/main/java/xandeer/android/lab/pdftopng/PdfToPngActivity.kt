package xandeer.android.lab.pdftopng

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.activity_pdf_to_png.*
import timber.log.Timber
import xandeer.android.lab.AbstractActivity
import xandeer.android.lab.App
import xandeer.android.lab.R
import xandeer.android.lab.utils.PermissionUtil
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt
import kotlin.math.sqrt

class PdfToPngActivity : AbstractActivity() {
  companion object {
    private const val PICK_PDF_FILE_CODE = 0
    // https://android.googlesource.com/platform/frameworks/base/+/master/core/java/android/view/DisplayListCanvas.java#43
    private const val MAX_BITMAP_SIZE = 100 * 1024 * 1024

    private val GALLERY_PATH = "${Environment.DIRECTORY_PICTURES}/xandeer-lab"
  }

  private lateinit var model: ViewModel
  private var isTransitionEnded = false

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_pdf_to_png)

    model = ViewModelProviders.of(this)[ViewModel::class.java].apply {
      setApp(application as App)
    }

    subscribeToModel(model)
    setClickCallbacks(model)
  }

  private fun subscribeToModel(model: ViewModel) {
    model.getScale().observe(this, Observer {
      updatePreview()
      decreaseScaleButton.isEnabled = it != 1
    })
    model.getPdfUri().observe(this, Observer {
      model.scale = 0
      if (it == null) {
        setScaleButtons(false)
      } else {
        setScaleButtons(true)
      }
      model.scale = 1
    })
  }

  private fun setScaleButtons(enabled: Boolean) {
    val buttons = arrayOf(increaseScaleButton, decreaseScaleButton)
    buttons.forEach { it.isEnabled = enabled }
  }

  private fun setClickCallbacks(model: ViewModel) {
    choosePdfButton.setOnClickListener {
      pickPdfFile()
    }

    increaseScaleButton.setOnClickListener {
      model.increaseScale()
    }

    decreaseScaleButton.setOnClickListener {
      model.decreaseScale()
    }
  }

  private fun updatePreview() {
    val uri = model.pdfUri
    val scale = model.scale

    if (isTransitionEnded
      && uri != null
      && scale != 0
    ) {
      updatePreview(uri, scale)
    }
  }

  private fun updatePreview(uri: Uri, scale: Int) {
    val descriptor = try {
      val takeFlags = intent.flags and
          (Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
      contentResolver.takePersistableUriPermission(uri, takeFlags)
      contentResolver.openFileDescriptor(uri, "r")
    } catch (e: SecurityException) {
      Timber.e(e)
      null
    }

    if (descriptor != null) {
      val pdfRenderer = PdfRenderer(descriptor)
      val page = pdfRenderer.openPage(0)

      val bitmap = createBitmap(page.width, page.height, scale)
      pdfPathView.text = uri.lastPathSegment?.substringAfterLast("/")
      page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

      previewView.setImageBitmap(bitmap)
      printBitmapSize(bitmap)
      scaledPngValueView.text = getString(R.string.pdf_to_png_scaled_value, scale)

      saveToGallery(bitmap)

      page.close()
      pdfRenderer.close()
    }
  }

  private fun createBitmap(originalWidth: Int, originalHeight: Int, scale: Int): Bitmap {
    var width = originalWidth * scale
    var height = originalHeight * scale

    /**
     * Each pixel is stored on 4 bytes.
     * @see Bitmap.Config.ARGB_8888
     */
    val zoom = getZoom(width * height * 4)

    if (zoom < 1) {
      width = (width * zoom).roundToInt()
      height = (height * zoom).roundToInt()
    }

    return Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
  }

  private fun getZoom(originalSize: Int): Float {
    var ret = 1f
    while (originalSize * sqrt(ret) > MAX_BITMAP_SIZE) {
      ret -= 0.01f
    }

    Timber.i("zoom: $ret")
    return ret
  }

  private fun printBitmapSize(bitmap: Bitmap) {
    val kb = bitmap.byteCount / 1024
    val mb = kb / 1024
    Timber.i("bitmap size: ${if (mb > 0) "$mb Mb" else "$kb Kb"}")
  }

  private fun saveToGallery(bitmap: Bitmap) {
    if (!PermissionUtil.requestExternalStorageIfNeed(this)) {
      val dir = File(Environment.getExternalStorageDirectory(), GALLERY_PATH)
      val timestamp = SimpleDateFormat("yyMMdd_HHmmss", Locale.ENGLISH).format(Date())
      val f = File(dir, "$timestamp.png")

      if (!dir.exists()) {
        dir.mkdir()
      }

      if (!f.exists()) {
        f.createNewFile()
      }
      f.outputStream().use {
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
      }

      Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).also { mediaScanIntent ->
        mediaScanIntent.data = Uri.fromFile(f)
        sendBroadcast(mediaScanIntent)
      }
    }
  }

  private fun pickPdfFile() {
    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
      addCategory(Intent.CATEGORY_OPENABLE)
      type = "application/pdf"
    }

    startActivityForResult(intent, PICK_PDF_FILE_CODE)
  }

  override fun onEnterAnimationComplete() {
    super.onEnterAnimationComplete()
    isTransitionEnded = true

    updatePreview()
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)

    if (resultCode != Activity.RESULT_OK) return

    val uri = data?.data
    if (requestCode == PICK_PDF_FILE_CODE
      && uri != null
      && ::model.isInitialized
    ) {
      model.pdfUri = uri
    }
  }

  override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<out String>,
    grantResults: IntArray
  ) {
    PermissionUtil.requestExternalStorageIfNeed(this)
  }
}
