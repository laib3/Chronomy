package it.polito.mainactivity.ui.request

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.google.android.material.tabs.TabLayout
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import it.polito.mainactivity.databinding.FragmentRequestsListBinding
import it.polito.mainactivity.model.Chat
import it.polito.mainactivity.model.Message
import it.polito.mainactivity.model.Rating
import it.polito.mainactivity.model.Timeslot
import it.polito.mainactivity.model.User
import it.polito.mainactivity.viewModel.TimeslotViewModel
import java.util.*

class RequestsFragment : Fragment() {
    private val vm: TimeslotViewModel by activityViewModels()
    private var _binding: FragmentRequestsListBinding? = null
    private var loadedList: List<Timeslot>? = null
    private var shownList: List<Timeslot>? = null

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val binding get() = _binding!!
    var adapter: RequestRecyclerViewAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRequestsListBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val rv: RecyclerView = binding.list
        rv.layoutManager = LinearLayoutManager(root.context)

        val tabLayout: TabLayout = binding.tabLayout
/*
        vm.timeslots.observe(viewLifecycleOwner) {
            loadedList = vm.timeslots.value!!

            //selected tab is "request received"
            shownList = if (tabLayout.getTabAt(0)!!.isSelected) {
                loadedList!!.filter { t -> t.publisher["userId"] == auth.currentUser!!.uid && t.chats.size > 0 }
            } else {
                loadedList!!.filter { t -> t.publisher["userId"] != auth.currentUser!!.uid && t.chats.any { c -> c.client["userId"] == auth.currentUser!!.uid } }
            }

            adapter = RequestRecyclerViewAdapter(
                shownList!!,
                this,
            )
            rv.adapter = adapter
        }
*/

//**************************************

        val u1 = User("user1", "nome", "cognome", "nickname", "bio", "email", "location", "phone", listOf(),5, null)
        val u2 = User("user2", "nome", "cognome", "nickname", "bio", "email", "location", "phone", listOf(),5, null)
        val u3 = User("user3", "nome", "cognome", "nickname", "bio", "email", "location", "phone", listOf(),5, null)

        loadedList = listOf(
            Timeslot(
                "t1", "prova1", "desc",
                GregorianCalendar(2022, 2, 3),
                "18:00", "21:00", "location",
                "Delivery", u1.toMap(),
                Timeslot.Status.PUBLISHED,
                mutableListOf<it.polito.mainactivity.model.Chat>(
                    Chat("chat1", u2.toMap(), false, mutableListOf<Message>(
                        Message("message1", "ciao", Timestamp(Date()), Message.Sender.CLIENT ),
                        Message("message2", "cosa vuoi", Timestamp(Date()), Message.Sender.PUBLISHER ),
                    )),
                    Chat("chat2", u3.toMap(), false, mutableListOf<Message>(
                        Message("message3", "ciao", Timestamp(Date()), Message.Sender.CLIENT ),
                        Message("message4", "cosa vuoi", Timestamp(Date()), Message.Sender.PUBLISHER ),
                        ))
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
                    Chat("chat3", u1.toMap(), false, mutableListOf<Message>(
                        Message("message5", "ciao", Timestamp(Date()), Message.Sender.CLIENT ),
                        Message("message6", "cosa vuoi", Timestamp(Date()), Message.Sender.PUBLISHER ),

                        )),
                    Chat("chat4", u3.toMap(), false, mutableListOf<Message>(
                        Message("message7", "ciao", Timestamp(Date()), Message.Sender.CLIENT ),
                        Message("message8", "cosa vuoi", Timestamp(Date()), Message.Sender.PUBLISHER ),
                        ))
                ), mutableListOf<Rating>()
            )
        )

        shownList = if (tabLayout.getTabAt(0)!!.isSelected) {
            loadedList!!.filter { t -> t.publisher["userId"] == "user1" && t.chats.size > 0 }
        } else {
            loadedList!!.filter { t -> t.publisher["userId"] == "user2" && t.chats.any { c -> c.client["userId"] == "user1" } }
        }

        adapter = RequestRecyclerViewAdapter(
            shownList!!,
            this,
        )
        rv.adapter = adapter
        //*************************************

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

            override fun onTabSelected(tab: TabLayout.Tab?) {
                /*
                shownList =
                    if (tab?.position == 0) {
                        // show current user's timeslot received requests
                        loadedList!!.filter { t -> t.publisher["userId"] == auth.currentUser!!.uid && t.chats.size > 0 }
                    } else {
                        // show current user's requests made to other users
                        loadedList!!.filter { t -> t.publisher["userId"] != auth.currentUser!!.uid && t.chats.any { c -> c.client["userId"] == auth.currentUser!!.uid } }
                    }
                 */
                shownList =
                    if (tab?.position == 0) {
                        // show current user's timeslot received requests
                        loadedList!!.filter { t -> t.publisher["userId"] == "user1" && t.chats.size > 0 }
                    } else {
                        // show current user's requests made to other users
                        loadedList!!.filter { t -> t.publisher["userId"] == "user2" && t.chats.any { c -> c.client["userId"] == "user1" } }
                    }
                adapter?.filterList(shownList!!)
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {}

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
        })

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}