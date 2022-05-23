package it.polito.mainactivity.ui.userprofile

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import it.polito.mainactivity.data.User
import it.polito.mainactivity.data.emptyUser
import it.polito.mainactivity.model.Utils

class UserProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> = _user
    private val _newUser = MutableLiveData<Boolean?>(null)
    val newUser: LiveData<Boolean?> = _newUser

    private lateinit var userListenerRegistration : ListenerRegistration

    init {
        FirebaseAuth.getInstance().addAuthStateListener { fAuth ->
            val userId = fAuth.currentUser?.uid
            if(userId != null){
                val userRef: DocumentReference = db.collection("users").document(userId)
                userRef.get().addOnSuccessListener {
                    // document doesn't exist
                    if(!it.exists()) {
                        userRef.set(emptyUser()).addOnSuccessListener {
                            Log.d("UserProfileViewModel", "user creation ok with id $userId")
                            userListenerRegistration = userRef.addSnapshotListener { value, _ ->
                                if (value != null) {
                                    _user.value = Utils.toUser(value)
                                    _newUser.value = true
                                    Log.d("UserProfileViewModel", "logged in as ${value.id}")
                                } else {
                                    Log.d("UserProfileViewModel", "error during log in")
                                    _user.value = null
                                    _newUser.value = null
                                }
                            }
                        }.addOnFailureListener { Log.d("UserProfileViewModel", "user not created") }
                    }
                    else {
                        // if document exists
                        userListenerRegistration = userRef.addSnapshotListener { value, error ->
                            if(value != null){
                                _user.value = Utils.toUser(value)
                                _newUser.value = false
                                Log.d("UserProfileViewModel", "logged in as (existing) ${value.id}")
                            } else {
                                Log.d("UserProfileViewModel", "error during (existing) log in")
                                _user.value = null
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
                _newUser.value = null
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        userListenerRegistration.remove()
    }

    // update user field and user inside the timeslots
    fun updateUserField(userId: String?, field: String, newValue: Any?): Boolean {
        //var returnValue = false
        if(userId == null || newValue == null)
            return false
        val userRef = db.collection("users").document(userId)
        val tsRef = db.collection("timeslots")

        tsRef.whereEqualTo("user.userId", userId).get().addOnSuccessListener { result ->
            val tsRefs = result.documents.map{ it.reference }
            userRef.update(field, newValue)
                .addOnSuccessListener {
                    userRef.get().addOnSuccessListener { userSnapshot ->
                        val user = Utils.toUser(userSnapshot)
                        tsRefs.forEach{ tsRef -> tsRef.update("user", user)
                            .addOnSuccessListener { Log.d("UserProfileViewModel", "timeslot updated successfully with user: " + user.toString()) }
                            .addOnFailureListener { Log.d("UserProfileViewModel", "update timeslot failure: " + it.message) }
                        }
                    }.addOnFailureListener { Log.d("UserProfileViewModel", "get user failure: " + it.message ) }
                }
                .addOnFailureListener { exception ->
                    Log.d("UserProfileViewModel", "update user failure: " + exception.message)
                }
        }

        return true
    }
}