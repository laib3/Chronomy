package it.polito.mainactivity.ui.timeslot.timeslot_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import it.polito.mainactivity.R
import it.polito.mainactivity.databinding.FragmentTimeslotListBinding
import it.polito.mainactivity.ui.timeslot.TimeslotViewModel

class TimeslotListFragment : Fragment() {

    private var _binding: FragmentTimeslotListBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val timeSlotListViewModel: TimeslotViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTimeslotListBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val rv: RecyclerView = binding.timeslotListRv
        rv.layoutManager = LinearLayoutManager(root.context)

        timeSlotListViewModel.timeslots.observe(viewLifecycleOwner) {
            val adapter = TimeslotAdapter(it, this)
            rv.adapter = adapter
            // If the list of timeslots is empty, show a message
            val tv: TextView = binding.emptyTimeslotListMessage
            tv.visibility = if (adapter.itemCount == 0)
                View.VISIBLE
            else
                View.INVISIBLE
        }

        // If click on fab, go to Edit timeslot
        val fab: FloatingActionButton = binding.fab
        val bundle = Bundle();
        bundle.putInt("id", -1)

        fab.setOnClickListener {
            findNavController().navigate(
                R.id.action_nav_list_to_nav_edit,
                bundle
            )
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}