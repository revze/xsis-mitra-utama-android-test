package id.revan.beritaku.data.response

import com.google.gson.annotations.SerializedName
import id.revan.beritaku.data.model.News

data class NewsResponse(
    @SerializedName("response")
    val response: Response
)

data class Response(
    @SerializedName("docs")
    val docs: List<News>
)