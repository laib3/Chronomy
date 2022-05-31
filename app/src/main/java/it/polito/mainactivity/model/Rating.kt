package it.polito.mainactivity.model

data class Rating(var rating: Int?, var comment: String?, var by: Message.Sender){

    fun toMap(): HashMap<String, Any?>{
        return hashMapOf(
            "rating" to rating,
            "comment" to comment,
            "by" to by
        )
    }

}