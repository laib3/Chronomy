package it.polito.mainactivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class MessageActivity : AppCompatActivity() {

    private lateinit var btnSend:ImageButton
    private lateinit var  textSend: EditText

    private val databaseReference: FirebaseFirestore = FirebaseFirestore.getInstance()
 //   private var databaseReference: DatabaseReference = Firebase.database.reference
/*
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)
        btnSend = findViewById(R.id.btnSendMsg)
        textSend = findViewById(R.id.textMsg)

        btnSend.setOnClickListener { sendMessage() }

    }

    private fun sendMessage() {
        btnSend.setOnClickListener {
            if (!textSend.text.toString().isEmpty()){
                sendData(textSend.text.toString())
            }else{
                Toast.makeText(this, "Please enter a message", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /* Send data to firebase */
    private fun sendData(text : String){
        val msg = hashMapOf(
            "timestamp" to java.lang.String.valueOf(System.currentTimeMillis()),
            "message" to text
        )
        databaseReference.collection("chat")
            .document()
            .set(msg)
            .addOnSuccessListener {
                Log.d(
                    "TimeslotViewModel",
                    "New timeslot successfully saved "
                ) //success = true
            }
            .addOnFailureListener {
                Log.d(
                    "Firebase",
                    "Error: timeslot not saved correctly"
                ) //success = false
            }

        //clear the text
        textSend.setText("")
    }

 */
}