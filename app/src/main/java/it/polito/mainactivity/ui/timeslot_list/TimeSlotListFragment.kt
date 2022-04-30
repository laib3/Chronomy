package it.polito.mainactivity.ui.timeslot_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import it.polito.mainactivity.R
import it.polito.mainactivity.Timeslot
import it.polito.mainactivity.TimeslotAdapter
import it.polito.mainactivity.databinding.FragmentTimeslotListBinding
import it.polito.mainactivity.ui.timeslot_edit.TimeSlotEditFragment
import java.util.*

class TimeSlotListFragment : Fragment() {

    private var _binding: FragmentTimeslotListBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    // TODO: remove
    private fun createTimeslots(n: Int): List<Timeslot> {
        /*val l = mutableListOf<Timeslot>()
        for (i in 1..n) {
            val i = Timeslot("title$i", "description$i", Date(2022, 2, 12), "09:00 AM", "10:00 PM", "Via qualcosa$i", "skill$i")
            l.add(i)
        }
        return l*/
        return listOf(
            Timeslot("Bring grocery shopping to your door",
                "I'll be happy to receive a list of goods to buy for you and to bring it back home to you."+
                        "I have a car so the quantity is not an issue. You can also select which supermarket you want me to go to,"+
                        " but please don't choose those outside of the neighbourhood.",
                Date(2022, 4, 25),
                "09:10 AM", "10:00 PM",
                "New Neighbourhood, Street 10, Sydney",
                "Delivery") ,

            Timeslot("Walk your dog",
                "I'll be happy to receive a list of dogs from you and to bring it back home to you."+
                        "I have a car so the quantity is not an issue. You can also select which supermarket you want me to go to,"+
                        " but please don't choose those outside of the neighbourhood.",
                Date(2022, 4, 25),
                "09:10 AM", "10:00 PM",
                "New City, Street 10, Anastasia",
                "Other"),

            Timeslot("Teach to your kid",
                "I'll be happy to receive a list of goods to buy for you and to bring it back home to you."+
                        "I have a car so the quantity is not an issue. You can also select which supermarket you want me to go to,"+
                        " but please don't choose those outside of the neighbourhood.",
                Date(2022, 4, 25),
                "09:10 AM", "10:00 PM",
                "New Neighbourhood, Street 10, Sydney",
                "Tutoring"),

            Timeslot("Bring grocery YOGA to your door",
                "I'll be happy to receive a list of goods to buy for you and to bring it back home to you."+
                        "I have a car so the quantity is not an issue. You can also select which supermarket you want me to go to,"+
                        " but please don't choose those outside of the neighbourhood.",
                Date(2022, 4, 25),
                "09:10 AM", "10:00 PM",
                "New Neighbourhood, Street 10, Sydney",
                "Wellness")

        )
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(TimeSlotListViewModel::class.java)

        _binding = FragmentTimeslotListBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //val textView: TextView = binding.textTimeslotList
        val rv: RecyclerView = binding.timeslotListRv
        rv.layoutManager = LinearLayoutManager(root.context)

        val timeslots = createTimeslots(100)
        val adapter = TimeslotAdapter(timeslots, this)
        rv.adapter = adapter

        // If the list of timeslots is empty, show a message
        val tv: TextView = binding.emptyTimeslotListMessage
        tv.visibility = if (adapter.itemCount == 0)
            View.VISIBLE
        else
            View.INVISIBLE


        // If click on fab, go to Edit timeslot
        val fab : FloatingActionButton = binding.fab

        var bundle = Bundle();
        bundle.putInt("id", -1)

        fab.setOnClickListener { findNavController().navigate(R.id.action_nav_list_to_nav_edit, bundle)}

        // TODO
        /*homeViewModel.text.observe(viewLifecycleOwner) {
            rv.text = it
        }
        é/
         */
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}