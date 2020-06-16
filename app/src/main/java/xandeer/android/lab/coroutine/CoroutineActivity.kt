package xandeer.android.lab.coroutine

import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.android.synthetic.main.activity_coroutines.*
import xandeer.android.lab.AbstractActivity
import xandeer.android.lab.R
import xandeer.android.lab.coroutine.CoroutineViewModel.Companion.FACTORY
import xandeer.android.lab.utils.observe
import java.lang.RuntimeException

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

    observe(viewModel.title) {
      title_view.text = it
      it?.let { logEvent("title", it) }
    }
    observe(viewModel.taps) {
      taps.text = it
      logEvent("taps", it)
    }
    observe(viewModel.spinner) {
      spinner.visibility = if (it) VISIBLE else GONE
    }

    observe(viewModel.snackbar) {
      Snackbar.make(rootLayout, it, Snackbar.LENGTH_SHORT).show()
      viewModel.onSnackbarShown()
      logEvent("snack", it)
      // Report a non-fatal exception
      FirebaseCrashlytics.getInstance().recordException(RuntimeException("Test Crash"))
    }
  }

  private fun logEvent(name: String, param: Any) {
    val bundle = Bundle()
    bundle.putString("param", param.toString())
    FirebaseAnalytics.getInstance(this)
      .logEvent(name, bundle)
  }
}

