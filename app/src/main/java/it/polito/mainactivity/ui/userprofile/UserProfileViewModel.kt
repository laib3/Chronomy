package it.polito.mainactivity.ui.userprofile

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ListenerRegistration
import it.polito.mainactivity.data.User
import it.polito.mainactivity.data.emptyUser

class UserProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val fAuth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> = _user
    private val _uId = MutableLiveData<String?>()
    val uId: LiveData<String?> = _uId

    private lateinit var userListenerRegistration : ListenerRegistration

    init {
        FirebaseAuth.getInstance().addAuthStateListener {
            val userId = it.currentUser?.uid
            if(userId != null){
                val userRef: DocumentReference = db.collection("users").document(userId)
                userRef.get().addOnSuccessListener {
                    if(!it.exists()){
                        userRef.set(emptyUser()).addOnSuccessListener {
                            Log.d("UserProfileViewModel", "user creation ok with id ${userId}")
                            userListenerRegistration = userRef.addSnapshotListener { value, error ->
                                if(value != null){
                                    _user.value = value.toUser()
                                    _uId.value = value.id
                                    Log.d("UserProfileViewModel", "logged in as ${value.id}")
                                } else {
                                    Log.d("UserProfileViewModel", "error during log in")
                                    _user.value = null
                                    _uId.value = null
                                }
                            }
                        }.addOnFailureListener { Log.d("UserProfileViewModel",  "user not created") }
                    }
                    // if document exists
                    userListenerRegistration = userRef.addSnapshotListener { value, error ->
                        if(value != null){
                            _user.value = value.toUser()
                            _uId.value = value.id
                            Log.d("UserProfileViewModel", "logged in as (existing) ${value.id}")
                        } else {
                            Log.d("UserProfileViewModel", "error during (existing) log in")
                            _user.value = null
                            _uId.value = null
                        }
                    }
                }.addOnFailureListener {
                    Log.d("UserProfileViewModel", "error db: cannot retrieve document with id ${userId}")
                }
            }
            else { // log out
                // TODO signal log out
                Log.d("UserProfileViewModel", "user log out")
                _user.value = null
                _uId.value = null
            }
        }
    }

    fun userIsRegistered(): Boolean{
        return uId.value != null
    }

    // fun isLoggedIn(userId: String): Boolean{
    //     return FirebaseAuth.getInstance().currentUser != null
    // }

    override fun onCleared() {
        super.onCleared()
        userListenerRegistration.remove()
    }

    private fun DocumentSnapshot.toUser(): User? {
        return try {
            User(
                get("name") as String,
                get("surname") as String,
                get("nickname") as String,
                get("bio") as String,
                get("email") as String,
                get("location") as String,
                get("phone") as String,
                //get("skills") as List<Skill>,
                listOf(),
                (get("balance") as Long).toInt(),
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

    fun updateTimeslotField(id: String?, field: String, newValue: Any?): Boolean {
        //var returnValue = false
        if(id == null)
            return false
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