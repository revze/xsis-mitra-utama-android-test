package id.revan.beritaku.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import id.revan.beritaku.data.db.dao.KeywordDao
import id.revan.beritaku.data.model.Keyword

@Database(entities = [Keyword::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun keywordDao(): KeywordDao
}