package id.revan.beritaku.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class News(
    @SerializedName("_id")
    val uuid: String,

    @SerializedName("snippet")
    val snippet: String,

    @SerializedName("pub_date")
    val pubDate: String,

    @SerializedName("web_url")
    val webUrl: String,

    @SerializedName("multimedia")
    val multimedia: List<NewsMultimedia>,

    @SerializedName("headline")
    val headline: NewsHeadline,

    var isFavorite: Boolean = false
) : Parcelable