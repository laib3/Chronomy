package it.polito.mainactivity.data

data class User (
    var uid: String = "userId",
    var name: String = "name",
    var surname: String = "surname",
    var nickname: String = "nickname",
    var bio: String = "bio",
    var email: String = "email",
    var location: String = "location",
    var phone: String = "phone",
    var skills: List<String> = listOf(),
    var balance: Int = 0,
    var timeslots: List<String> = listOf(),
    var profilePicture: String? = null
){
    override fun toString(): String {
        return "${name} ${surname}, ${nickname}"
    }
}
