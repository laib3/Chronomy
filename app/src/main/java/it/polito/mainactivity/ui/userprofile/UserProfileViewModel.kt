package it.polito.mainactivity.ui.userprofile

import android.graphics.Bitmap
import android.graphics.Picture
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import it.polito.mainactivity.R

class UserProfileViewModel : ViewModel() {

    // TODO load from Model
    private val _name = MutableLiveData<String>().apply{ value = "Mario" }
    private val _surname = MutableLiveData<String>().apply{ value = "Rossi" }
    private val _nickname = MutableLiveData<String>().apply{ value = "superMario" }
    private val _bio = MutableLiveData<String>().apply{ value = "Simpatico, solare, in cerca di amicizie" }
    private val _email = MutableLiveData<String>().apply{ value = "mario@rossi.it" }
    private val _phone = MutableLiveData<String>().apply{ value = "3331234560" }
    private val _location = MutableLiveData<String>().apply{ value = "Italy" }
    private val _balance = MutableLiveData<Int>().apply{ value = 5 }
    private val _picture = MutableLiveData<Drawable>().apply{ value = null }

    val name: LiveData<String> = _name
    val surname: LiveData<String> = _surname
    val nickname: LiveData<String> = _nickname
    val bio: LiveData<String> = _bio
    val email: LiveData<String> = _email
    val phone: LiveData<String> = _phone
    val location: LiveData<String> = _location
    val balance: LiveData<Int> = _balance
    val picture: LiveData<Drawable> = _picture
    val skills: MutableList<SkillViewModel> = createSkills()

    init {
        createSkills()
    }

    fun setName(s:String){ _name.value = s }
    fun setSurname(s:String) { _surname.value = s }
    fun setNickname(s:String) { _nickname.value = s }
    fun setBio(s:String) { _bio.value = s }
    fun setEmail(s:String) { _email.value = s }
    fun setPhone(s:String) { _phone.value = s }
    fun setLocation(s:String) { _location.value = s }
    fun setPicture(d:Drawable) { _picture.value = d }

    private fun createSkills() : MutableList<SkillViewModel> {
        return mutableListOf(
            SkillViewModel("Gardening").also{ it -> it.setActive(true); it.setDescription("I will take care of your plants.") },
            SkillViewModel("Tutoring"),
            SkillViewModel("Child Care"),
            SkillViewModel("Odd Jobs"),
            SkillViewModel("Home Repair"),
            SkillViewModel("Wellness").also{ it -> it.setActive(true) },
            SkillViewModel("Delivery"),
            SkillViewModel("Transportation"),
            SkillViewModel("Companionship"),
            SkillViewModel("Other")
        )
    }

}