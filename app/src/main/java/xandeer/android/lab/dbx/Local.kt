package xandeer.android.lab.dbx

import android.net.Uri
import xandeer.android.lab.App
import xandeer.android.lab.App.Companion.context
import xandeer.android.lab.utils.fileName
import java.io.File
import java.util.*

object Local {
  private const val ROOT = "dropbox"
  private const val REV = ".rev"
  private const val IS_DOWNLOADING = ".downloading"
  private const val UPLOAD = ".upload"
  const val CURSOR = ".cursor"

  private val root = File(App.context.filesDir, ROOT)

  fun init() {
    if (!root.exists()) {
      root.mkdirs()
    }
  }

  fun listFolder(path: String): Array<File>? {
    val r = arrayListOf<File>()

    val f = getFile(path)
    return if (f.exists() && f.isDirectory) {
      f.listFiles { file -> file.isDirectory }?.let {
        addAndSort(r, it)
      }
      f.listFiles { file ->
        file.isFile
      }?.let {
        addAndSort(r, it)
      }
      r.toTypedArray()
    } else null
  }

  private fun addAndSort(src: ArrayList<File>, files: Array<File>) {
    files.sortedBy { it.nameWithoutExtension.toLowerCase(Locale.getDefault()) }
      .let {
        src.addAll(it.filter { f -> !f.isHidden })
      }
  }

  fun getPath(file: File) =
    file.absolutePath.substringAfter(root.absolutePath)

  fun getFile(path: String) =
    File(root, path)

  fun createFolder(path: String) {
    val f = getFile(path)
    if (!f.exists()) f.mkdirs()
  }

  fun addFile(parent: String, uri: Uri) {
    val name = uri.fileName
    val path = "$parent/$name"
    val file = getFile(path)

    context.contentResolver.openInputStream(uri)?.use {
      if (!file.exists()) file.createNewFile()
      file.writeBytes(it.readBytes())
      waitToUpload(path)
    }
  }

  private fun waitToUpload(path: String) {
    val f = getUploadFile(path)
    f.parentFile?.mkdirs()

    if (!f.exists()) f.createNewFile()
  }

  fun uploaded(path: String) {
    getUploadFile(path).delete()
  }

  fun isUploading(path: String): Boolean {
    return getUploadFile(path).exists()
  }

  private fun getUploadFile(path: String): File {
    val f = getFile(path)
    return File(f.parent, "$UPLOAD/${f.name}")
  }

  fun getUploadFolder(path: String): File {
    return File(getFile(path), UPLOAD)
  }

  fun getUploadFiles(path: String): Array<File>? {
    return getUploadFolder(path).listFiles()?.map {
      File(getFile(path), it.name)
    }?.toTypedArray()
  }

  fun delete(path: String) {
    val f = getFile(path)
    if (f.isDirectory) {
      f.deleteRecursively()
    } else {
      f.delete()
      getRevFile(path).delete()
    }
  }

  fun saveCursor(path: String, cursor: String) {
    val f = getCursorFile(path)
    if (!f.exists()) f.createNewFile()
    f.writeText(cursor)
  }

  private fun getCursorFile(path: String) =
    getFile("$path/$CURSOR")

  fun getCursor(path: String): String {
    val f = getCursorFile(path)
    return if (f.exists()) f.readText() else ""
  }

  fun saveRev(path: String, rev: String) {
    val f = getRevFile(path)
    if (!f.exists()) f.createNewFile()
    f.writeText(rev)
  }

  fun getRev(path: String): String {
    val f = getRevFile(path)
    return if (f.exists()) f.readText() else ""
  }

  private fun getRevFile(path: String): File {
    val f = getFile(path)
    return File(f.parent, "$REV-${f.name}")
  }

  fun startDownloading(path: String) {
    val f = getDownloadingFile(path)
    if (!f.exists()) f.createNewFile()
  }

  fun quitDownloading(path: String) {
    getDownloadingFile(path).delete()
  }

  fun isDownloading(path: String): Boolean {
    return getDownloadingFile(path).exists()
  }

  private fun getDownloadingFile(path: String): File {
    val f = getFile(path)
    return File(f.parent, "$IS_DOWNLOADING-${f.name}")
  }
}