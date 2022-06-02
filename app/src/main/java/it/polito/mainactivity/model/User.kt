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
    var profilePictureUrl: String?
) {
    override fun toString() =
        """{ "userId": "$userId", "name": "$name", "surname": "$surname", "nickname": "$nickname", "bio": "$bio", """ +
                """"email": "$email", "phone": "$phone", "location": "$location", "balance": $balance, "skills": $skills,""".trimMargin()

    fun toMap(): HashMap<String, *>{
        return hashMapOf(
            "userId" to userId,
            "name" to name,
            "surname" to surname,
            "nickname" to nickname,
            "bio" to bio,
            "email" to email,
            "phone" to phone,
            "location" to location,
            "balance" to balance,
            "profilePictureUrl" to profilePictureUrl
            // no skills, only top-level fields
        )
    }

    constructor(userMap: Map<String, Any?>, skillMap: List<Map<String, Any>>): this(
        userMap["userId"] as String,
        userMap["name"] as String,
        userMap["surname"] as String,
        userMap["nickname"] as String,
        userMap["bio"] as String,
        userMap["email"] as String,
        userMap["location"] as String,
        userMap["phone"] as String,
        skillMap.map{ sm -> Skill(sm) },
        (userMap["balance"] as Long).toInt(),
        userMap["profilePictureUrl"]?.let{ it as String }
    )

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

