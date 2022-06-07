package it.polito.mainactivity.model

import com.google.firebase.Timestamp

data class Chat(val chatId: String, val client: Map<String, Any>, var assigned: Boolean, var messages: MutableList<Message>){

    fun toMap(): HashMap<String, Any>{
        return hashMapOf(
            "chatId" to chatId,
            "client" to client,
            "assigned" to assigned
        )
    }

    constructor(chatMap: Map<String, Any>, messages: List<Map<String, Any>>, clientMap: Map<String, Any>): this(
        chatMap["chatId"] as String,
        clientMap,
        chatMap["assigned"] as Boolean,
        messages.map{ mm -> Message(mm) }.toMutableList()
    )

    // Used only when you need to update the value of assigned
    constructor(chatMap: Map<String, Any>): this(
        chatMap["chatId"] as String,
        hashMapOf(),
        chatMap["assigned"] as Boolean,
        mutableListOf()
    )
}

data class Message(val messageId: String, val text: String, val timestamp: Timestamp, val sender: Sender){

    enum class Sender {
        PUBLISHER, CLIENT, ERROR
    }

    fun toMap(): HashMap<String, Any>{
        return hashMapOf(
            "messageId" to messageId,
            "text" to text,
            "timestamp" to timestamp,
            "sender" to sender
        )
    }

    constructor(messageMap: Map<String, Any>): this(
        messageMap["messageId"] as String,
        messageMap["text"] as String,
        // TODO fix - handle String to Timestamp conversion
        messageMap["timestamp"] as Timestamp,
        when(messageMap["sender"]) {
            is String -> messageMap["sender"].let{ Sender.valueOf(it as String) }
            is Sender -> messageMap["sender"] as Sender
            else -> Sender.ERROR
        }
    )

}