package it.polito.mainactivity.ui.home

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SearchView
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import it.polito.mainactivity.R
import it.polito.mainactivity.model.Timeslot
import it.polito.mainactivity.databinding.FragmentFilteredTimeslotListBinding
import it.polito.mainactivity.model.Utils
import it.polito.mainactivity.viewModel.TimeslotViewModel

class FilteredTimeslotListFragment : Fragment() {
    private val vm: TimeslotViewModel by activityViewModels()
    private var _binding: FragmentFilteredTimeslotListBinding? = null
    private var loadedList: List<Timeslot>? = null

    private val args: FilteredTimeslotListFragmentArgs by navArgs()

    private val binding get() = _binding!!
    var adapter: TimeslotsRecyclerViewAdapter? = null

    //results from filterFragment
    private var startDate: String? = null
    private var endDate: String? = null
    private var startTime: String? = null
    private var endTime: String? = null
    private var minDuration: String? = null
    private var maxDuration: String? = null

    private var sortingKeyPosition: Int = 0

    private var category: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFragmentResultListener("applyFilters") { _, bundle ->
            startDate = bundle.getString("startDate")
            endDate = bundle.getString("endDate")
            startTime = bundle.getString("startTime")
            endTime = bundle.getString("endTime")
            minDuration = bundle.getString("minDuration")
            maxDuration = bundle.getString("maxDuration")

            var filteredList = loadedList

            filteredList = applyFilters(
                startDate,
                endDate,
                startTime,
                endTime,
                minDuration,
                maxDuration,
                filteredList
            )
            adapter!!.filterList(filteredList)

            binding.filterButton.setBackgroundColor(resources.getColor(R.color.not_so_dark_slate_blue))
            Snackbar.make(binding.root, "Filters has been applied", Snackbar.LENGTH_LONG)
                .setTextColor(Color.GREEN)
                .show()
        }

