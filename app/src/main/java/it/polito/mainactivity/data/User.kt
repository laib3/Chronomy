package it.polito.mainactivity.data
import android.graphics.drawable.Drawable
import com.google.firebase.auth.FirebaseAuth
import it.polito.mainactivity.model.Skill

data class User(
    val userId: String,
    var name: String,
    var surname: String,
    var nickname: String,
    var bio: String,
    var email: String,
    var location: String,
    var phone: String,
    var skills: List<Skill>,
    var balance: Int,
    var timeslots: List<String>,
    var profilePicture: Drawable?
) {
    override fun toString() =
        """{ "userId": "$userId", "name": "$name", "surname": "$surname", "nickname": "$nickname", "bio": "$bio", """ +
                """"email": "$email", "phone": "$phone", "location": "$location", "balance": $balance, "skills": $skills }"""
}

fun emptyUser(): User {
    return User(
        FirebaseAuth.getInstance().currentUser!!.uid,
        "name",
        "surname",
        "nickname",
        "bio",
        "email",
        "location",
        "phone",
        listOf(),
        0,
        listOf(),
        null
    )
}
