package it.polito.mainactivity.model

import android.graphics.Color
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import it.polito.mainactivity.R
import it.polito.mainactivity.data.Timeslot
import it.polito.mainactivity.data.User
import it.polito.mainactivity.data.emptyUser
import org.json.JSONArray
import org.json.JSONObject
import java.text.DateFormat
import java.util.*

class Utils {

    companion object {

        fun JSONArrayToList(ja: JSONArray): MutableList<JSONObject> {
            val list: MutableList<JSONObject> = mutableListOf()
            for (i in 0 until ja.length()) {
                list.add(ja.getJSONObject(i))
            }
            return list
        }

        fun JSONArrayToIntList(ja: JSONArray): MutableList<Int> {
            val list: MutableList<Int> = mutableListOf()
            for (i in 0 until ja.length()) {
                list.add(ja.getInt(i))
            }
            return list
        }

        /*
        fun JSONObjectToTimeslot(jo: JSONObject): Timeslot {
                val title: String = jo.getString("title")
                val description: String = jo.getString("description")
                val date: JSONObject = jo.getJSONObject("startDate")
                val dYear: Int = date.getInt("year")
                val dMonth: Int = date.getInt("month")
                val dDay: Int = date.getInt("day")
                val startHour: String = jo.getString("startHour")
                val endHour: String = jo.getString("endHour")
                val location: String = jo.getString("location")
                val category: String = jo.getString("category")
                var repetition: String? = jo.getString("repetition")
                // NOTE: no repetition is saved with null value, not null string like in json
                if (repetition == "null" || repetition == "")
                    repetition = null
                val JSONDays: JSONArray = jo.getJSONArray("days")
                val days: MutableList<Int> = JSONArrayToIntList(JSONDays)
                val endRepetitionDate: JSONObject = jo.getJSONObject("endRepetitionDate")
                val erYear: Int = endRepetitionDate.getInt("year")
                val erMonth: Int = endRepetitionDate.getInt("month")
                val erDay: Int = endRepetitionDate.getInt("day")
                return Timeslot(title, description, GregorianCalendar(dYear, dMonth, dDay),
                    startHour, endHour, location, category, repetition,
                    days, GregorianCalendar(erYear, erMonth, erDay))
            }
        */

        fun formatStringToDate(strDate: String): Calendar {
            // TODO: improve using dateFormat
            val day = strDate.split("/")[0]
            val month = strDate.split("/")[1]
            val year = strDate.split("/")[2]

            // TODO: Check why this -1 is needed
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
            var durationM: Int = if (startM >= endM) startM - endM else {
                durationH -= 1
                startM + 60 - endM
            }
            return "%dh %02dm".format(durationH, durationM)
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

        fun toTimeslot(d: DocumentSnapshot?, user: User): Timeslot? {
            if(d == null)
                return null
            return try {
                Timeslot(
                    d.id,
                    d.get("title") as String,
                    d.get("description") as String,
                    Utils.formatStringToDate(d.get("startDate") as String),
                    d.get("startHour") as String,
                    d.get("endHour") as String,
                    d.get("location") as String,
                    d.get("category") as String,
                    d.get("repetition") as String?,
                    (d.get("days") as List<Number>).map{it.toInt()},
                    Utils.formatStringToDate(d.get("endRepetitionDate") as String),
                    user
                )
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        fun toUser(d: DocumentSnapshot?): User? {
            if(d == null)
                return null
            return try {
                User(
                    d.get("userId") as String,
                    d.get("name") as String,
                    d.get("surname") as String,
                    d.get("nickname") as String,
                    d.get("bio") as String,
                    d.get("email") as String,
                    d.get("location") as String,
                    d.get("phone") as String,
                    //get("skills") as List<Skill>,
                    (d.get("skills") as List<Map<Any?,Any?>>).map{s -> Skill(s["category"] as String, s["description"] as String, s["active"] as Boolean)},
                    (d.get("balance") as Long).toInt(),
                    d.get("profilePictureUrl") as String?
                    // TODO: update with real values
                    //get("timeslots") as List<String>,
                    //get("profilePicture") as String
                )
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        fun anyToUser(any: Any?): User {
            // this should never happen
            if (any == null)
                return emptyUser()
            val map = any as Map<String, Any?>
            return User(
                map["userId"] as String,
                map["name"] as String,
                map["surname"] as String,
                map["nickname"] as String,
                map["bio"] as String,
                map["email"] as String,
                map["location"] as String,
                map["phone"] as String,
                (map["skills"] as List<Map<Any?, Any?>>).map { s ->
                    Skill(
                        s["category"] as String,
                        s["description"] as String,
                        s["active"] as Boolean
                    )
                },
                (map["balance"] as Long).toInt(),
                map["profilePictureUrl"] as String?
            )
        }

    }

}