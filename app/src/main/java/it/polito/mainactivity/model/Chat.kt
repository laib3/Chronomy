package it.polito.mainactivity.model

import com.google.firebase.Timestamp

data class Chat(val client: Map<String, String>, var assigned: Boolean, val messages: MutableList<Message>){

    fun toMap(): HashMap<String, Any>{
        return hashMapOf(
            "client" to client,
            "assigned" to assigned
        )
    }

    constructor(chatMap: Map<String, Any>, messages: List<Map<String, String>>, clientMap: Map<String, String>): this(
        clientMap,
        chatMap["assigned"] == "true",
        messages.map{ mm -> Message(mm) }.toMutableList()
    )
}

data class Message(val text: String, val timestamp: Timestamp, val sender: Sender){

    enum class Sender {
        PUBLISHER, CLIENT, ERROR
    }

    fun toMap(): HashMap<String, Any>{
        return hashMapOf(
            "text" to text,
            "timestamp" to timestamp,
            "sender" to sender
        )
    }

    constructor(messageMap: Map<String, String>): this(
        messageMap["text"] ?: "null",
        // TODO fix - handle String to Timestamp conversion
        Timestamp.now(),
        messageMap["sender"]?.let{ Sender.valueOf(it) } ?: Sender.ERROR
    )

}