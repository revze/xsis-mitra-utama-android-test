package id.revan.beritaku.di

import android.content.Context
import id.revan.beritaku.di.modules.ApiModule
import id.revan.beritaku.di.modules.DatabaseModule

object Injector {
    fun getApp(context: Context): AppComponent =
        DaggerAppComponent.builder().apiModule(ApiModule(context)).databaseModule(
            DatabaseModule(
                context
            )
        ).build()
}