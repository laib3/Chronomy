package it.polito.mainactivity

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.google.firebase.firestore.FirebaseFirestore

class MainViewModel(application: Application): AndroidViewModel(application) {

    private val db: FirebaseFirestore

    init {
        db = FirebaseFirestore.getInstance()
    }

    // consider category
    // TODO filter date start
    // TODO filter date end
    // TODO filter hour start
    // TODO filter hour end
    // TODO filter duration min
    // TODO filter duration max
    // TODO search by string (and by category)
    // TODO close db (cancel subscription)
    // TODO update/add timeslot data
    // TODO update/add profile data

}