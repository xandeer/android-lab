package xandeer.android.lab.main

import xandeer.android.lab.pdftopng.PdfToPngActivity
import xandeer.android.lab.work.WorkActivity

object Activities {
  private const val DEVELOPING_INDEX = 2

  private val models = arrayOf(
    Model("Main", MainActivity::class.java.name),
    Model("Pdf To Png", PdfToPngActivity::class.java.name),
    Model("Work Manager", WorkActivity::class.java.name)
  )

  val size = models.size

  val developing = get(DEVELOPING_INDEX)

  fun get(index: Int) = try {
    models[index]
  } catch (e: ArrayIndexOutOfBoundsException) {
    null
  }
}