package id.revan.beritaku.di.modules

import dagger.Module
import dagger.Provides
import id.revan.beritaku.ui.base.BaseViewModelFactory
import id.revan.beritaku.ui.latestnews.LatestNewsViewModel
import id.revan.beritaku.ui.searchnews.SearchNewsViewModel
import javax.inject.Singleton

@Module
class ViewModelFactoryModule {
    @Singleton
    @Provides
    fun provideLatestNewsViewModelFactory(viewModel: LatestNewsViewModel): BaseViewModelFactory<LatestNewsViewModel> {
        return BaseViewModelFactory { viewModel }
    }

    @Singleton
    @Provides
    fun provideSearchNewsViewModelFactory(viewModel: SearchNewsViewModel): BaseViewModelFactory<SearchNewsViewModel> {
        return BaseViewModelFactory { viewModel }
    }
}