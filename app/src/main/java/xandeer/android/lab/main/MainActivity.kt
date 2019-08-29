package xandeer.android.lab.main

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber
import xandeer.android.lab.AbstractActivity
import xandeer.android.lab.R

class MainActivity : AbstractActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    setRecyclerView()

    if (isTaskRoot) {
      Activities.developing?.let {
        Timber.d("Auto start the developing activity of ${it.name}")
        startActivity(it.clazz)
      }
    }
  }

  private fun setRecyclerView() {
    activitiesView.apply {
      layoutManager = LinearLayoutManager(context)
      addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

      adapter = ActivityAdapter().apply {
        clickListener = object : ActivityAdapter.ItemClickListener {
          override fun onItemClick(className: String) {
            startActivity(className)
          }
        }
      }
    }
  }

  private fun startActivity(className: String) {
    val intent = Intent()
    intent.component = ComponentName(packageName, className)
    startActivity(intent)
  }
}
