package it.polito.mainactivity.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import it.polito.mainactivity.model.*
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

class UserProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> = _user
    private val _newUser = MutableLiveData<Boolean?>(null)
    val newUser: LiveData<Boolean?> = _newUser

    private lateinit var userListenerRegistration: ListenerRegistration

    init {
        FirebaseAuth.getInstance().addAuthStateListener { fAuth ->
            val userId = fAuth.currentUser?.uid
            if (userId != null) {
                viewModelScope.launch {
                    val u = getUserById(userId)
                    if(u == null){ // user doesn't exist
                        val userRef = db.collection("users").document(userId)
                        userRef.set(emptyUser().toMap()).await() // add user
                        val skillsSnapshot = userRef.collection("skills").get().await()
                        skillsSnapshot.documents.mapNotNull{ ss -> Utils.toSkill(ss) }.forEach{ sm ->
                            userRef.collection("skills").add(sm).await() // add skills to db
                        }
                    }
                    else {
                    }
                }

                db.collection("users").document(userId).get().addOnSuccessListener { userSnapshot ->
                    // document doesn't exist
                    if (!userSnapshot.exists()) {
                        userSnapshot.reference.set(emptyUser().toMap()) // create new user in db
                            .addOnSuccessListener {
                            // add skills to user
                            db.runBatch { batch ->
                                createEmptySkills().map{ s -> s.toMap() }.forEach{ sm ->
                                    batch.set(userSnapshot.reference.collection("skills").document(), sm) // add skills to user
                                }
                            }.addOnCompleteListener { // skills added
                                Log.d("UserProfileViewModel", "skills added")
                            }
                            userListenerRegistration = userSnapshot.reference.addSnapshotListener { value, _ -> // on change
                                if (value != null) {
                                    val userMap = Utils.toUser(value)
                                    // get skills
                                    if(userMap != null){
                                        value.reference.collection("skills").get().addOnSuccessListener { skillsSnapshots ->
                                            // TODO modify, skills should exist always
                                            val skillsMaps = skillsSnapshots.documents
                                                .mapNotNull{ ss -> Utils.toSkill(ss) }
                                            _user.value = User(userMap, skillsMaps)
                                            _newUser.value = true
                                            Log.d("UserProfileViewModel", "logged in as ${value.id}")
                                        }
                                    }
                                } else {
                                    Log.d("UserProfileViewModel", "error during log in")
                                    _user.value = null
                                    _newUser.value = null
                                }
                            }
                        }.addOnFailureListener { Log.d("UserProfileViewModel", "publisher not created") }
                    } else { // user already exists
                        userListenerRegistration = userSnapshot.reference.addSnapshotListener { us, _ ->
                            if (us != null) {
                                val userMap = Utils.toUser(us)
                                if(userMap != null){
                                    us.reference.collection("skills").get().addOnSuccessListener { skillsSnapshot ->
                                        val skillsMap = skillsSnapshot.documents
                                            .mapNotNull{ ss -> Utils.toSkill(ss) }
                                        _user.value = User(userMap, skillsMap)
                                        Log.d("UserProfileViewModel", "logged in as (existing) ${us.id}")
                                    }
                                }
                            } else {
                                Log.d("UserProfileViewModel", "error during (existing user) log in")
                                _user.value = null
                                _newUser.value = null
                            }
                        }
                    }
                }.addOnFailureListener {
                    Log.d(
                        "UserProfileViewModel",
                        "error db: cannot retrieve document with id $userId"
                    )
                }
            } else { // log out
                // TODO signal log out
                Log.d("UserProfileViewModel", "publisher log out")
                _user.value = null
                _newUser.value = null
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        userListenerRegistration.remove()
    }

    // TODO update fields
    // update publisher inside the timeslots and client inside chats
    fun updateUserField(field: String, newValue: Any?): Boolean {
        //var returnValue = false
        val userId = auth.currentUser?.uid!!
        if (newValue == null)
            return false
        val userRef = db.collection("users").document(userId)
        val tsRef = db.collection("timeslots")

        tsRef.whereEqualTo("publisher.userId", userId).get().addOnSuccessListener { result ->
            val tsRefs = result.documents.map { it.reference }
            userRef.update(field, newValue)
                .addOnSuccessListener {
                    userRef.get().addOnSuccessListener { userSnapshot ->
                        val user = Utils.toUser(userSnapshot)
                        tsRefs.forEach { tsRef ->
                            tsRef.update("publisher", user)
                                .addOnSuccessListener {
                                    Log.d(
                                        "UserProfileViewModel",
                                        "timeslot updated successfully with publisher: " + user.toString()
                                    )
                                }
                                .addOnFailureListener {
                                    Log.d(
                                        "UserProfileViewModel",
                                        "update timeslot failure: " + it.message
                                    )
                                }
                        }
                    }.addOnFailureListener {
                        Log.d(
                            "UserProfileViewModel",
                            "get publisher failure: " + it.message
                        )
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("UserProfileViewModel", "update publisher failure: " + exception.message)
                }
        }
        return true
    }

    suspend fun addNewUser(userId: String){
        val userRef = db.collection("users").document(userId)
        userRef.set(emptyUser().toMap()).await() // add user
        val skillsSnapshot = userRef.collection("skills").get().await()
        skillsSnapshot.documents.mapNotNull{ ss -> Utils.toSkill(ss) }.forEach{ sm ->
            userRef.collection("skills").add(sm).await() // add skills to db
        }
    }

    suspend fun getUserById(id: String): User? {
        val userSnapshot = db.collection("users").document(id).get().await()
        if(!userSnapshot.exists())
            return null
        val userMap = Utils.toUser(userSnapshot) ?: return null
        val skillsSnapshot = userSnapshot.reference.collection("skills").get().await()
        val skillsMaps = skillsSnapshot.mapNotNull { ss -> Utils.toSkill(ss) }
        return User(userMap, skillsMaps)
    }

}