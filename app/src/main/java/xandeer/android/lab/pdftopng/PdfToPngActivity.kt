package xandeer.android.lab.pdftopng

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.activity_pdf_to_png.*
import xandeer.android.lab.App
import xandeer.android.lab.R
import xandeer.android.lab.utils.PermissionUtil


class PdfToPngActivity : AppCompatActivity() {
  companion object {
    private const val PICK_PDF_FILE_CODE = 0
  }

  private lateinit var model: ViewModel

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
      val uri = model.pdfUri
      if (uri != null && it != 0) {
        updatePreview(uri, it)
      }
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

  private fun updatePreview(uri: Uri, scale: Int) {
    val takeFlags = intent.flags and
        (Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
    contentResolver.takePersistableUriPermission(uri, takeFlags)

    pdfPathView.text = uri.lastPathSegment?.substringAfterLast("/")
    val descriptor = contentResolver.openFileDescriptor(uri, "r")
    if (descriptor != null) {
      val pdfRenderer = PdfRenderer(descriptor)
      val page = pdfRenderer.openPage(0)
      val width = page.width * scale
      val height = page.height * scale

      scaledPngValueView.text = getString(R.string.pdf_to_png_scaled_value, scale, width, height)

      val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
      page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

      previewView.setImageBitmap(bitmap)

      page.close()
      pdfRenderer.close()
    }
  }

  private fun pickPdfFile() {
    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
      addCategory(Intent.CATEGORY_OPENABLE)
      type = "application/pdf"
    }

    startActivityForResult(intent, PICK_PDF_FILE_CODE)
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)

    if (resultCode != Activity.RESULT_OK) return

    val uri = data?.data
    if (requestCode == PICK_PDF_FILE_CODE
        && uri != null
        && ::model.isInitialized) {
      model.pdfUri = uri
    }
  }

  override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
    PermissionUtil.requestWriteExternalStorageIfNeed(this)
  }
}
