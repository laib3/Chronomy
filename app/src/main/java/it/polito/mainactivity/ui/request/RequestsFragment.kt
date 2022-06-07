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
import com.google.firebase.auth.FirebaseAuth
import it.polito.mainactivity.databinding.FragmentRequestsListBinding
import it.polito.mainactivity.model.Timeslot
import it.polito.mainactivity.viewModel.TimeslotViewModel

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

        vm.timeslots.observe(viewLifecycleOwner) {
            loadedList = it
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

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

            override fun onTabSelected(tab: TabLayout.Tab?) {
                shownList =
                    if (tab?.position == 0) {
                        // show current user's timeslot received requests
                        loadedList?.filter { t -> t.publisher["userId"] == auth.currentUser!!.uid && t.chats.size > 0 }
                    } else {
                        // show current user's requests made to other users
                        loadedList?.filter { t -> t.publisher["userId"] != auth.currentUser!!.uid && t.chats.any { c -> c.client["userId"] == auth.currentUser!!.uid } }
                    }
                adapter?.filterList(shownList ?: emptyList())
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