package id.revan.beritaku.helper

import android.content.Context
import android.net.ConnectivityManager
import javax.inject.Inject

class NetworkHelper @Inject constructor() {
    fun hasNetwork(context: Context): Boolean {
        var isConnected = false
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetworkInfo
        if (activeNetwork != null && activeNetwork.isConnected) {
            isConnected = true
        }
        return isConnected
    }
}