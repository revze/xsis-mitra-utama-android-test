package id.revan.beritaku.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class NewsMultimedia(
    @SerializedName("url")
    val url: String
) : Parcelable