package id.revan.beritaku.helper

import org.ocpsoft.prettytime.PrettyTime
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object DateTimeHelper {
    fun convertTimestampToLocalTime(timestamp: String): String {
        var result = ""
        try {
            val utcFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss+SSS")
            utcFormat.timeZone = TimeZone.getDefault()

            val date = utcFormat.parse(timestamp)

            val prettyTime = PrettyTime(Locale("id"))

            result = prettyTime.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return result
    }

    fun convertTimestampToReadableTime(timestamp: String): String {
        var result = ""
        try {
            val utcFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss+SSS")
            utcFormat.timeZone = TimeZone.getDefault()

            val date = utcFormat.parse(timestamp)

            val newFormat = SimpleDateFormat("EEEE, d MMMM yyyy HH:mm", Locale("id"))

            if (date != null) {
                result = newFormat.format(date)
            }
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return result
    }
}