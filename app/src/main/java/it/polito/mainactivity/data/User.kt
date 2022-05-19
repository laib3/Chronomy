package it.polito.mainactivity.data

data class User(
    var uid: String,
    var name: String,
    var surname: String,
    var nickname: String,
    var bio: String,
    var email: String,
    var location: String,
    var phone: String,
    var skills: List<String>,
    var balance: Int,
    var timeslots: List<String>,
    var profilePicture: String?
) {
    override fun toString() =
        """{ "name": "$name", "surname": "$surname", "nickname": "$nickname", "bio": "$bio", """ +
                """"email": "$email", "phone": "$phone", "location": "$location", "balance": $balance, "skills": $skills }"""
}

fun emptyUser(): User {
    return User(
        "uid",
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
        ""
    )
}
