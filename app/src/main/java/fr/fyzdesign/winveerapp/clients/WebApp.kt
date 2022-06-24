package fr.fyzdesign.winveerapp.clients

import android.content.Intent
import android.net.Uri
import android.webkit.WebView
import android.webkit.WebViewClient
import fr.fyzdesign.winveerapp.MainActivity


class WebApp(private val winActivity: MainActivity) : WebViewClient() {

    override fun onReceivedError(view: WebView, errorCode: Int, description: String, failingUrl: String) {
        winActivity.startApplication("https://winveer.com")
    }

    override fun shouldOverrideUrlLoading(view: WebView, url: String?): Boolean {
        return if (url != null && !url.contains("winveer.com")) {
            view.context.startActivity(
                Intent(Intent.ACTION_VIEW, Uri.parse(url))
            )
            true
        } else {
            false
        }
    }

}