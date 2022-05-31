package it.polito.mainactivity.ui

import android.content.ClipData
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import it.polito.mainactivity.R

class ChatActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_chat)

//    val username = intent.getStringExtra(NewMessageActivity.USER_KEY)
//    val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)

    supportActionBar?.title = "Utente 1"//user.username

    val adapter = GroupAdapter<RecyclerView.ViewHolder>()

    adapter.add(ChatFromItem())
    adapter.add(ChatToItem())
    adapter.add(ChatFromItem())
    adapter.add(ChatToItem())
    adapter.add(ChatFromItem())
    adapter.add(ChatToItem())
    adapter.add(ChatFromItem())
    adapter.add(ChatToItem())

        recyclerviewChat.adapter = adapter

  }
}


class ChatFromItem: ClipData.Item<RecyclerView.ViewHolder>() {
  fun bind(viewHolder: RecyclerView.ViewHolder, position: Int) {

  }

   fun getLayout(): Int {
    return R.layout.chat_from_row
  }
}

class ChatToItem: ClipData.Item<RecyclerView.ViewHolder>() {
    fun bind(viewHolder: RecyclerView.ViewHolder, position: Int) {

  }

  fun getLayout(): Int {
    return R.layout.chat_to_row
  }

}