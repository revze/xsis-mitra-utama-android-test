package id.revan.beritaku.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import id.revan.beritaku.data.db.dao.FavoriteNewsDao
import id.revan.beritaku.data.db.dao.KeywordDao
import id.revan.beritaku.data.model.FavoriteNews
import id.revan.beritaku.data.model.Keyword

@Database(entities = [Keyword::class, FavoriteNews::class], version = 3)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getKeywordDao(): KeywordDao

    abstract fun getFavoriteNewsDao(): FavoriteNewsDao
}