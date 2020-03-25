package xandeer.android.lab.dbx

import android.view.Gravity.CENTER
import android.view.Gravity.CENTER_VERTICAL
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import xandeer.android.lab.R
import xandeer.android.lab.utils.*
import java.io.File

class FileAdapter(private val files: Array<File>) :
  RecyclerView.Adapter<FileAdapter.FileHolder>() {
  override fun onCreateViewHolder(
    parent: ViewGroup,
    viewType: Int
  ): FileHolder {
    return FileHolder(FrameLayout(parent.context).apply {
      layoutParams = ViewGroup.LayoutParams(matchParent, dp(48))
      horizontalPadding = dp(16)
      verticalPadding = dp(4)

      addView(TextView(context).apply {
        horizontalPadding = dp(8)
        gravity = CENTER_VERTICAL
      })

      addView(ProgressBar(context).apply {
        layoutParams = FrameLayout.LayoutParams(dp(24), dp(24)).apply {
          gravity = CENTER
        }
      })
    })
  }

  override fun onBindViewHolder(holder: FileHolder, position: Int) {
    holder.bind(files[position])
  }

  override fun getItemCount(): Int {
    return files.size
  }

  inner class FileHolder(private val view: ViewGroup) :
    RecyclerView.ViewHolder(view) {
    fun bind(file: File) {
      view.apply {
        val path = Local.getPath(file)
        val isShownProgress = Local.isDownloading(path) ||
            Local.isUploading(path)
        setOnClickListener {
          val vm = getVm(DropboxVM::class.java)
          if (file.isDirectory) {
            vm.goto(path)
          } else {
            vm.downloadByWorker(path)
          }
        }

        (getChildAt(0) as TextView).apply {
          text = file.name
          backgroundResource =
            if (file.isDirectory) R.color.folder_bg
            else R.color.file_bg
        }

        (getChildAt(1) as ProgressBar).apply {
          visibility = if (isShownProgress) VISIBLE else GONE
        }
      }
    }
  }
}