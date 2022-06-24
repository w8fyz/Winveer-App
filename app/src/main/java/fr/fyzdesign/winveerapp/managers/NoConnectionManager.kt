package fr.fyzdesign.winveerapp.managers

import android.app.Activity
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import fr.fyzdesign.winveerapp.MainActivity


class NoConnectionManager(val winActivity: MainActivity) {

    fun showError() {
        val popUp: AlertDialog.Builder = AlertDialog.Builder(winActivity)

        popUp.setTitle("Erreur")
        popUp.setMessage("Connection échouée aux serveurs de Winveer.")
        popUp.setPositiveButton("Reessayer") { _, _ -> tryAgainConnection() }
        popUp.setOnCancelListener {
            tryAgainConnection()
        }
        popUp.show()
    }

    private fun tryAgainConnection() {
        winActivity.startApplication("https://winveer.com")
    }

    fun checkForInternet(): Boolean {
        val connectivityManager = winActivity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                else -> false
            }
        } else {
            @Suppress("DEPRECATION") val networkInfo =
                connectivityManager.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION")
            return networkInfo.isConnected
        }
    }

}