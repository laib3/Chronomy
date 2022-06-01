package it.polito.mainactivity.model

import android.graphics.Color
import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import it.polito.mainactivity.R
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
                val submitEndRepetitionDate: JSONObject = jo.getJSONObject("submitEndRepetitionDate")
                val erYear: Int = submitEndRepetitionDate.getInt("year")
                val erMonth: Int = submitEndRepetitionDate.getInt("month")
                val erDay: Int = submitEndRepetitionDate.getInt("day")
                return Timeslot(title, description, GregorianCalendar(dYear, dMonth, dDay),
                    startHour, endHour, location, category, repetition,
                    days, GregorianCalendar(erYear, erMonth, erDay))
            }
        */

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


        // fun toTimeslot(d: DocumentSnapshot?): Timeslot? {
        //     if (d == null)
        //         return null
        //     return try {

        //         val user = anyToUser(d.get("publisher"))

        //         val t =
        //         Timeslot(
        //             d.get("timeslotId") as String,
        //             d.get("title") as String,
        //             d.get("description") as String,
        //             formatStringToDate(d.get("date") as String),
        //             d.get("startHour") as String,
        //             d.get("endHour") as String,
        //             d.get("location") as String,
        //             d.get("category") as String,
        //             anyToUser(d.get("publisher")),
        //             d.get("status") as Timeslot.Status,
        //             (d.get("chats") as List<Any?>).map{ c -> c as Chat }.toMutableList(),
        //             (d.get("ratings") as List<Any?>).map{ r -> r as Rating }.toMutableList()
        //         )
        //         Log.d("Utils: timeslot:", t.toString())
        //         t
        //     } catch (e: Exception) {
        //         e.message?.let { Log.d("Utils: exception", it) }
        //         e.printStackTrace()
        //         null
        //     }
        // }

        fun toUser(d: DocumentSnapshot?): Map<String, String>? {
            if(d == null)
                return null
            return try {
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
            } catch(e: Exception){
                e.printStackTrace()
                null
            }
        }

        fun toSkill(d: DocumentSnapshot?): Map<String, String>?{
            if(d == null)
                return null
            return try {
                hashMapOf(
                    "category" to d.get("category") as String,
                    "description" to d.get("description") as String,
                    // TODO change to Boolean
                    "active" to d.get("active") as String
                )
            } catch(e: Exception){
                e.printStackTrace()
                null
            }
        }

        /** create a list of dates (Calendar) starting from the parameters; if repetitionType is null,
        * then the only present date will be `date`, otherwise it will find all the dates within the interval
        * date - endRepetitionDate **/
        fun createDates(date: Calendar, repetitionType: String?, endRepetitionDate: Calendar, daysOfWeek: List<Int>): List<Calendar> {
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

    }

    fun toRating(d: DocumentSnapshot?): Map<String, String>?{
        if(d == null)
            return null
        return try {
            hashMapOf(
                "sender" to d.get("sender") as String,
                "value" to d.get("value").toString(),
                "comment" to d.get("comment") as String
            )
        } catch(e: Exception){
            e.printStackTrace()
            null
        }
    }

    fun toChat(d: DocumentSnapshot?): Map<String, String>?{
        if(d == null)
            return null
        return try {
            hashMapOf(
                "assigned" to d.get("assigned") as String
            )
        } catch(e: Exception){
            e.printStackTrace()
            null
        }
    }

    // TODO check if string
    fun toMessage(d: DocumentSnapshot?): Map<String, String>?{
        if(d == null)
            return null
        return try {
            hashMapOf(
                "text" to d.get("text") as String,
                "assigned" to d.get("assigned") as String,
                "sender" to d.get("sender") as String
            )
        } catch(e: Exception){
            e.printStackTrace()
            null
        }
    }


}
// fun anyToUser(any: Any?): User {
//     // this should never happen
//     if (any == null)
//         return emptyUser()
//     val map = any as Map<String, Any?>
//     return User(
//         map["userId"] as String,
//         map["name"] as String,
//         map["surname"] as String,
//         map["nickname"] as String,
//         map["bio"] as String,
//         map["email"] as String,
//         map["location"] as String,
//         map["phone"] as String,
//         (map["skills"] as List<Map<Any?, Any?>>).map { s ->
//             Skill(
//                 s["category"] as String,
//                 s["description"] as String,
//                 s["active"] as Boolean
//             )
//         },
//         (map["balance"] as Long).toInt(),
//         map["profilePictureUrl"] as String?,
//         // TODO update with real values
//         mutableListOf(),
//         mutableListOf()
//     )
// }
