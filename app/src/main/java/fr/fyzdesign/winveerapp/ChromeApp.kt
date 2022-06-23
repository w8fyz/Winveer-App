package fr.fyzdesign.winveerapp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView

class ChromeApp (val winActivity : MainActivity) : WebChromeClient() {

    override fun onShowFileChooser(
        webView: WebView?,
        filePathCallback: ValueCallback<Array<Uri>>?,
        fileChooserParams: FileChooserParams?
    ): Boolean {
        winActivity.checkDownloadPermission()
        winActivity.filePath = filePathCallback

        val contentIntent = Intent(Intent.ACTION_GET_CONTENT)
        contentIntent.type = "*/*"
        contentIntent.addCategory(Intent.CATEGORY_OPENABLE)

        winActivity.startActivityForResult(contentIntent, 1)
        return true
    }





}