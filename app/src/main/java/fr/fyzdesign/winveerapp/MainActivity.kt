package fr.fyzdesign.winveerapp

import android.Manifest
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import fr.fyzdesign.winveerapp.clients.ChromeApp
import fr.fyzdesign.winveerapp.clients.WebApp
import fr.fyzdesign.winveerapp.managers.NoConnectionManager
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import java.util.concurrent.Executors


class MainActivity : AppCompatActivity() {

    private var jsonNewUserMSG = "{\"content\": null,\"embeds\": [{\"title\": \"Nouvel utilisateur\",\"description\": \"Un nouvel utilisateur a installé l'application!\",\"color\": null}],\"attachments\": []}"
    private var jsonUserLogged = "{\"content\": null,\"embeds\": [{\"title\": \"Utilisateur connecté\",\"description\": \"Un utilisateur s'est connecté\",\"color\": null}],\"attachments\": []}"

    var filePath: ValueCallback<Array<Uri>>? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logFistTime()

    }

    fun startApplication(url: String) {
        if(!NoConnectionManager(this).checkForInternet()) {
            setContentView(R.layout.error_nointernet)
            NoConnectionManager(this).showError()
            return;
        }

        setContentView(R.layout.activity_main)

        checkDownloadPermission()

        val contentView: WebView = findViewById(R.id.WebView)

        contentView.setWebChromeClient(ChromeApp(this))


        contentView.setWebViewClient(WebApp(this))

        contentView.settings.javaScriptEnabled = true

        contentView.settings.allowFileAccess = true
        contentView.settings.domStorageEnabled = true
        contentView.settings.allowContentAccess = true

        contentView.loadUrl(url)
    }

    private fun post(json: String) {
        Executors.newSingleThreadExecutor().execute {
        val client = OkHttpClient()
            
        val body: RequestBody = RequestBody.create("application/json; charset=utf-8".toMediaType(), json)
        val request: Request = Request.Builder()
            .url("")
            .post(body)
            .build()
        val response: Response = client.newCall(request).execute()
        response.close()
    }
    }

    private fun logFistTime(){
        val sharedPref = getSharedPreferences("prefs", MODE_PRIVATE)
        if(!sharedPref.getBoolean("hasAlreadyUsedApp", false)) {
            val editor = sharedPref.edit();
            editor.putBoolean("hasAlreadyUsedApp", true)
            editor.commit()
            post(jsonNewUserMSG)
            setContentView(R.layout.welcome_screen)

            val login: Button = findViewById(R.id.login)
            val signin: Button = findViewById(R.id.signin)
            val withoutLog: TextView = findViewById(R.id.withoutLog)
            withoutLog.setOnClickListener {
                withoutLog.setTextColor(Color.GREEN)
                startApplication("https://winveer.com/")
            }

            login.setOnClickListener {
                signin.setBackgroundColor(Color.GREEN)
                startApplication("https://winveer.com/user/login")
            }

            signin.setOnClickListener {
                signin.setBackgroundColor(Color.GREEN)
                startApplication("https://winveer.com/user/join")
            }

            checkDownloadPermission()
        } else {
            post(jsonUserLogged)
            startApplication("https://winveer.com")
        }
    }

    fun checkDownloadPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this@MainActivity,
                Manifest.permission.READ_EXTERNAL_STORAGE
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
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                100
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_CANCELED) {
            filePath?.onReceiveValue(null)
            return
        } else if (resultCode == RESULT_OK) {
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