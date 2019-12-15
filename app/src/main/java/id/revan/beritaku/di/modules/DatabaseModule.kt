package id.revan.beritaku.di.modules

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import id.revan.beritaku.data.db.AppDatabase
import id.revan.beritaku.data.db.dao.FavoriteNewsDao
import id.revan.beritaku.data.db.dao.KeywordDao
import javax.inject.Singleton

@Module
class DatabaseModule(private val context: Context) {
    @Singleton
    @Provides
    fun provideAppDatabase(): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "beritaku")
            .fallbackToDestructiveMigration().build()
    }

    @Singleton
    @Provides
    fun provideKeywordDao(appDatabase: AppDatabase): KeywordDao {
        return appDatabase.getKeywordDao()
    }

    @Singleton
    @Provides
    fun provideNewsDao(appDatabase: AppDatabase): FavoriteNewsDao {
        return appDatabase.getFavoriteNewsDao()
    }
}