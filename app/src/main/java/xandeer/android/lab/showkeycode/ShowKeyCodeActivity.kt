package xandeer.android.lab.showkeycode

import android.os.Bundle
import android.view.KeyEvent
import kotlinx.android.synthetic.main.activity_show_key_code.*
import timber.log.Timber
import xandeer.android.lab.AbstractActivity
import xandeer.android.lab.R

class ShowKeyCodeActivity : AbstractActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_show_key_code)
    setSupportActionBar(findViewById(R.id.toolbar))
  }

  override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
    event?.let {
      if (it.action == KeyEvent.ACTION_UP) {
        add(it.keyCode)
      }
    }
    return super.dispatchKeyEvent(event)
  }

  private fun add(keyCode: Int) {
    val key = KeyEvent.keyCodeToString(keyCode)

    Timber.i("Key: $key down.")
    text_view.append("$key\n")
  }
}