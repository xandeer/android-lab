package xandeer.android.lab.main

import xandeer.android.lab.coroutine.CoroutineActivity
import xandeer.android.lab.dbx.DropboxActivity
import xandeer.android.lab.pdfpreview.PdfPreviewActivity
import xandeer.android.lab.pdftopng.PdfToPngActivity
import xandeer.android.lab.work.WorkActivity

object Activities {
  private val models = arrayOf(
    Model("Main", MainActivity::class.java.name),
    Model("Pdf To Png", PdfToPngActivity::class.java.name),
    Model("Work Manager", WorkActivity::class.java.name),
    Model("Dropbox", DropboxActivity::class.java.name),
    Model("Pdf Previewer", PdfPreviewActivity::class.java.name),
    Model("Coroutine", CoroutineActivity::class.java.name)
  )

  val size = models.size

  val developing = get(size - 1)

  fun get(index: Int) = try {
    models[index]
  } catch (e: ArrayIndexOutOfBoundsException) {
    null
  }
}