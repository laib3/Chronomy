package it.polito.mainactivity.ui.chat

import android.app.Activity
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import it.polito.mainactivity.databinding.FragmentChatListBinding
import it.polito.mainactivity.model.*
import it.polito.mainactivity.viewModel.TimeslotViewModel


class ChatFragment : Fragment() {
    private val vm: TimeslotViewModel by activityViewModels()
    private var _binding: FragmentChatListBinding? = null
    private val binding get() = _binding!!
    var adapter: MessageRecyclerViewAdapter? = null

    var ts: Timeslot? = null
    var chat: Chat? = null

    private val args: ChatFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChatListBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val rv: RecyclerView = binding.list
        rv.layoutManager = LinearLayoutManager(root.context)


        vm.timeslots.observe(viewLifecycleOwner) {
            ts = vm.timeslots.value?.find { it.timeslotId == args.timeslotId }
            chat =
                if(args.chatId != "new" ) ts?.chats?.find { it.chatId == args.chatId }
            else ts!!.chats.firstOrNull { chat -> chat.client["userId"] == FirebaseAuth.getInstance().currentUser!!.uid }
            adapter = MessageRecyclerViewAdapter(
                chat!!.messages, chat!!, ts!!, this
            )
            rv.adapter = adapter
            rv.scrollToPosition(chat!!.messages.size - 1);

            when (ts!!.status) {
                Timeslot.Status.PUBLISHED -> {
                    binding.btnsManageReq.visibility = View.VISIBLE
                    binding.ratingZone.visibility = View.GONE
                }
                Timeslot.Status.COMPLETED -> {
                    binding.ratingZone.visibility = View.VISIBLE
                    binding.textMsg.visibility = View.GONE
                    binding.btnSendMsg.visibility = View.GONE
                    binding.btnsManageReq.visibility = View.GONE
                }
                Timeslot.Status.ASSIGNED -> {
                    binding.btnsManageReq.visibility = View.GONE
                    binding.ratingZone.visibility = View.GONE
                }
                else -> {}
            }
            if(Utils.getUserRole(ts!!, chat!!) == Message.Sender.CLIENT)
                binding.btnsManageReq.visibility = View.GONE
        }

        binding.btnSendMsg.setOnClickListener {
            vm.addMessage(args.chatId, binding.textMsg.text.toString())
            binding.textMsg.setText("")
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}