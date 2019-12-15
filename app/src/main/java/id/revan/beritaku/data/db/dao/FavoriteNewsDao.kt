package id.revan.beritaku.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import id.revan.beritaku.data.model.FavoriteNews

@Dao
interface FavoriteNewsDao {
    @Query("SELECT *FROM ms_favorite_news ORDER BY id DESC")
    suspend fun getAll(): List<FavoriteNews>

    @Query("SELECT *FROM ms_favorite_news WHERE uuid = :uuid")
    suspend fun getNews(uuid: String): FavoriteNews?

    @Delete
    suspend fun delete(news: FavoriteNews)

    @Query("DELETE FROM ms_favorite_news WHERE uuid = :uuid")
    suspend fun delete(uuid: String)

    @Insert
    suspend fun insert(news: FavoriteNews)
}