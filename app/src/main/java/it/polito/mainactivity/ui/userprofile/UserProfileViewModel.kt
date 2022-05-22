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
import it.polito.mainactivity.model.Skill

class UserProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val fAuth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> = _user
    private val _uId = MutableLiveData<String?>()
    val uId: LiveData<String?> = _uId
    private val _newUser = MutableLiveData<Boolean?>(null)
    val newUser: LiveData<Boolean?> = _newUser

    private lateinit var userListenerRegistration : ListenerRegistration

    init {
        FirebaseAuth.getInstance().addAuthStateListener {
            val userId = it.currentUser?.uid
            if(userId != null){
                val userRef: DocumentReference = db.collection("users").document(userId)
                userRef.get().addOnSuccessListener {
                    // document doesn't exist
                    if(!it.exists()) {
                        userRef.set(emptyUser()).addOnSuccessListener {
                            Log.d("UserProfileViewModel", "user creation ok with id ${userId}")
                            userListenerRegistration = userRef.addSnapshotListener { value, error ->
                                if (value != null) {
                                    _user.value = value.toUser()
                                    _uId.value = value.id
                                    _newUser.value = true
                                    Log.d("UserProfileViewModel", "logged in as ${value.id}")
                                } else {
                                    Log.d("UserProfileViewModel", "error during log in")
                                    _user.value = null
                                    _uId.value = null
                                    _newUser.value = null
                                }
                            }
                        }.addOnFailureListener { Log.d("UserProfileViewModel", "user not created") }
                    }
                    else {
                        // if document exists
                        userListenerRegistration = userRef.addSnapshotListener { value, error ->
                            if(value != null){
                                _user.value = value.toUser()
                                _uId.value = value.id
                                _newUser.value = false
                                Log.d("UserProfileViewModel", "logged in as (existing) ${value.id}")
                            } else {
                                Log.d("UserProfileViewModel", "error during (existing) log in")
                                _user.value = null
                                _uId.value = null
                                _newUser.value = null
                            }
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
                _newUser.value = null
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
                (get("skills") as List<Map<Any?,Any?>>).map{s -> Skill(s["category"] as String, s["description"] as String, s["active"] as Boolean)},
                (get("balance") as Long).toInt(),
                listOf(),
                "https://firebasestorage.googleapis.com/v0/b/chronomy-3fc87.appspot.com/o/dog-png-22667.png?alt=media&token=073755c2-c289-4af4-b0f6-b4110ae07b17"
                //get("profilePicture") as String?,
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
        if(id == null || newValue == null)
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