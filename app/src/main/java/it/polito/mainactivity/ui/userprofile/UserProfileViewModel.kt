package it.polito.mainactivity.ui.userprofile

import android.app.Application
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import it.polito.mainactivity.data.Timeslot
import it.polito.mainactivity.data.User
import it.polito.mainactivity.data.emptyUser
import it.polito.mainactivity.model.Field
import it.polito.mainactivity.model.Skill
import it.polito.mainactivity.model.UserProfileModel
import it.polito.mainactivity.model.Utils

class UserProfileViewModel(application: Application) : AndroidViewModel(application) {

    // instantiate user profile model - it will be a repository in future (?)
    //private val model: UserProfileModel = UserProfileModel(application)

    //private val _name: MutableLiveData<String> = model.getData(Field.NAME)
    //private val _surname: MutableLiveData<String> = model.getData(Field.SURNAME)
    //private val _nickname: MutableLiveData<String> = model.getData(Field.NICKNAME)
    //private val _bio: MutableLiveData<String> = model.getData(Field.BIO)
    //private val _email: MutableLiveData<String> = model.getData(Field.EMAIL)
    //private val _phone: MutableLiveData<String> = model.getData(Field.PHONE)
    //private val _location: MutableLiveData<String> = model.getData(Field.LOCATION)
    //private val _balance: MutableLiveData<String> = model.getData(Field.BALANCE)
    //private val _skills: MutableLiveData<List<Skill>> = model.getSkills()
    //private val _picture: MutableLiveData<Drawable> = model.getPicture()
    //private val _updated: MutableLiveData<Skill?> = MutableLiveData<Skill?>().apply{ value = null }

    //val name: LiveData<String> = _name
    //val surname: LiveData<String> = _surname
    //val nickname: LiveData<String> = _nickname
    //val bio: LiveData<String> = _bio
    //val email: LiveData<String> = _email
    //val phone: LiveData<String> = _phone
    //val location: LiveData<String> = _location
    //val balance: LiveData<String> = _balance
    //val skills: LiveData<List<Skill>> = _skills
    //val picture: LiveData<Drawable> = _picture
    //val updated: MutableLiveData<Skill?> = _updated

    //fun setName(s:String){ _name.value = s; model.setData(s, Field.NAME) }
    //fun setSurname(s: String){ _surname.value = s; model.setData(s, Field.SURNAME) }
    //fun setNickname(s:String) { _nickname.value = s; model.setData(s, Field.NICKNAME) }
    //fun setBio(s:String) { _bio.value = s; model.setData(s, Field.BIO) }
    //fun setEmail(s:String) { _email.value = s; model.setData(s, Field.EMAIL) }
    //fun setPhone(s:String) { _phone.value = s; model.setData(s, Field.PHONE) }
    //fun setLocation(s:String) { _location.value = s; model.setData(s, Field.LOCATION) }
    //fun setPicture(d: Drawable) { _picture.value = d; model.setPicture(d) }
    //fun setUpdated(s: Skill) { _updated.value = s }
    //fun resetUpdated() { _updated.value = null }
    //fun setSkills(s: List<Skill>?) { if(s != null) { _skills.value = s!!; model.setSkills(s) } }
    //private val TIME_LENGTH: Int = 5

    private val _user = MutableLiveData<User>()
    val user: LiveData<User> = _user

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var lUser : ListenerRegistration

    var id = "02SI6rzBrETnUoFGLCJH"
    init {
        lUser= db.collection("users").document(id)
            .addSnapshotListener { d, e ->
                if (e == null && d != null) {
                    _user.value = d.toUser()
                    Log.d("USEr", _user.value.toString())
                } else _user.value = emptyUser()
            }

    }

    override fun onCleared() {
        super.onCleared()
        lUser.remove()
    }

    private fun DocumentSnapshot.toUser(): User? {
        return try {
            User(
                id,
                get("name") as String,
                get("surname") as String,
                get("nickname") as String,
                get("bio") as String,
                get("email") as String,
                get("location") as String,
                get("phone") as String,
                get("skills") as List<String>,
                get("balance") as Int,
                listOf(),
               null
                // TODO: update with real values
                //get("timeslots") as List<String>,
                //get("profilePicture") as String
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun updateTimeslotField(id: String, field: String, newValue: Any?): Boolean {
        //var returnValue = false
        db
            .collection("users")
            .document(id)
            .update(field, newValue)
            .addOnSuccessListener {
                Log.d(
                    "Firebase",
                    "User updated successfully"
                ); //returnValue = true;
            }
            .addOnFailureListener {
                Log.d(
                    "Firebase",
                    "Error: user not updated correctly"
                ); //returnValue = false;
            }
        return true
    }
}