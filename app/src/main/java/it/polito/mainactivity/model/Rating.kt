package it.polito.mainactivity.model

data class Rating(var rating: Int?, var comment: String?, var by: Message.Sender){

    fun toMap(): Map<String, Any?>{
        return hashMapOf(
            "rating" to rating,
            "comment" to comment,
            "by" to by
        )
    }

    constructor(ratingMap: Map<String, Any>): this(
        ratingMap["rating"] as Int,
        ratingMap["comment"] as String,
        ratingMap["by"]?.let { Message.Sender.valueOf(it as String) } ?: Message.Sender.ERROR
    )

}