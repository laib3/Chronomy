package it.polito.mainactivity.model

import android.graphics.Color
import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import it.polito.mainactivity.R
import org.json.JSONArray
import org.json.JSONObject
import java.text.DateFormat
import java.util.*
import kotlin.collections.HashMap

class Utils {

    companion object {

        fun formatStringToDate(strDate: String): Calendar {
            val day = strDate.split("/")[0]
            val month = strDate.split("/")[1]
            var year = strDate.split("/")[2]

            if (year.length == 2) year = "20${year}"

            // -1 is needed because months start from 0
            return GregorianCalendar(year.toInt(), month.toInt() - 1, day.toInt())
        }

        fun formatDateToString(date: Calendar?): String {
            if (date == null)
                return ""
            val dateFormat: DateFormat = DateFormat.getDateInstance(DateFormat.SHORT, Locale.ITALY)
            dateFormat.timeZone = date.timeZone
            return dateFormat.format(date.time)
        }

        fun formatYearMonthDayToString(year: Int, month: Int, day: Int): String {
            val date = GregorianCalendar(year, month, day)
            return formatDateToString(date)
        }

        fun getSkillImgRes(title: String): Int? {
            return when (title) {
                "Gardening" -> R.drawable.ic_skill_gardening
                "Tutoring" -> R.drawable.ic_skill_tutoring
                "Child Care" -> R.drawable.ic_skill_child_care
                "Odd Jobs" -> R.drawable.ic_skill_odd_jobs
                "Home Repair" -> R.drawable.ic_skill_home_repair
                "Wellness" -> R.drawable.ic_skill_wellness
                "Delivery" -> R.drawable.ic_skill_delivery
                "Transportation" -> R.drawable.ic_skill_transportation
                "Companionship" -> R.drawable.ic_skill_companionship
                "Other" -> R.drawable.ic_skill_other
                else -> null
            }
        }

        fun formatTime(hh: Int, mm: Int): String = String.format("%02d:%02d", hh, mm)

        fun getDuration(startHourMinutes: String, endHourMinutes: String): String {
            val endH: Int = startHourMinutes.split(":")[0].toInt()
            val endM: Int = startHourMinutes.split(":")[1].toInt()
            val startH: Int = endHourMinutes.split(":")[0].toInt()
            val startM: Int = endHourMinutes.split(":")[1].toInt()

            var durationH: Int = startH - endH
            val durationM: Int = if (startM >= endM) startM - endM else {
                durationH -= 1
                startM + 60 - endM
            }
            return "%dh:%02dm".format(durationH, durationM)
        }

        fun durationInMinutes(duration: String): Int {
            val hours = duration.split(":")[0].dropLast(1).toInt()
            val minutes = duration.split(":")[1].dropLast(1).toInt()
            return minutes + hours * 60
        }

        fun getDayName(num: Int): String = when (num) {
            Calendar.SUNDAY -> "Sunday"
            Calendar.MONDAY -> "Monday"
            Calendar.TUESDAY -> "Tuesday"
            Calendar.WEDNESDAY -> "Wednesday"
            Calendar.THURSDAY -> "Thursday"
            Calendar.FRIDAY -> "Friday"
            Calendar.SATURDAY -> "Saturday"
            else -> ""
        }

        fun getInitialsOfDayName(num: Int) = getDayName(num).substring(0, 2).uppercase()

        fun getDaysOfRepetition(days: List<Int>): String =
            days.sorted().joinToString(", ", "", "", -1, "...") { getDayName(it) }

        fun getSnackbarColor(msg: String): Int =
            if (msg.startsWith("ERROR:")) Color.parseColor("#ffff00")
            else Color.parseColor("#55ff55")

        fun toTimeslotMap(d: DocumentSnapshot?): HashMap<String, Any>? {
            if (d == null)
                return null
            return try {
                hashMapOf(
                    "timeslotId" to d.get("timeslotId") as String,
                    "title" to d.get("title") as String,
                    "description" to d.get("description") as String,
                    "date" to d.get("date") as String,
                    "startHour" to d.get("startHour") as String,
                    "endHour" to d.get("endHour") as String,
                    "location" to d.get("location") as String,
                    "category" to d.get("category") as String,
                    "publisher" to d.get("publisher") as Map<String, String>
                )
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        fun toUserMap(d: DocumentSnapshot?): Map<String, String?>? {
            if (d == null)
                return null
            return try {
                if(d.get("profilePictureUrl") != null)
                    hashMapOf(
                        "userId" to d.get("userId") as String,
                        "name" to d.get("name") as String,
                        "surname" to d.get("surname") as String,
                        "nickname" to d.get("nickname") as String,
                        "bio" to d.get("bio") as String,
                        "email" to d.get("email") as String,
                        "location" to d.get("location") as String,
                        "phone" to d.get("phone") as String,
                        "profilePictureUrl" to d.get("profilePictureUrl") as String
                    )
                else
                    hashMapOf(
                        "userId" to d.get("userId") as String,
                        "name" to d.get("name") as String,
                        "surname" to d.get("surname") as String,
                        "nickname" to d.get("nickname") as String,
                        "bio" to d.get("bio") as String,
                        "email" to d.get("email") as String,
                        "location" to d.get("location") as String,
                        "phone" to d.get("phone") as String,
                    )
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        fun toSkillMap(d: DocumentSnapshot?): Map<String, Any>? {
            if (d == null)
                return null
            return try {
                hashMapOf(
                    "category" to d.get("category") as String,
                    "description" to d.get("description") as String,
                    "active" to d.get("active") as Boolean
                )
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        /** create a list of dates (Calendar) starting from the parameters; if repetitionType is null,
         * then the only present date will be `date`, otherwise it will find all the dates within the interval
         * date - endRepetitionDate **/
        fun createDates(
            date: Calendar,
            repetitionType: String?,
            endRepetitionDate: Calendar,
            daysOfWeek: List<Int>
        ): List<Calendar> {
            val tmp: Calendar = date.clone() as Calendar
            val list: MutableList<Calendar> = mutableListOf()
            when {
                repetitionType?.lowercase() == "weekly" -> {
                    while (tmp.before(endRepetitionDate)) {
                        if (daysOfWeek.contains(tmp.get(Calendar.DAY_OF_WEEK))) {
                            list.add(tmp.clone() as Calendar)
                        }
                        // increment tmp by one day
                        tmp.add(Calendar.DATE, 1)
                    }
                }
                repetitionType?.lowercase() == "monthly" -> { //monthly
                    while (tmp.before(endRepetitionDate)) {
                        list.add(tmp.clone() as Calendar)
                        // increment tmp by one month
                        tmp.add(Calendar.MONTH, 1)
                    }
                }
                else -> list.add(date)
            }
            return list
        }


        fun toRatingMap(d: DocumentSnapshot?): Map<String, String>? {
            if (d == null)
                return null
            return try {
                hashMapOf(
                    "sender" to d.get("sender") as String,
                    "value" to d.get("value").toString(),
                    "comment" to d.get("comment") as String
                )
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        fun toChatMap(d: DocumentSnapshot?): Map<String, Any>? {
            if (d == null)
                return null
            return try {
                hashMapOf(
                    "client" to d.get("client") as Map<String, String>,
                    "assigned" to d.get("assigned") as String
                )
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        // TODO check if string
        fun toMessageMap(d: DocumentSnapshot?): Map<String, String>? {
            if (d == null)
                return null
            return try {
                hashMapOf(
                    "text" to d.get("text") as String,
                    "assigned" to d.get("assigned") as String,
                    "sender" to d.get("sender") as String
                )
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}
