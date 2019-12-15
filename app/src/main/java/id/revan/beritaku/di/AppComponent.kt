package id.revan.beritaku.di

import dagger.Component
import id.revan.beritaku.di.modules.ApiModule
import id.revan.beritaku.di.modules.RepositoryModule
import id.revan.beritaku.di.modules.ViewModelFactoryModule
import id.revan.beritaku.ui.latestnews.LatestNewsFragment
import id.revan.beritaku.ui.searchnews.SearchNewsActivity
import javax.inject.Singleton

@Singleton
@Component(modules = [ApiModule::class, RepositoryModule::class, ViewModelFactoryModule::class])
interface AppComponent {
    fun inject(fragment: LatestNewsFragment)

    fun inject(activity: SearchNewsActivity)
}