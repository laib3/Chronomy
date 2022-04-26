package it.polito.mainactivity.ui.timeslot_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import it.polito.mainactivity.TimeSlotItem
import it.polito.mainactivity.TimeSlotItemAdapter
import it.polito.mainactivity.databinding.FragmentTimeslotListBinding

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

        val timeSlotItems = createTimeSlotItems(1000)
        val adapter = TimeSlotItemAdapter(timeSlotItems)
        rv.adapter = adapter

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