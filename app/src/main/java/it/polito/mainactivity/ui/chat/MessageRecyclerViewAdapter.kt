package it.polito.mainactivity.ui.chat

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import it.polito.mainactivity.R
import it.polito.mainactivity.placeholder.PlaceholderContent.PlaceholderItem
import it.polito.mainactivity.databinding.FragmentChatListBinding
import it.polito.mainactivity.databinding.FragmentMessageSentBinding
import it.polito.mainactivity.databinding.FragmentMessageReceivedBinding

// class MessageRecyclerViewAdapter(
//     private val values: List<PlaceholderItem>,
//     private val parentFragment: Fragment
//
// ) : RecyclerView.Adapter<MessageRecyclerViewAdapter.ViewHolder>() {
//
//
//    inner class MessageSentHolder(v:View):RecyclerView.ViewHolder(v) {
//        val tvNickname:TextView = v.findViewById(R.id.nickname)
//        val tvMessage: TextView = v.findViewById(R.id.message)
//        val ivProfilePic:ImageView = v.findViewById(R.id.profilePic)
//
//    }
//
//    inner class MessageReceivedHolder(v:View):RecyclerView.ViewHolder(v) {
//        val tvNickname:TextView = v.findViewById(R.id.nickname)
//        val tvMessage: TextView = v.findViewById(R.id.message)
//        val ivProfilePic:ImageView = v.findViewById(R.id.profilePic)
//    }
//
//    inner class ViewHolder(binding: FragmentChatBinding) : RecyclerView.ViewHolder(binding.root) {
//        val idView: TextView = binding.itemNumber
//        val contentView: TextView = binding.content
//
//        override fun toString(): String {
//            return super.toString() + " '" + contentView.text + "'"
//        }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//
//        return ViewHolder(
//            FragmentChatBinding.inflate(
//                LayoutInflater.from(parent.context),
//                parent,
//                false
//            )
//        )
//
//    }
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val item = values[position]
//        holder.idView.text = item.id
//        holder.contentView.text = item.content
//    }
//
//    override fun getItemCount(): Int = values.size
//
//
// }