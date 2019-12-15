package id.revan.beritaku.di.modules

import dagger.Module
import dagger.Provides
import id.revan.beritaku.data.repository.ArticleRepository
import id.revan.beritaku.data.repository.ArticleRepositoryImpl
import javax.inject.Singleton

@Module
class RepositoryModule {
    @Singleton
    @Provides
    fun provideArticleRepository(repository: ArticleRepositoryImpl): ArticleRepository {
        return repository
    }
}