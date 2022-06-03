package it.polito.mainactivity.ui.chat

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.google.firebase.Timestamp
import it.polito.mainactivity.R
import it.polito.mainactivity.databinding.FragmentChatListBinding
import it.polito.mainactivity.databinding.FragmentRequestsListBinding
import it.polito.mainactivity.model.*
import it.polito.mainactivity.placeholder.PlaceholderContent
import it.polito.mainactivity.ui.home.FilteredTimeslotListFragmentArgs
import it.polito.mainactivity.viewModel.TimeslotViewModel
import java.util.*

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

        /*
        vm.timeslots.observe(viewLifecycleOwner){
            ts =vm.timeslots.value?.find { it.timeslotId == args.timeslotId }
            chat = ts?.chats?.find { it.chatId == args.chatId }
            adapter = MessageRecyclerViewAdapter(
                chat!!.messages, chat!!, ts!!, this
            )
            rv.adapter = adapter
        }

         */
        val u1 = User(
            "user1",
            "nome",
            "cognome",
            "user1",
            "bio",
            "email",
            "location",
            "phone",
            listOf(),
            5,
            null
        )
        val u2 = User(
            "user2",
            "nome",
            "cognome",
            "user2",
            "bio",
            "email",
            "location",
            "phone",
            listOf(),
            5,
            null
        )
        val u3 = User(
            "user3",
            "nome",
            "cognome",
            "user3",
            "bio",
            "email",
            "location",
            "phone",
            listOf(),
            5,
            null
        )

        val loadedList = listOf(
            Timeslot(
                "t1", "prova1", "desc",
                GregorianCalendar(2022, 2, 3),
                "18:00", "21:00", "location",
                "Delivery", u1.toMap(),
                Timeslot.Status.PUBLISHED,
                mutableListOf<it.polito.mainactivity.model.Chat>(
                    Chat(
                        "chat1", u2.toMap(), false, mutableListOf<Message>(
                            Message(
                                "message1",
                                "ciao da u2",
                                Timestamp(Date()),
                                Message.Sender.CLIENT
                            ),
                            Message(
                                "message2",
                                "cosa vuoi da u1",
                                Timestamp(Date()),
                                Message.Sender.PUBLISHER
                            ),
                        )
                    ),
                    Chat(
                        "chat2", u3.toMap(), false, mutableListOf<Message>(
                            Message(
                                "message3",
                                "ciao da u3",
                                Timestamp(Date()),
                                Message.Sender.CLIENT
                            ),
                            Message(
                                "message4",
                                "cosa vuoi da u1",
                                Timestamp(Date()),
                                Message.Sender.PUBLISHER
                            ),
                        )
                    )
                ),
                mutableListOf<Rating>()
            ),
            Timeslot(
                "t2", "prova2", "desc",
                GregorianCalendar(2022, 2, 3),
                "18:00", "21:00", "location",
                "Delivery", u2.toMap(),
                Timeslot.Status.PUBLISHED,
                mutableListOf<it.polito.mainactivity.model.Chat>(
                    Chat(
                        "chat3", u1.toMap(), false, mutableListOf<Message>(
                            Message(
                                "message5",
                                "ciao da u1",
                                Timestamp(Date()),
                                Message.Sender.CLIENT
                            ),
                            Message(
                                "message6",
                                "cosa vuoi da u2",
                                Timestamp(Date()),
                                Message.Sender.PUBLISHER
                            ),

                            )
                    ),
                    Chat(
                        "chat4", u3.toMap(), false, mutableListOf<Message>(
                            Message(
                                "message7",
                                "ciao da u1",
                                Timestamp(Date()),
                                Message.Sender.CLIENT
                            ),
                            Message(
                                "message8",
                                "cosa vuoi da u3",
                                Timestamp(Date()),
                                Message.Sender.PUBLISHER
                            ),
                        )
                    )
                ), mutableListOf<Rating>()
            )
        )

        ts = loadedList.find { it.timeslotId == args.timeslotId }
        chat = ts!!.chats.find { it.chatId == args.chatId }
        adapter = MessageRecyclerViewAdapter(
            chat!!.messages, chat!!, ts!!, this
        )
        rv.adapter = adapter
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}