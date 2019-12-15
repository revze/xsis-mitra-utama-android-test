package id.revan.beritaku.data.model

import com.google.gson.annotations.SerializedName

data class News (
    @SerializedName("snippet")
    val snippet: String,

    @SerializedName("pub_date")
    val pubDate: String,

    @SerializedName("web_url")
    val webUrl: String,

    @SerializedName("multimedia")
    val multimedia: List<NewsMultimedia>,

    @SerializedName("headline")
    val headline: NewsHeadline
)