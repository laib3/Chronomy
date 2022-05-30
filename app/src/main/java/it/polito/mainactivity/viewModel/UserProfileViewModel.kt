package it.polito.mainactivity.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import it.polito.mainactivity.model.Timeslot
import it.polito.mainactivity.model.User
import it.polito.mainactivity.model.emptyUser
import it.polito.mainactivity.model.Utils
import kotlinx.coroutines.runBlocking

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
                val userRef: DocumentReference = db.collection("users").document(userId)
                userRef.get().addOnSuccessListener {
                    // document doesn't exist
                    if (!it.exists()) {
                        userRef.set(emptyUser()).addOnSuccessListener {
                            Log.d("UserProfileViewModel", "publisher creation ok with id $userId")
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
                        }.addOnFailureListener { Log.d("UserProfileViewModel", "publisher not created") }
                    } else {
                        // if document exists
                        userListenerRegistration = userRef.addSnapshotListener { value, _ ->
                            if (value != null) {
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

    // update publisher field and publisher inside the timeslots
    fun updateUserField(field: String, newValue: Any?): Boolean{
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

    /** if timeslot is present in offers update, otherwise add it to the list **/
    fun updateOffers(timeslot: Timeslot){
        val offers = user.value?.offers
        var offer = offers?.find{ o -> o.timeslotId == timeslot.timeslotId }
        if(offer != null) { // update existing offer
            offer = timeslot.copy()
        }
        else { // not existing, add offer to offers
            offers?.add(timeslot.copy())
        }
        updateUserField("offers", offers)
    }

    /** remove offer from timeslots **/
    fun deleteOffer(timeslotId: String): Boolean{
        val offers = user.value?.offers?.filter{ o -> o.timeslotId != timeslotId }
        return updateUserField("offers", offers)
    }

    /** if timeslot is present in requests update, otherwise add it to the list **/
    fun updateRequests(timeslot: Timeslot){
        val requests = user.value?.requests
        var request = requests?.find{ r -> r.timeslotId == timeslot.timeslotId }
        if(request != null) { // update existing offer
            request = timeslot.copy()
        }
        else { // not existing, add offer to offers
            requests?.add(timeslot.copy())
        }
        updateUserField("requests", requests)
    }

    /** remove offer from timeslots **/
    fun deleteRequest(timeslotId: String): Boolean{
        val requests = user.value?.requests?.filter{ o -> o.timeslotId != timeslotId }
        return updateUserField("requests", requests)
    }

}