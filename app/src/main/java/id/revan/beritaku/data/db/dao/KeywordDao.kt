package id.revan.beritaku.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import id.revan.beritaku.data.model.Keyword

@Dao
interface KeywordDao {
    @Query("SELECT *FROM ms_keyword ORDER BY id DESC LIMIT 10")
    suspend fun getAll(): List<Keyword>

    @Query("SELECT *FROM ms_keyword WHERE name = :name")
    suspend fun getKeyword(name: String): Keyword?

    @Delete
    suspend fun delete(keyword: Keyword)

    @Insert
    suspend fun insert(keyword: Keyword)
}