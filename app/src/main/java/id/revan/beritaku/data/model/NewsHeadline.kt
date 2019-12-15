package id.revan.beritaku.data.model

import com.google.gson.annotations.SerializedName

data class NewsHeadline (
    @SerializedName("main")
    val main: String
)