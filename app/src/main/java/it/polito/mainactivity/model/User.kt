package it.polito.mainactivity.model

import com.google.firebase.auth.FirebaseAuth

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
    var profilePictureUrl: String?,
    var offers: MutableList<HashMap<String, Any>>,
    var requests: MutableList<HashMap<String, Any>>
) {
    override fun toString() =
        """{ "userId": "$userId", "name": "$name", "surname": "$surname", "nickname": "$nickname", "bio": "$bio", """ +
                """"email": "$email", "phone": "$phone", "location": "$location", "balance": $balance, "skills": $skills,""" +
               """ "offers":${offers}, "requests":${requests} """ .trimMargin()

    fun toMap(): HashMap<String, Any?>{
        return hashMapOf(
            "userId" to userId,
            "name" to name,
            "surname" to surname,
            "nickname" to nickname,
            "bio" to bio,
            "email" to email,
            "location" to location,
            "phone" to phone,
            "profilePictureUrl" to profilePictureUrl
            // no skills
        )
    }


}

fun emptyUser(): User {
    return User(
        FirebaseAuth.getInstance().currentUser!!.uid,
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        createEmptySkills(),
        5,
        null,
        mutableListOf(),
        mutableListOf()
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

