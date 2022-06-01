package it.polito.mainactivity.model

data class Rating(var rating: Int?, var comment: String?, var by: Message.Sender){

    fun toMap(): Map<String, Any?>{
        return hashMapOf(
            "rating" to rating,
            "comment" to comment,
            "by" to by
        )
    }

    constructor(ratingMap: Map<String, String>): this(
        ratingMap["rating"]?.toInt() ?: 0,
        ratingMap["comment"] ?: "null",
        ratingMap["by"]?.let { Message.Sender.valueOf(it) } ?: Message.Sender.ERROR
    )

}