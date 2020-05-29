package xandeer.android.lab.pdfpreview

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebView.setWebContentsDebuggingEnabled
import kotlinx.android.synthetic.main.activity_pdf_preview.*
import xandeer.android.lab.AbstractActivity
import xandeer.android.lab.R

class PdfPreviewActivity : AbstractActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_pdf_preview)

    initView()
  }

  private fun initView() {
    initWebView()
    previewTest()
  }

  @SuppressLint("SetJavaScriptEnabled")
  private fun initWebView() {
    web_view.apply {
      setWebContentsDebuggingEnabled(true)

      isHorizontalScrollBarEnabled = false
      isVerticalScrollBarEnabled = false
    }

    web_view.settings?.apply {
      textZoom = 100
      javaScriptEnabled = true
      domStorageEnabled = true
      databaseEnabled = true
      setAppCacheEnabled(true)
      setAppCachePath(cacheDir.absolutePath)
      setSupportZoom(true)
      builtInZoomControls = true
      displayZoomControls = false
//      userAgentString = ""
      allowFileAccess = true
      allowUniversalAccessFromFileURLs = true
    }
  }

  private fun previewTest() {
    web_view.loadUrl("file:///android_asset/pdf-viewer/dist/index.html")
  }
}
