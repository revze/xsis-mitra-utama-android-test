package id.revan.beritaku.di.modules

import android.content.Context
import dagger.Module
import dagger.Provides
import id.revan.beritaku.data.services.ApiService
import id.revan.beritaku.helper.NetworkHelper
import id.revan.beritaku.helper.constants.Endpoint
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class ApiModule(private val context: Context) {

    @Singleton
    @Provides
    fun provideOkHttpClient(networkHelper: NetworkHelper): OkHttpClient {
        val cacheSize = (5 * 1024 * 1024).toLong()
        val myCache = Cache(context.cacheDir, cacheSize)

        return OkHttpClient.Builder()
            .cache(myCache)
            .addInterceptor {
                var request = it.request()
                request = if (networkHelper.hasNetwork(context)) {
                    /**
                     * If there is Internet, get the cache that was stored 5 seconds ago.
                     * If the cache is older than 5 seconds, then discard it.
                     */

                    request.newBuilder().header("Cache-Control", "public, max-age=" + 5).build()
                } else {
                    /**
                     * If there is no Internet, get the cache that was stored 2 hours ago.
                     * If the cache is older than 2 hours, then discard it.
                     */

                    request.newBuilder()
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + 60 * 60 * 2)
                        .build()
                }
                it.proceed(request)
            }
            .build()
    }

    @Singleton
    @Provides
    fun provideApiService(okHttpClient: OkHttpClient): ApiService {
        return Retrofit.Builder().baseUrl(Endpoint.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create()).build()
            .create(ApiService::class.java)

    }
}