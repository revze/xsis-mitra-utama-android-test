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

    @SerializedName("lead_paragraph")
    val leadParagraph: String,

    @SerializedName("byline")
    val author: NewsAuthor,

    @SerializedName("source")
    val source: String,

    var isFavorite: Boolean = false
) : Parcelable