package it.polito.mainactivity.model

import com.google.firebase.Timestamp

data class Chat(val requester: User, var assigned: Boolean, val messages: MutableList<Message>)

data class Message(val text: String, val timestamp: Timestamp, val sender: Sender){
    enum class Sender {
        PUBLISHER, CLIENT
    }
}