package it.polito.mainactivity.ui.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import it.polito.mainactivity.R
import it.polito.mainactivity.data.Timeslot
import it.polito.mainactivity.data.User
import it.polito.mainactivity.databinding.FragmentFilteredTimeslotListBinding
import it.polito.mainactivity.ui.timeslot.TimeslotViewModel
import java.util.*


class FilteredTimeslotListFragment : Fragment() {
   private val vm: TimeslotViewModel by activityViewModels()
    private var _binding: FragmentFilteredTimeslotListBinding? = null
    private var loadedList: List<Timeslot>? = null

    private val args: FilteredTimeslotListFragmentArgs by navArgs()

    private val binding get() = _binding!!
    var adapter:TimeslotsRecyclerViewAdapter?=null

    //private var columnCount = 1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFilteredTimeslotListBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val rv: RecyclerView = binding.timeslotListRv
        rv.layoutManager = LinearLayoutManager(root.context)

        val category = args.category

        val filterButton = binding.filterButton
        filterButton.setOnClickListener{
            var bottomFragment = FiltersDialogFragment()
            fragmentManager?.let { bottomFragment.show(it, "" ) }
        }

        vm.timeslots.observe(viewLifecycleOwner) {
            loadedList = vm.timeslots.value!!
                .filter { it.category.lowercase() == category }
                .sortedBy { it.startDate }
            adapter = TimeslotsRecyclerViewAdapter(
                loadedList!!,
                this
            )
            rv.adapter = adapter
        }

        val search = binding.searchView
        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                search.clearFocus()
                val filteredList = loadedList!!.filter{it.title.lowercase().contains(query!!.lowercase() as CharSequence)}
                adapter!!.filterList(filteredList)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val filteredList = loadedList!!.filter{it.title.lowercase().contains(newText!!.lowercase() as CharSequence)}
                adapter!!.filterList(filteredList)
                return false
            }
        })
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}