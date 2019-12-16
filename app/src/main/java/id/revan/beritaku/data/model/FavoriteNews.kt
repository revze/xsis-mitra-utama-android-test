package id.revan.beritaku.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ms_favorite_news")
data class FavoriteNews(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val uuid: String,

    val snippet: String,

    val pubDate: String,

    val webUrl: String,

    val thumbnail: String,

    val title: String,

    val author: String?,

    val leadParagraph: String,

    val source: String?
)