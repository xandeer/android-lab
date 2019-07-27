package xandeer.android.lab.pdftopng

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.activity_pdf_to_png.*
import xandeer.android.lab.R


class PdfToPngActivity : AppCompatActivity() {
  companion object {
    private const val PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 0
    private const val SHARED_PREFERENCES_NAME = "pdf_to_png"
    private const val PICK_PDF_FILE_CODE = 0
  }

  private lateinit var model: ViewModel

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_pdf_to_png)

    initModel()

    setObserve()
    setCallback()
  }

  private fun initModel() {
    model = ViewModelProviders.of(this)[ViewModel::class.java]
    model.setSharedPreferences(getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE))
  }

  private fun setObserve() {
    model.getScale().observe(this, Observer<Int> {
      scaledPngValueView.text = getString(R.string.pdf_to_png_scaled_value, it)
      updateImagePreview()
    })

    model.getPdfUri().observe(this, Observer {
      if (needRequestPermission()) {
        requestPermission()
      } else {
        updateImagePreview()
      }
    })
  }

  private fun updateImagePreview() {
    val uri = model.getPdfUri().value!!
    val scale = model.getScale().value!!

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

      val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
      page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

      previewView.setImageBitmap(bitmap)

      page.close()
      pdfRenderer.close()
    }
  }

  private fun setCallback() {
    initScaleButtons()
    initPathButton()
  }

  private fun initScaleButtons() {
    increaseScaleButton.setOnClickListener {
      model.increaseScale()
    }
    decreaseScaleButton.setOnClickListener {
      model.decreaseScale()
    }
  }

  private fun initPathButton() {
    choosePdfButton.setOnClickListener {
      if (needRequestPermission()) {
        requestPermission()
      } else {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
          addCategory(Intent.CATEGORY_OPENABLE)
          type = "application/pdf"
        }

        startActivityForResult(intent, PICK_PDF_FILE_CODE)
      }
    }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)

    if (resultCode != Activity.RESULT_OK) return

    val uri = data?.data
    if (requestCode == PICK_PDF_FILE_CODE && uri != null) {
      model.setPdfPath(uri)
    }
  }

  private fun needRequestPermission(): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
        && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
  }

  private fun requestPermission() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
          PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE)
    }
  }

  override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
    if (needRequestPermission()) {
      requestPermission()
    }
  }
}
