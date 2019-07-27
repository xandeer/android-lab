package xandeer.android.lab

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import xandeer.android.lab.pdftopng.PdfToPngActivity

class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    startCurrentTestActivity()
  }

  private fun startCurrentTestActivity() {
    val intent = Intent(this, PdfToPngActivity::class.java)
    startActivity(intent)
  }
}
