package it.polito.mainactivity.model

import com.google.firebase.Timestamp

data class Chat(val requester: User, var assigned: Boolean, val messages: MutableList<Message>){

    fun toMap(): HashMap<String, Any>{
        return hashMapOf(
            "assigned" to assigned
        )
    }

}

data class Message(val text: String, val timestamp: Timestamp, val sender: Sender){

    enum class Sender {
        PUBLISHER, CLIENT
    }

    fun toMap(): HashMap<String, Any>{
        return hashMapOf(
            "text" to text,
            "timestamp" to timestamp,
            "sender" to sender
        )
    }
}