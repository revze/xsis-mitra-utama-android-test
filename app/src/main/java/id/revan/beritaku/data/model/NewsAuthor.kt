package id.revan.beritaku.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class NewsAuthor(
    @SerializedName("original")
    val name: String?
) : Parcelable