package it.polito.mainactivity.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
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
    private lateinit var skillsListenerRegistration: ListenerRegistration

    init {

        FirebaseAuth.getInstance().addAuthStateListener { fAuth ->
            val userId = fAuth.currentUser?.uid
            if (userId != null) { // log in
                viewModelScope.launch {
                    val u = getUserById(userId)
                    if(u == null){ // user doesn't exist
                        if(addNewUser(userId)){
                            Log.d("UserProfileViewModel", "new user created with id $userId")
                            val userRef = db.collection("users").document(userId).get().await().reference
                            addUserSnapshotListener(userRef, true) // watch for changes
                        }
                        else {
                            Log.d("UserProfileViewModel", "user creation error with id $userId")
                        }
                    }
                    else { // user exists
                        val userRef = db.collection("users").document(userId).get().await().reference
                        addUserSnapshotListener(userRef, false) // watch for changes
                    }
                }
            } else { // log out
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
        skillsListenerRegistration.remove()
    }

    fun updateUserSkill(category: String, active: Boolean, description: String){
        viewModelScope.launch {
            val userId = auth.currentUser?.uid!!
            val skillSnapshot =
                db
                    .collection("users")
                    .document(userId)
                    .collection("skills")
                    .whereEqualTo("category", category).get().await()
            skillSnapshot.documents[0].reference.update("active", active, "description", description).await()
        }
    }

    // Only first level fields, no skills
    // update publisher inside the timeslots and client inside chats
    fun updateUserField(field: String, newValue: Any?): Boolean {
        //var returnValue = false
        if(field == "skills") throw Exception("cannot update skills with this function")
        val userId = auth.currentUser?.uid!!
        if (newValue == null)
            return false

        viewModelScope.launch {
            try {
                // update user in users collection
                db.collection("users").document(userId).update(field, newValue).await()
                // update publisher in timeslots if any
                val timeslotsSnapshot = db.collection("timeslots").whereEqualTo("publisher.userId", userId).get().await()
                timeslotsSnapshot.documents
                    .forEach{ ts ->
                        val publisherMap = Utils.toTimeslotMap(ts)?.get("publisher") as MutableMap<String, String>
                        ts.reference.update("publisher", publisherMap).await()
                    }
                // update client in chats if any
                val chatsSnapshots = db.collectionGroup("chats").whereEqualTo("client.userId", userId).get().await()
                chatsSnapshots.documents
                    .forEach { cs ->
                        val clientMap = Utils.toChatMap(cs)?.get("client") as MutableMap<String, String>
                        cs.reference.update("client", clientMap).await()
                    }
            } catch(e: FirebaseFirestoreException){
                Log.d("UserProfileViewModel", "updateUserField: FirebaseFirestoreException: " + e.message)
                e.printStackTrace()
            } catch(e: Exception){
                Log.d("UserProfileViewModel", "updateUserField: Exception: " + e.message)
                e.printStackTrace()
            }
        }
        return true
    }

    private suspend fun addNewUser(userId: String): Boolean {
        return try {
            val userRef = db.collection("users").document(userId)
            userRef.set(emptyUser().toMap()).await() // add user
            createEmptySkills().map{ skill -> skill.toMap() }.forEach{ sm ->
                userRef.collection("skills").add(sm).await()
            }
            true
        } catch(e: FirebaseFirestoreException){
            e.printStackTrace()
            false
        } // TODO add catch for generic exception
    }

    suspend fun getUserById(id: String): User? {
        try {
            val userSnapshot = db.collection("users").document(id).get().await()
            if(!userSnapshot.exists())
                return null
            val userMap = Utils.toUserMap(userSnapshot) ?: return null
            val skillsSnapshot = userSnapshot.reference.collection("skills").get().await()
            val skillsMaps = skillsSnapshot.mapNotNull { ss -> Utils.toSkillMap(ss) }
            return User(userMap, skillsMaps)
        } catch(e: FirebaseFirestoreException){
            e.printStackTrace()
            return null
        }
    }

    private fun addUserSnapshotListener(userRef: DocumentReference, isNewUser: Boolean){
        userListenerRegistration = userRef.addSnapshotListener{ userSnapshot, _ ->
            if (userSnapshot != null) {
                viewModelScope.launch {
                    val userMap = Utils.toUserMap(userSnapshot)
                    if(userMap != null){
                        val skillsSnapshots = userSnapshot.reference.collection("skills").get().await()
                        val skillsMaps = skillsSnapshots.documents.map{ ss -> Utils.toSkillMap(ss)!! }
                        _user.value = User(userMap, skillsMaps)
                        _newUser.value = isNewUser
                    }
                }
            } else {
                Log.d("UserProfileViewModel", "error during log in")
                _user.value = null
                _newUser.value = null
            }
        }
        skillsListenerRegistration =
            userRef.collection("skills").addSnapshotListener { skillsCollectionSnapshot, _ ->
                // TODO add isEmpty, not != null
            if(skillsCollectionSnapshot != null && skillsCollectionSnapshot.documents.size > 0){
                viewModelScope.launch {
                    if(user.value != null){
                        val updatedSkills = skillsCollectionSnapshot.documents.map{ ss -> Utils.toSkillMap(ss)!! }
                            .map{ sm -> Skill(sm) }
                        _user.value = _user.value.apply{ this!!.skills = updatedSkills }
                    }
                }
            }
        }
    }

}
