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
}