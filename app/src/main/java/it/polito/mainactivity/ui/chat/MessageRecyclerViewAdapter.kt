package it.polito.mainactivity.ui.chat

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.squareup.picasso.Picasso
import it.polito.mainactivity.R

import it.polito.mainactivity.model.Chat
import it.polito.mainactivity.model.Message
import it.polito.mainactivity.model.Timeslot

class MessageRecyclerViewAdapter(
    private val values: List<Message>,
    private val chat: Chat,
    private val timeslot: Timeslot,
    private val parentFragment: Fragment

) : RecyclerView.Adapter<MessageRecyclerViewAdapter.ViewHolder>() {

    private val loggedUserRole = Message.Sender.PUBLISHER

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        private val tvNickname:TextView = v.findViewById(R.id.nickname)
        private val tvMessage: TextView = v.findViewById(R.id.message)
        private val ivProfilePic:ImageView = v.findViewById(R.id.profilePic)

        fun bind(message: Message) {
            val user = if(message.sender == Message.Sender.PUBLISHER ){
                timeslot.publisher
            } else {
                chat.client
            }

            tvNickname.text = user["nickname"].toString()
            tvMessage.text = message.text
            if(user["profilePictureUrl"]!= null){
                Picasso.get().load(user["profilePictureUrl"] as String).into(ivProfilePic)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layout = when (viewType) {
            TYPE_SENT -> R.layout.fragment_message_sent
            TYPE_RECEIVED -> R.layout.fragment_message_received
            else -> throw IllegalArgumentException("Invalid type")
        }
        val view = LayoutInflater
            .from(parent.context)
            .inflate(layout, parent, false)

        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(values[position])
    }

    override fun getItemCount(): Int = values.size


    override fun getItemViewType(position: Int): Int {
        if(values[position].sender == loggedUserRole ){
            return TYPE_SENT
        }
        else if (values[position].sender != loggedUserRole){
            return TYPE_RECEIVED
        }
        return TYPE_ERROR
    }

    companion object {
        private const val TYPE_SENT = 0
        private const val TYPE_RECEIVED = 1
        private const val TYPE_ERROR = 2
    }

}