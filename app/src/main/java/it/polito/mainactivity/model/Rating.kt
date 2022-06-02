package it.polito.mainactivity.model

data class Rating(val timeslotId: String, var rating: Int?, var comment: String?, var by: Message.Sender){

    fun toMap(): Map<String, Any?>{
        return hashMapOf(
            "timeslotId" to timeslotId,
            "rating" to rating,
            "comment" to comment,
            "by" to by
        )
    }

    constructor(ratingMap: Map<String, Any>): this(
        ratingMap["timeslotId"] as String,
        ratingMap["rating"] as Int,
        ratingMap["comment"] as String,
        ratingMap["by"]?.let { Message.Sender.valueOf(it as String) } ?: Message.Sender.ERROR
    )

}

fun createBlankRatings(timeslotId: String): List<Rating>{
    return listOf(
        Rating(timeslotId, -1, "", Message.Sender.PUBLISHER),
        Rating(timeslotId, -1, "", Message.Sender.CLIENT)
    )
}