package it.polito.mainactivity.data
import android.graphics.drawable.Drawable
import it.polito.mainactivity.model.Skill

data class User(
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
    var profilePictureUrl: String?
) {
    override fun toString() =
        """{ "name": "$name", "surname": "$surname", "nickname": "$nickname", "bio": "$bio", """ +
                """"email": "$email", "phone": "$phone", "location": "$location", "balance": $balance, "skills": $skills }"""
}

fun emptyUser(): User {
    return User(
        "name",
        "surname",
        "nickname",
        "bio",
        "email",
        "location",
        "phone",
        createEmptySkills(),
        0,
        listOf(),
        null
    )
}

fun createEmptySkills(): List<Skill> {
    return listOf(
        Skill("Gardening"),
        Skill("Tutoring"),
        Skill("Child Care"),
        Skill("Odd Jobs"),
        Skill("Home Repair"),
        Skill("Wellness"),
        Skill("Delivery"),
        Skill("Transportation"),
        Skill("Companionship"),
        Skill("Other")
    )
}