        setFragmentResultListener("cleanFilters") { _, _ ->
            startDate = "dd/mm/yyyy"
            endDate = "dd/mm/yyyy"
            startTime = "hh:mm"
            endTime = "hh:mm"
            minDuration = "--h --m"
            maxDuration = "--h --m"
            adapter!!.filterList(loadedList!!)
            binding.filterButton.setBackgroundColor(Color.TRANSPARENT)
            Snackbar.make(binding.root, "Filters has been cleaned", Snackbar.LENGTH_LONG)
                .setTextColor(Color.GREEN)
                .show()
        }
    }

    override fun onResume() {
        super.onResume()
        val root: View = binding.root

        val sortingKeys =
            listOf("Date", "Title (A-Z)", "Title (Z-A)", "Duration (ASC)", "Duration (DES)")
        val sortingKeysArrayAdapter =
            ArrayAdapter(requireContext(), R.layout.list_item, sortingKeys)
        binding.tvSortBy.setAdapter(sortingKeysArrayAdapter)

        // TODO store position in a variable
        binding.tvSortBy.setText(
            sortingKeysArrayAdapter.getItem(sortingKeyPosition).toString(), false
        )

        val rv: RecyclerView = binding.timeslotListRv
        rv.layoutManager = LinearLayoutManager(root.context)

        category = args.category

        val filterButton = binding.filterButton
        filterButton.setOnClickListener {
            val bottomFragment = FiltersDialogFragment()
            val bundle = bundleOf(
                "startDate" to startDate,
                "endDate" to endDate,
                "startTime" to startTime,
                "endTime" to endTime,
                "minDuration" to minDuration,
                "maxDuration" to maxDuration
            )
            bottomFragment.arguments = bundle
            fragmentManager?.let { bottomFragment.show(it, "") }
        }

        binding.tvSortBy.onItemClickListener = AdapterView.OnItemClickListener { _, _, idx, _ ->
            loadedList = vm.timeslots.value!!
                .filter { it.category.lowercase() == category.lowercase() }
                .filter { it.publisher.get("userId") != FirebaseAuth.getInstance().uid } // display only other users' timeslots

            loadedList = applyFilters(
                startDate,
                endDate,
                startTime,
                endTime,
                minDuration,
                maxDuration,
                loadedList
            )

            sortingKeyPosition = idx
            loadedList = applySorting(loadedList, sortingKeys[sortingKeyPosition])

            adapter = TimeslotsRecyclerViewAdapter(
                loadedList!!,
                this,
            )
            rv.adapter = adapter
        }

        vm.timeslots.observe(viewLifecycleOwner) {
            loadedList = vm.timeslots.value!!
                .filter { it.category.lowercase() == category.lowercase() }
                .filter { it.publisher.get("userId") != FirebaseAuth.getInstance().uid } // display only other users' timeslots

            loadedList = applyFilters(
                startDate,
                endDate,
                startTime,
                endTime,
                minDuration,
                maxDuration,
                loadedList
            )

            loadedList = applySorting(loadedList, binding.tvSortBy.text.toString())

            adapter = TimeslotsRecyclerViewAdapter(
                loadedList!!,
                this,
            )
            rv.adapter = adapter
        }

        val search = binding.searchView
        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                search.clearFocus()
                val filteredList = loadedList!!.filter {
                    it.title.lowercase().contains(query!!.lowercase() as CharSequence)
                }
                adapter!!.filterList(filteredList)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val filteredList = loadedList!!.filter {
                    it.title.lowercase().contains(newText!!.lowercase() as CharSequence)
                }
                adapter!!.filterList(filteredList)
                return false
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFilteredTimeslotListBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun applySorting(
        timeslotList: List<Timeslot>?,
        sortingKey: String
    ): List<Timeslot> {
        return when (sortingKey) {
            "Date" -> {
                timeslotList!!.sortedBy { it.date }
            }
            "Title (A-Z)" -> {
                timeslotList!!.sortedBy { it.title }
            }
            "Title (Z-A)" -> {
                timeslotList!!.sortedByDescending { it.title }
            }
            "Duration (ASC)" -> {
                timeslotList!!.sortedBy {
                    Utils.durationInMinutes(
                        Utils.getDuration(
                            it.startHour,
                            it.endHour
                        )
                    )
                }
            }
            "Duration (DES)" -> {
                timeslotList!!.sortedByDescending {
                    Utils.durationInMinutes(
                        Utils.getDuration(
                            it.startHour,
                            it.endHour
                        )
                    )
                }
            }
            else -> {
                timeslotList!!.sortedBy { it.date }
            }
        }
    }

    private fun applyFilters(
        minStartDate: String?, maxStartDate: String?,
        minStartTime: String?, maxEndTime: String?,
        minDuration: String?, maxDuration: String?,
        filteredList: List<Timeslot>?
    ): List<Timeslot> {
        var result = filteredList

        if (minStartDate != null && minStartDate != "dd/mm/yyyy") {
            val calendarStartDate = Utils.formatStringToDate(minStartDate)
            result = result?.filter { !it.date.before(calendarStartDate) }
        }

        if (maxStartDate != null && maxStartDate != "dd/mm/yyyy") {
            val calendarEndDate = Utils.formatStringToDate(maxStartDate)
            result = result?.filter { !it.date.after(calendarEndDate) }
        }


        if (minStartTime != null && minStartTime != "hh:mm") {
            result =
                result?.filter {
                    it.startHour >= Utils.formatTime(
                        minStartTime.split(":")[0].toInt(),
                        minStartTime.split(":")[1].toInt()
                    )
                }
        }
        if (maxEndTime != null && maxEndTime != "hh:mm") {
            result =
                result?.filter {
                    it.endHour <= Utils.formatTime(
                        maxEndTime.split(":")[0].toInt(),
                        maxEndTime.split(":")[1].toInt()
                    )
                }
        }

        if (minDuration != null && minDuration != "--h --m") {
            val minDurationInMinutes = Utils.durationInMinutes(minDuration)

            result = result?.filter {
                val duration = Utils.getDuration(it.startHour, it.endHour)
                val durationInMinutes = Utils.durationInMinutes(duration)
                durationInMinutes >= minDurationInMinutes
            }
        }
        if (maxDuration != null && maxDuration != "--h --m") {
            val maxDurationInMinutes = Utils.durationInMinutes(maxDuration)

            result = result?.filter {
                val duration = Utils.getDuration(it.startHour, it.endHour)
                val durationInMinutes = Utils.durationInMinutes(duration)
                durationInMinutes <= maxDurationInMinutes
            }
        }
        return result!!
    }
}