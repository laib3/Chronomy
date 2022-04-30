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
import it.polito.mainactivity.TimeSlotItem
import it.polito.mainactivity.TimeSlotItemAdapter
import it.polito.mainactivity.databinding.FragmentTimeslotListBinding
import it.polito.mainactivity.ui.timeslot_edit.TimeSlotEditFragment

class TimeSlotListFragment : Fragment() {

    private var _binding: FragmentTimeslotListBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    // TODO: remove
    private fun createTimeSlotItems(n: Int): List<TimeSlotItem> {
        val l = mutableListOf<TimeSlotItem>()
        for (i in 1..n) {
            val i = TimeSlotItem("title$i", "location$i", "availability$i", "category$i")
            l.add(i)
        }
        return l
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

        val timeSlotItems = createTimeSlotItems(100)
        val adapter = TimeSlotItemAdapter(timeSlotItems, this)
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