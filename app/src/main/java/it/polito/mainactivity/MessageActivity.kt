package it.polito.mainactivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import it.polito.mainactivity.ui.request.Message

class MessageActivity : AppCompatActivity() {

    private val btnSend = findViewById<ImageButton>(R.id.btnSendMsg)
    private val textSend = findViewById<EditText>(R.id.textMsg)

    private var databaseReference: DatabaseReference = Firebase.database.reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)


    }

    private fun sendMessage() {
        btnSend.setOnClickListener {
            if (!textSend.text.toString().isEmpty()){
                sendData()
            }else{
                Toast.makeText(this, "Please enter a message", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /* Send data to firebase */
    private fun sendData(){
        databaseReference.child("messages").child(java.lang.String.valueOf(System.currentTimeMillis()))
            .setValue(Message(textSend.text.toString()))

        //clear the text
        textSend.setText("")
    }
}