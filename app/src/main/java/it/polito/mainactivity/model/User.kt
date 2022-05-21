package it.polito.mainactivity.model

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import it.polito.mainactivity.R

data class User (
    var uid: Int = 0,
    var name: String = "name",
    var surname: String = "surname",
    var nickname: String = "nickname",
    var bio: String = "bio",
    var email: String = "email",
    var location: String = "location",
    var phone: String = "phone",
    var skills: List<String> = listOf(),
    var balance: Int = 0,
    var profilePicture: Bitmap? = null

)