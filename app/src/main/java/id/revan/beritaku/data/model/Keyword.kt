package id.revan.beritaku.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ms_keyword")
data class Keyword (
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val name: String
)