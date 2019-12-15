package id.revan.beritaku.di

import android.content.Context
import id.revan.beritaku.di.modules.ApiModule

object Injector {
    fun getApp(context: Context): AppComponent =
        DaggerAppComponent.builder().apiModule(ApiModule(context)).build()
}