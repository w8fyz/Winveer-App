package fr.fyzdesign.winveerapp

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

class MainActivity : AppCompatActivity() {

    var filePath: ValueCallback<Array<Uri>>? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        checkDownloadPermission()

        val contentView: WebView = findViewById(R.id.WebView)

        contentView.setWebChromeClient(ChromeApp(this))


        contentView.setWebViewClient(WebViewClient())

        contentView.settings.javaScriptEnabled = true

        contentView.settings.allowFileAccess = true
        contentView.settings.domStorageEnabled = true
        contentView.settings.allowContentAccess = true

        contentView.loadUrl("https://winveer.com")

    }

    fun checkDownloadPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this@MainActivity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        ) {
            Toast.makeText(
                this@MainActivity,
                "Sans cette permission, il vous sera impossible d'ajouter des images à vos conversations.",
                Toast.LENGTH_LONG
            ).show()
        } else {
            ActivityCompat.requestPermissions(
                this@MainActivity,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                100
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_CANCELED) {
            filePath?.onReceiveValue(null)
            return
        } else if (resultCode == Activity.RESULT_OK) {
            if (filePath == null) return

            filePath!!.onReceiveValue(
                WebChromeClient.FileChooserParams.parseResult(resultCode, data)
            )
                    filePath = null
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {

        val myWebView: WebView = findViewById(R.id.WebView)
        if (keyCode == KeyEvent.KEYCODE_BACK && myWebView.canGoBack()) {
            myWebView.goBack()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }


}