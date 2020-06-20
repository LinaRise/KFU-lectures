package com.nikak.linadom.kfulectures

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import android.widget.Toast

class ConnectivityReceiver : BroadcastReceiver() {


    override fun onReceive(context: Context?, intent: Intent?) {
        val connectivityManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val isConnected: Boolean = isNetworkAvailable(context)
        if (!isConnected) {
            Log.i("Flag № 1", "No internet connection")
            Toast.makeText(context, context.getString(R.string.no_internet_connection), Toast.LENGTH_LONG).show()
        }

    }

    //apply, also
//The Kotlin standard library contains several functions whose sole purpose is to execute a block of code
// within the context of an object.
// When you call such a function on an object with a lambda expression provided,
// it forms a temporary scope. In this scope, you can access the object without its name.
// Such functions are called scope functions.
    //проверка есть ли сам интернет
    private fun isNetworkAvailable(context: Context): Boolean {
        var result = false
        (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).apply {
            //для устройств с версией выше Marshmallow
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                result = isCapableNetwork(this, this.activeNetwork)
            } else {
                val networkInfos = this.allNetworks
                for (tempNetworkInfo in networkInfos) {
                    if (isCapableNetwork(this, tempNetworkInfo))
                        result = true
                }
            }
        }

        return result
    }

    //что использует устройство для связи
    private fun isCapableNetwork(cm: ConnectivityManager, network: Network?): Boolean {
        cm.getNetworkCapabilities(network)?.also {
            //используется wifi
            when {
                it.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> return true
                //используется моб. интернет
                it.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> return true
                //на случай, если пользователь пользуется VPN
                it.hasTransport(NetworkCapabilities.TRANSPORT_VPN) -> return true
            }
        }
        return false
    }


}