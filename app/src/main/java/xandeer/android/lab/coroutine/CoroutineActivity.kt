package xandeer.android.lab.coroutine

import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_coroutines.*
import xandeer.android.lab.AbstractActivity
import xandeer.android.lab.R
import xandeer.android.lab.coroutine.CoroutineViewModel.Companion.FACTORY
import xandeer.android.lab.utils.observe

class CoroutineActivity : AbstractActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_coroutines)

    val database = getDatabase(this)
    val repository = TitleRepository(getNetworkService(), database.titleDao)

    val viewModel = ViewModelProviders
      .of(this, FACTORY(repository))
      .get(CoroutineViewModel::class.java)

    rootLayout.setOnClickListener { viewModel.onMainViewClicked() }

    observe(viewModel.title) { title_view.text = it }
    observe(viewModel.taps) { taps.text = it }
    observe(viewModel.spinner) {
      spinner.visibility = if (it) VISIBLE else GONE
    }

    observe(viewModel.snackbar) {
      Snackbar.make(rootLayout, it, Snackbar.LENGTH_SHORT).show()
      viewModel.onSnackbarShown()
    }
  }
}

